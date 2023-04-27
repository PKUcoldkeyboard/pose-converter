package llm.poseconverter.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.IoUtil;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;

/**
 * minio工具类
 */
@Component
public class MinioUtils {
    @Resource
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucketName}")
    private String bucketName;

    // 判断桶是否存在，不存在则创建
    public void existBucket(String name) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
        }
    }

    // 创建存储桶
    public boolean makeBucket(String bucketName) throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        return true;
    }

    // 删除存储桶
    public boolean removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        return true;
    }

    // 上传单个文件
    public String upload(MultipartFile multipartFile) throws Exception {
        String fileName = multipartFile.getOriginalFilename();
        String[] split = fileName.split("\\.");
        if (split.length > 1) {
            fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
        } else {
            fileName = fileName + System.currentTimeMillis();
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName)
                    .stream(inputStream, inputStream.available(), -1).contentType(multipartFile.getContentType())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endpoint + "/" + bucketName + "/" + fileName;
    }

    // 下载单个文件
    public ResponseEntity<byte[]> downloadObject(String fileName) throws Exception {
        ResponseEntity<byte[]> responseEntity = null;
        try(InputStream in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            IoUtil.copy(in, out);
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            headers.setContentLength(bytes.length);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setAccessControlExposeHeaders(Arrays.asList("*"));
            responseEntity = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseEntity;
    }

    // 删除文件对象
    public void delete(String fileName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }
}
