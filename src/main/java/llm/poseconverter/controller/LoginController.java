package llm.poseconverter.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.dto.LoginDto;
import llm.poseconverter.service.UserService;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {
    @Resource
    private UserService userService;

    @PostMapping("login")
    public SaResult login(@RequestBody LoginDto loginDto) {
        Long userId = userService.login(loginDto.getUsername(), loginDto.getPassword());
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return SaResult.data(tokenInfo).setMsg("登录成功！");
    }
}
