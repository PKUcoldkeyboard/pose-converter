package llm.poseconverter.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.dto.LoginDto;
import llm.poseconverter.service.UserService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {
    @Resource
    private UserService userService;

    @PostMapping("login")
    @ResponseBody
    public SaResult login(@RequestBody @Valid LoginDto loginDto) {
        Long userId = userService.login(loginDto.getUsername(), loginDto.getPassword());
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenInfo.getTokenValue());
        tokenMap.put("tokenHead", tokenInfo.getTokenName());
        return SaResult.data(tokenMap).setMsg("登录成功！");
    }

    @GetMapping("logout")
    @ResponseBody
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("注销成功！");
    }

    @GetMapping("test")
    @ResponseBody
    public SaResult test() {
        return SaResult.ok("hello world!");
    }
}
