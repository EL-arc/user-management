package com.example.usermanagement;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users;

    public UserManager() {
        users = new ArrayList<>();
    }

    public boolean addUser(User user){

        for(int i=0; i<users.size(); i++){
            if(users.get(i).getId() == user.getId()){
                return false;
            }

        }
        users.add(user);
        return true;


    }


    public ArrayList<User> getAllUsers() {
        return users;
    }

    public User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public boolean deleteUserById(int id) {
         for(int i = 0; i<users.size(); i++){
             if(users.get(i).getId() == id){
                 users.remove(i);
                 return true;
             }

         }
        return false;
    }

    public boolean updateUser(int id , String name){
        for(int i = 0; i<users.size(); i++){
           if(users.get(i).getId() == id){
               users.get(i).setName(name);
               return true;
           }
        }
        return false;
    }








}
