package llm.poseconverter.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.Item;
import llm.poseconverter.entity.Directory;
import llm.poseconverter.entity.File;
import llm.poseconverter.service.MinioService;

@Service
public class MinioServiceImpl implements MinioService {

    @Value("${minio.endpoint}")
    private String endPoint;

    @Resource
    private MinioClient minioClient;

    @Override
    public String uploadFile(String bucketName, MultipartFile file) throws Exception {
        // 先检查Bucket是否存在
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        InputStream inputStream = file.getInputStream();
        long size = file.getSize();
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filename)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build());
        inputStream.close();

        return endPoint + "/" + bucketName + "/" + filename;
    }

    @Override
    public SaResult listFiles(String bucketName, String prefix) throws Exception {
        List<File> files = new ArrayList<>();
        List<Directory> directories = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(false).build());
    
        // 遍历结果，区分文件和目录
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.isDir()) {
                directories.add(new Directory(item.objectName()));
            } else {
                files.add(new File(item.objectName(), item.size(), item.lastModified(), 
                          endPoint + "/" + bucketName + prefix + "/" + item.objectName()));
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("files", files);
        map.put("directories", directories);
        return SaResult.data(map);
    }

    @Override
    public void deleteFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    @Override
    public SaResult searchFiles(String bucketName, String prefix, String keyword) throws Exception {
        List<File> files = new ArrayList<>();
        List<Directory> directories = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(false).build());
    
        // 遍历结果，区分文件和目录
        for (Result<Item> result : results) {
            Item item = result.get();
            if (!item.objectName().contains(keyword)) {
                continue;
            }
            if (item.isDir()) {
                directories.add(new Directory(item.objectName()));
            } else {
                files.add(new File(item.objectName(), item.size(), item.lastModified(), 
                          endPoint + "/" + bucketName + prefix + "/" + item.objectName()));
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("files", files);
        map.put("directories", directories);
        return SaResult.data(map);
    }

    @Override
    public void deleteFiles(String bucketName, List<String> objectNames) throws Exception {
        for (String objectName : objectNames) {
            deleteFile(bucketName, objectName);
        }
    }

    @Override
    public void addBucket(String bucketName) throws Exception {
        // 如果存在则不创建
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (isExist) {
            return;
        }
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        // 配置bucket访问权限为公共读
        String policyJson = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"s3:GetObject\",\"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"}]}";
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policyJson).build());
    }

    @Override
    public SaResult getBucketMetaData(String bucketName) throws Exception {
        Map<String, Object> map = new HashMap<>();
        // 计算bucket中文件数量和文件总大小
        long totalFileSize = 0L;
        int totalFileCount = 0;
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
        for (Result<Item> result : objectsIterator) {
            Item item = result.get();
            totalFileSize += item.size();
            totalFileCount++;
        }
        map.put("totalFileSize", totalFileSize);
        map.put("totalFileCount", totalFileCount);
        return SaResult.data(map);
    }
}
