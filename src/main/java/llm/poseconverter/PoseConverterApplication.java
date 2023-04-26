package llm.poseconverter;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("llm.poseconverter.mapper")
public class PoseConverterApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PoseConverterApplication.class, args);
        System.out.println("启动成功, Sa-Token配置如下：" + SaManager.getConfig());
    }

}
