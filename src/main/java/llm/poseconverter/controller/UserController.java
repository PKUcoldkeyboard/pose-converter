package llm.poseconverter.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.util.SaResult;
import llm.poseconverter.dto.RegisterDto;
import llm.poseconverter.dto.UpdateUserDto;
import llm.poseconverter.entity.User;
import llm.poseconverter.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Resource
    private UserService userService;
    
    @PostMapping("register")
    @ResponseBody
    public SaResult register(@RequestBody @Valid RegisterDto registerDto) throws Exception {
        User user = userService.register(registerDto);
        user.setPassword(registerDto.getPassword());
        return SaResult.ok("注册成功！").setData(user);
    }

    @PutMapping("users/{id}")
    @ResponseBody
    public SaResult update(@PathVariable("id") Long id, @RequestBody UpdateUserDto updateUserDto) {
        User user = userService.update(id, updateUserDto);
        user.setPassword(updateUserDto.getPassword());
        return SaResult.ok("修改成功！").setData(user);
    }
}
