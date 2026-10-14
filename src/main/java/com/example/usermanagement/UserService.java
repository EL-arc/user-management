package com.example.usermanagement;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserManager userManager;

    public UserService(UserManager userManager) {
        this.userManager = userManager;
    }

    public List<User> getAllUsers() {
        return userManager.getAllUsers();
    }

    public User getUserById(int id) {
        validateId(id);
        User user = userManager.getUserById(id);
        if (user == null) {
            throw notFound();
        }
        return user;
    }

    public User addUser(int id, String name) {
        validateId(id);
        User user = new User(id, normalizeName(name));
        if (!userManager.addUser(user)) {
            throw new UserOperationException(HttpStatus.CONFLICT, "用户 ID 已存在");
        }
        return user;
    }

    public User updateUser(int id, String name) {
        validateId(id);
        String normalizedName = normalizeName(name);
        if (!userManager.updateUser(id, normalizedName)) {
            throw notFound();
        }
        return new User(id, normalizedName);
    }

    public void deleteUserById(int id) {
        validateId(id);
        if (!userManager.deleteUserById(id)) {
            throw notFound();
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new UserOperationException(HttpStatus.BAD_REQUEST, "用户 ID 必须为正整数");
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank() || name.length() > 100) {
            throw new UserOperationException(HttpStatus.BAD_REQUEST, "姓名不能为空，且不能超过 100 个字符");
        }
        return name.strip();
    }

    private UserOperationException notFound() {
        return new UserOperationException(HttpStatus.NOT_FOUND, "用户不存在");
    }
}
