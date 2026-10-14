package com.example.usermanagement;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/** In-memory storage. Operations are atomic; callers receive detached snapshots. */
@Component
public class UserManager {
    private final Map<Integer, User> users = new LinkedHashMap<>();

    public synchronized boolean addUser(User user) {
        if (users.containsKey(user.getId())) {
            return false;
        }
        users.put(user.getId(), copy(user));
        return true;
    }

    public synchronized ArrayList<User> getAllUsers() {
        ArrayList<User> snapshot = new ArrayList<>();
        users.values().forEach(user -> snapshot.add(copy(user)));
        return snapshot;
    }

    public synchronized User getUserById(int id) {
        User user = users.get(id);
        return user == null ? null : copy(user);
    }

    public synchronized boolean deleteUserById(int id) {
        return users.remove(id) != null;
    }

    public synchronized boolean updateUser(int id, String name) {
        if (!users.containsKey(id)) {
            return false;
        }
        users.put(id, new User(id, name));
        return true;
    }

    private User copy(User user) {
        return new User(user.getId(), user.getName());
    }
}
