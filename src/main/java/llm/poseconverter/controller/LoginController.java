package llm.poseconverter.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.dto.LoginDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {
    @PostMapping("login")
    public SaResult login(@RequestBody LoginDto loginDto) {
        StpUtil.login(111);
        return SaResult.ok("登录成功!");
    }
}
