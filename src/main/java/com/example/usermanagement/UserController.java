package com.example.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired

    private UserService userService;


    @GetMapping("/users/{id}")
    public Result getUser(@PathVariable int id) {
        User user = userService.getUserById(id);

        if(user == null) {
            Result result = new Result(404,"用户不存在",null);
            return result;
        }
        Result result = new Result(200,"success",user);

        return result;
    }
}