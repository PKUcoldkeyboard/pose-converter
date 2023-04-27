package llm.poseconverter.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import llm.poseconverter.dto.RegisterDto;
import llm.poseconverter.dto.UpdateUserDto;
import llm.poseconverter.entity.User;
import llm.poseconverter.exception.CustomException;
import llm.poseconverter.mapper.UserMapper;
import llm.poseconverter.service.MinioService;
import llm.poseconverter.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private MinioService minioService;

    @Override
    public User register(RegisterDto registerDto) throws Exception {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", registerDto.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            throw new CustomException("用户名已存在");
        }

        // Bcrypt加密密码
        String hashPassword = BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt());

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(hashPassword);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 创建存储桶
        minioService.addBucket(user.getUsername());

        // 保存用户信息
        userMapper.insert(user);
        return user;
    }

    @Override
    public Long login(String username, String password) {
        // 验证用户名和密码是否正确
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new CustomException("用户名或密码错误");
        }
        // 通过认证
        return user.getId();
    }

    @Override
    public User update(Long id, UpdateUserDto updateUserDto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        if (updateUserDto.getPassword() != null) {
            user.setPassword(BCrypt.hashpw(updateUserDto.getPassword(), BCrypt.gensalt()));
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }
}
