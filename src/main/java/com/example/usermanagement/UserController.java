package com.example.usermanagement;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<List<User>> getAllUsers() {
        return new Result<>(200, "success", userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable int id) {
        return new Result<>(200, "success", userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<Result<User>> addUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.addUser(request.id(), request.name());
        return ResponseEntity.created(URI.create("/users/" + user.getId()))
                .body(new Result<>(201, "success", user));
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable int id, @Valid @RequestBody UpdateUserRequest request) {
        return new Result<>(200, "success", userService.updateUser(id, request.name()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return new Result<>(200, "success", null);
    }
}
