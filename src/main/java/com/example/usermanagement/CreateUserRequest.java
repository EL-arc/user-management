package com.example.usermanagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotNull(message = "用户 ID 不能为空")
        @Positive(message = "用户 ID 必须为正整数") Integer id,
        @NotBlank(message = "姓名不能为空")
        @Size(max = 100, message = "姓名不能超过 100 个字符") String name) {
}
