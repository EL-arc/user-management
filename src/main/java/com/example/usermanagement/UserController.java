package com.example.usermanagement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserManager userManager = new UserManager();

    public UserController() {
        userManager.addUser(new User(1, "张三"));
        userManager.addUser(new User(2, "李四"));
    }

    @GetMapping("/users/{id}")
    public Result getUser(@PathVariable int id) {
        User user = userManager.getUserById(id);

        if(user == null) {
            Result result = new Result(404,"用户不存在",null);
            return result;
        }
        Result result = new Result(200,"success",user);

        return result;
    }
}