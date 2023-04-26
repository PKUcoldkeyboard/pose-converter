package llm.poseconverter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.HashMap;

@SpringBootApplication
@MapperScan("llm.poseconverter.mapper")
public class PoseConverterApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PoseConverterApplication.class, args);
        System.out.println("启动成功, Sa-Token配置如下：" + SaManager.getConfig());
    }

}
