package llm.poseconverter.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.dto.RegisterDto;
import llm.poseconverter.entity.User;
import llm.poseconverter.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Resource
    private UserService userService;
    
    @PostMapping("register")
    @ResponseBody
    public SaResult register(@RequestBody @Valid RegisterDto registerDto) {
        User user = userService.register(registerDto);
        user.setPassword(registerDto.getPassword());
        return SaResult.ok("注册成功！").setData(user);
    }
}
