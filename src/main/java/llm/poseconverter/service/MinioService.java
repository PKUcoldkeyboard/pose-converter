package llm.poseconverter.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;

public interface MinioService {
    SaResult getBucketMetaData(String bucketName) throws Exception;
    String uploadFile(String bucketName, MultipartFile file) throws Exception;
    SaResult listFiles(String bucketName, String prefix) throws Exception;
    void deleteFile(String bucketName, String objectName) throws Exception;
    void deleteFiles(String bucketName, List<String> objectNames) throws Exception;
    void deleteDir(String bucketName, String prefix) throws Exception;
    SaResult searchFiles(String bucketName, String prefix, String keyword) throws Exception;
    void addBucket(String bucketName) throws Exception;
    void renameFile(String bucketName, String oldName, String newName) throws Exception;
    void renameDirectory(String bucketName, String oldName, String newName) throws Exception;
    ByteArrayOutputStream createZipOfDirectory(String bucketName, String prefix) throws Exception;
}
