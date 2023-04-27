package llm.poseconverter.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.Item;
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

        return endPoint + "/" + bucketName + "/" + filename;
    }

    @Override
    public List<Item> listFiles(String bucketName, String prefix) throws Exception {
        List<Item> results = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(true).build());
    
        for (Result<Item> result : objectsIterator) {
            results.add(result.get());
        }
        return results;
    }

    @Override
    public void deleteFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    @Override
    public List<Item> searchFiles(String bucketName, String prefix, String keyword) throws Exception {
        List<Item> results = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(true).build());
    
        for (Result<Item> result : objectsIterator) {
            Item item = result.get();
            String objectName = item.objectName();
            if (objectName.contains(keyword)) {
                results.add(item);
            }
        }
        return results;
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

}
