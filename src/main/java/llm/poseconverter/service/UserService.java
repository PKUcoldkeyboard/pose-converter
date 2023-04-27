package llm.poseconverter.service;

import llm.poseconverter.dto.RegisterDto;
import llm.poseconverter.dto.UpdateUserDto;
import llm.poseconverter.entity.User;

public interface UserService {
    /*
     * 注册
     */
    User register(RegisterDto registerDto) throws Exception;

    /*
     * 登录，返回用户id
     */
    Long login(String username, String password);

    /*
    * 更新用户信息
     */
    User update(Long id, UpdateUserDto updateUserDto);
}
