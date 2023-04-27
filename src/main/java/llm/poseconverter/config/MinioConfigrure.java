package llm.poseconverter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.minio.MinioClient;

@Component
public class MinioConfigrure {
    @Value("${minio.endpoint}")
	private String endPoint;
	@Value("${minio.accessKey}")
	private String accessKey;
	@Value("${minio.secretKey}")
	private String secretKey;
    
    /**
     * 注入minio客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(endPoint)
            .credentials(accessKey, secretKey)
            .build();
    }
}
