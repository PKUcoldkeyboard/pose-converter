package llm.poseconverter.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
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
                        endPoint + "/" + bucketName + "/" + item.objectName(), false, prefix));
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
                        endPoint + "/" + bucketName + "/" + childItem.objectName(), false, prefix));
            }
        }
        String objectName = item.objectName();
        if (objectName.contains("/")) {
            char last = objectName.charAt(objectName.length() - 1);
            objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
            objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
        }
        return new Directory(objectName,
                endPoint + "/" + bucketName + "/" + item.objectName(), children, prefix);
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
                children.add(constructDirectory(childItem, bucketName, newPrefix));
            } else {
                String objectName = childItem.objectName();
                if (objectName.contains("/")) {
                    char last = objectName.charAt(objectName.length() - 1);
                    objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
                    objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
                }
                children.add(new File(objectName, childItem.size(), childItem.lastModified(),
                        endPoint + "/" + bucketName + "/" + childItem.objectName(), false, prefix));
            }
        }
        String objectName = item.objectName();
        if (objectName.contains("/")) {
            char last = objectName.charAt(objectName.length() - 1);
            objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
            objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
        }
        return new Directory(objectName,
                endPoint + "/" + bucketName + "/" + item.objectName(), children, prefix);
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
                // 如果objectName存在/，则获取objectName最后一个/后面的内容
                String objectName = item.objectName();
                if (objectName.contains("/")) {
                    char last = objectName.charAt(objectName.length() - 1);
                    objectName = last == '/' ? objectName.substring(0, objectName.length() - 1) : objectName;
                    objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
                }
                files.add(new File(objectName, item.size(), item.lastModified(),
                        endPoint + "/" + bucketName + "/" + item.objectName(), false, prefix));
            }
        }
        return SaResult.data(files);
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

    @Override
    public void renameFile(String bucketName, String oldName, String newName) throws Exception {
        // 复制对象并重命名
        CopyObjectArgs args = CopyObjectArgs.builder()
                                .source(CopySource.builder().bucket(bucketName).object(oldName).build())
                                .bucket(bucketName)
                                .object(newName)
                                .build();
        minioClient.copyObject(args);
        // 删除原对象
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(oldName).build());
    }

    @Override
    public void renameDirectory(String bucketName, String oldName, String newName) throws Exception {
        // 列出源目录下的所有对象，并复制它们到目标目录中
        List<String> objectNames = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(oldName).recursive(true).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            String oldObjectName = item.objectName();
            objectNames.add(oldObjectName);
            String newObjectName = item.objectName().replace(oldName, newName);
            CopyObjectArgs args = CopyObjectArgs.builder()
                                    .source(CopySource.builder().bucket(bucketName).object(oldObjectName).build())
                                    .bucket(bucketName)
                                    .object(newObjectName)
                                    .build();
            minioClient.copyObject(args);
        }
        // 删除原目录
        deleteFiles(bucketName, objectNames);
    }

    @Override
    public void deleteDir(String bucketName, String prefix) throws Exception {
        List<String> objectNames = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(true).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            objectNames.add(item.objectName());
        }
        deleteFiles(bucketName, objectNames);
    }

    @Override
    public ByteArrayOutputStream createZipOfDirectory(String bucketName, String prefix) throws Exception {
        List<String> objectNames = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(true).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            objectNames.add(item.objectName());
        }

        // 创建一个 ByteArrayOutputStream 用于存储 ZIP 压缩文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (String objectName : objectNames) {
            // 从minio获取文件
            InputStream filStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            // 文件添加到ZIP压缩文件
            ZipEntry zipEntry = new ZipEntry(objectName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = filStream.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            zos.closeEntry();
            filStream.close();
        }
        zos.close();
        return baos;
    }
}
