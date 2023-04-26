package llm.poseconverter.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import llm.poseconverter.entity.User;
import llm.poseconverter.mapper.UserMapper;
import llm.poseconverter.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    // 获取配置文件中的private-key属性
    @Value("${private-key}")
    private String privateKey;

    @Override
    public User register(User userToAdd) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userToAdd.getUserName());
        if (userMapper.selectOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // Bcrypt加密密码
        String hashPassword = BCrypt.hashpw(userToAdd.getPassword(), BCrypt.gensalt());
        userToAdd.setPassword(hashPassword);
        userToAdd.setCreateAt(LocalDateTime.now());
        userToAdd.setUpdateAt(LocalDateTime.now());

        // 保存用户信息
        userMapper.insert(userToAdd);
        return userToAdd;
    }

    @Override
    public String login(String userName, String password) {
        // 验证用户名和密码是否正确
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        User user = userMapper.selectOne(wrapper);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 通过认证，生成token返回


        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }
}
