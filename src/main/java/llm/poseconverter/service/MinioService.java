package llm.poseconverter.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.minio.messages.Item;

public interface MinioService {
    String uploadFile(String bucketName, MultipartFile file) throws Exception;
    List<Item> listFiles(String bucketName, String prefix) throws Exception;
    void deleteFile(String bucketName, String objectName) throws Exception;
    void deleteFiles(String bucketName, List<String> objectNames) throws Exception;
    List<Item> searchFiles(String bucketName, String prefix, String keyword) throws Exception;
    void addBucket(String bucketName) throws Exception;
}
