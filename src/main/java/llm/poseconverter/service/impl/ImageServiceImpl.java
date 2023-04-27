package llm.poseconverter.service.impl;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.service.ImageService;
import llm.poseconverter.service.MinioService;

@Service
public class ImageServiceImpl implements ImageService{

    @Resource
    private MinioService minioService;
    
    @Override
    public SaResult convert(String bucketName, MultipartFile file) throws Exception {
        // 首先上传到minio
        String imageUrl = minioService.uploadFile(bucketName, file);

        String url = "http://localhost:5000/image/detect/pose";
        String json = "{\"bucket_name\":\"" + bucketName + "\",\"image_url\":\"" + imageUrl + "\"}";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        SaResult result = restTemplate.exchange(url, HttpMethod.POST, entity, SaResult.class).getBody();
        return result;
    }
}
