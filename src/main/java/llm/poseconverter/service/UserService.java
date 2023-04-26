package llm.poseconverter.service;

import llm.poseconverter.entity.User;

public interface UserService {
    /*
     * 注册
     */
    User register(User userToAdd);

    /*
     * 登录，返回用户id
     */
    Long login(String username, String password);

    /*
    * 更新用户信息
     */
    User update(User user);
}
