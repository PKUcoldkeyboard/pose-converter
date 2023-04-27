package llm.poseconverter.service;

import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.util.SaResult;

public interface ImageService {
    SaResult convert(MultipartFile file) throws Exception;
}
