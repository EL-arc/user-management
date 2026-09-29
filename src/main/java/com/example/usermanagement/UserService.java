package com.example.usermanagement;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserManager userManager;



        public User getUserById(int id){
            return userManager.getUserById(id);
        }
    }




