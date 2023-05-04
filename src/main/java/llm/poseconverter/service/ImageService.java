package llm.poseconverter.service;

import cn.dev33.satoken.util.SaResult;

public interface ImageService {
    SaResult convert(String bucketName, String imageName) throws Exception;
}
