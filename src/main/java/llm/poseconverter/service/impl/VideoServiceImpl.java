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
import llm.poseconverter.service.MinioService;
import llm.poseconverter.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

    @Resource
    private MinioService minioService;

    @Override
    public SaResult convert(String bucketName, MultipartFile file) throws Exception {
        // 首先上传到Minio，得到视频url
        String videoUrl = minioService.uploadFile(bucketName, file);
        String url = "http://localhost:5000/video/detect/pose";
        String json = "{\"bucket_name\":\"" + bucketName + "\",\"video_url\":\"" + videoUrl + "\"}";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        SaResult result = restTemplate.exchange(url, HttpMethod.POST, entity, SaResult.class).getBody();
        return result;
    }
    
}
