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

        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(false).build());

        // 遍历结果，区分文件和目录
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.isDir()) {
                files.add(constructDirectory(item, bucketName, prefix));
            } else {
                // 如果objectName存在/，则获取objectName最后一个/后面的内容
                String objectName = item.objectName();
                if (objectName.contains("/")) {
                    char last = objectName.charAt(objectName.length() - 1);
                    objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
                    objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
                }
                files.add(new File(objectName, item.size(), item.lastModified(),
                        endPoint + "/" + bucketName + "/" + item.objectName(), false));
            }
        }

        return SaResult.data(files);
    }

    private File constructDirectory(Item item, String bucketName, String prefix) throws Exception {
        List<File> children = new ArrayList<>();

        String newPrefix = item.objectName();

        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(newPrefix).recursive(false).build());

        for (Result<Item> result : results) {
            Item childItem = result.get();
            if (childItem.isDir()) {
                children.add(constructDirectory(childItem, bucketName, newPrefix));
            } else {
                String objectName = childItem.objectName();
                if (objectName.contains("/")) {
                    char last = objectName.charAt(objectName.length() - 1);
                    objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
                    objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
                }
                children.add(new File(objectName, childItem.size(), childItem.lastModified(),
                        endPoint + "/" + bucketName + "/" + childItem.objectName(), false));
            }
        }
        String objectName = item.objectName();
        if (objectName.contains("/")) {
            char last = objectName.charAt(objectName.length() - 1);
            objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
            objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
        }
        return new Directory(objectName,
                endPoint + "/" + bucketName + "/" + item.objectName(), children);
    }

    private File constructDirectory(Item item, String bucketName, String prefix, String keyword) throws Exception {
        List<File> children = new ArrayList<>();
    
        String newPrefix = item.objectName();
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(newPrefix).recursive(false).build());
    
        for (Result<Item> result : results) {
            Item childItem = result.get();
            if (!childItem.objectName().contains(keyword)) {
                continue;
            }
            if (childItem.isDir()) {
                children.add(constructDirectory(childItem, bucketName, newPrefix, keyword));
            } else {
                String objectName = childItem.objectName();
                if (objectName.contains("/")) {
                    objectName = objectName.substring(0, objectName.length() - 1);
                    objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
                }
                children.add(new File(childItem.objectName(), childItem.size(), childItem.lastModified(),
                          endPoint + "/" + bucketName + newPrefix + "/" + childItem.objectName(), false));
            }
        }
        
        String objectName = item.objectName();
        if (objectName.contains("/")) {
            char last = objectName.charAt(objectName.length() - 1);
            objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
            objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
        }
        return new Directory(objectName,
                endPoint + "/" + bucketName + prefix + "/" + item.objectName(), children);
    }

    @Override
    public void deleteFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    @Override
    public SaResult searchFiles(String bucketName, String prefix, String keyword) throws Exception {
        List<File> files = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(false).build());
    
        // 遍历结果，区分文件和目录
        for (Result<Item> result : results) {
            Item item = result.get();
            if (!item.objectName().contains(keyword)) {
                continue;
            }
            if (item.isDir()) {
                files.add(constructDirectory(item, bucketName, prefix, keyword));
            } else {
                files.add(new File(item.objectName(), item.size(), item.lastModified(), 
                          endPoint + "/" + bucketName + prefix + "/" + item.objectName(), false));
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("files", files);
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
