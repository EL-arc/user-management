package com.example.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);

        UserManager manager = new UserManager();

        System.out.println(manager.addUser(new User(1, "张三")));
        System.out.println(manager.addUser(new User(2, "李四")));

        System.out.println(manager.getAllUsers());
    }
}