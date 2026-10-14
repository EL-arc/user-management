package com.example.usermanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserManager manager;

    @BeforeEach
    void clearUsers() {
        manager.getAllUsers().forEach(user -> manager.deleteUserById(user.getId()));
    }

    @Test
    void completeUserLifecycle() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data", hasSize(0)));
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"  张三  \"}"))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/users/1"))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.id").value(1)).andExpect(jsonPath("$.data.name").value("张三"));
        mvc.perform(get("/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("张三"));
        mvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"李四\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("李四"));
        mvc.perform(get("/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("李四"));
        mvc.perform(delete("/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200)).andExpect(jsonPath("$.data").value(nullValue()));
        mvc.perform(get("/users/1")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404)).andExpect(jsonPath("$.message").value("用户不存在"));
        mvc.perform(get("/users")).andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void duplicateIdDoesNotOverwriteExistingUser() throws Exception {
        manager.addUser(new User(1, "张三"));
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"李四\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value(409));
        mvc.perform(get("/users/1")).andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void missingUsersReturn404ForAllOperations() throws Exception {
        mvc.perform(get("/users/9")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404));
        mvc.perform(put("/users/9").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"张三\"}"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404));
        mvc.perform(delete("/users/9")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404));
        mvc.perform(get("/users")).andExpect(jsonPath("$.data", hasSize(0)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"id\":1}", "{\"name\":\"张三\"}",
            "{\"id\":0,\"name\":\"张三\"}", "{\"id\":-1,\"name\":\"张三\"}",
            "{\"id\":1,\"name\":\"   \"}", "{\"id\":1,\"name\":null}",
            "{\"id\":1.5,\"name\":\"张三\"}", "{\"id\":2147483648,\"name\":\"张三\"}",
            "{\"id\":\"abc\",\"name\":\"张三\"}", "null", "{broken", "[]"})
    void invalidCreateRequestsDoNotSaveUsers(String json) throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data").value(nullValue()));
        mvc.perform(get("/users")).andExpect(jsonPath("$.data", hasSize(0)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "abc", "2147483648", "1.5"})
    void invalidPathIdsReturn400(String id) throws Exception {
        mvc.perform(get("/users/" + id)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
        mvc.perform(delete("/users/" + id)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
        mvc.perform(put("/users/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"张三\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null}", "{\"name\":\"   \"}", "null", "{broken"})
    void invalidUpdatesDoNotChangeExistingUser(String json) throws Exception {
        manager.addUser(new User(1, "张三"));
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
        mvc.perform(get("/users/1")).andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void nameLengthLimitIsEnforced() throws Exception {
        String maximumName = "张".repeat(100);
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"" + maximumName + "\"}"))
                .andExpect(status().isCreated());
        String tooLong = maximumName + "张";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2,\"name\":\"" + tooLong + "\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + tooLong + "\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/users/1")).andExpect(jsonPath("$.data.name").value(maximumName));
    }

    @Test
    void transportErrorsUseUnifiedResponses() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
        mvc.perform(post("/users").contentType(MediaType.TEXT_PLAIN).content("hello"))
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.code").value(415));
        mvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed()).andExpect(jsonPath("$.code").value(405))
                .andExpect(header().string("Allow", containsString("PUT")));
        mvc.perform(get("/unknown")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404));
    }
}
