package llm.poseconverter.service;

import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;

public interface VideoService {
    SaResult convert(String bucketName, MultipartFile file) throws Exception;
}
