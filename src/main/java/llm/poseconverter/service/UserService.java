package llm.poseconverter.service;

import llm.poseconverter.entity.User;

public interface UserService {
    /*
     * 注册
     */
    User register(User userToAdd);

    /*
     * 登录
     */
    String login(String userName, String password);

    /*
    * 更新用户信息
     */
    User update(User user);
}
