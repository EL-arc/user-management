package com.example.usermanagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "姓名不能为空")
        @Size(max = 100, message = "姓名不能超过 100 个字符") String name) {
}
