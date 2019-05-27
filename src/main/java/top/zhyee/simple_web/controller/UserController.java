package top.zhyee.simple_web.controller;

import rx.Single;
import top.zhyee.simple_web.entity.User;
import top.zhyee.simple_web.service.UserService;

/**
 * @author zhyee
 */
public class UserController {

    private final UserService userService;

    public UserController(UserService service) {
        userService = service;
    }

    public Single<Boolean> addUser(User user) {
        return userService.addUser(user);
    }

    public Single<User> getUser(String id) {
        return userService.getUser(id);
    }
}
