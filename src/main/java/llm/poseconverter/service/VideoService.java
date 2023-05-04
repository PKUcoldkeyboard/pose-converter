package llm.poseconverter.service;

import cn.dev33.satoken.util.SaResult;

public interface VideoService {
    SaResult convert(String bucketName, String videoUrl) throws Exception;
}
