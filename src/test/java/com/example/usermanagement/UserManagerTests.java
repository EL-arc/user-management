package com.example.usermanagement;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTests {
    @Test
    void callersCannotMutateStoredUsersOrCollections() {
        UserManager manager = new UserManager();
        User original = new User(1, "张三");
        manager.addUser(original);
        original.setId(99);
        original.setName("外部修改");
        manager.getUserById(1).setName("外部修改");
        ArrayList<User> snapshot = manager.getAllUsers();
        snapshot.get(0).setId(100);
        snapshot.clear();
        assertEquals("张三", manager.getUserById(1).getName());
        assertEquals(1, manager.getAllUsers().size());
        assertNull(manager.getUserById(99));
    }

    @Test
    void concurrentCreationOfTheSameIdSucceedsExactlyOnce() throws Exception {
        UserManager manager = new UserManager();
        var executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Callable<Boolean>> tasks = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                tasks.add(() -> {
                    assertTrue(start.await(5, TimeUnit.SECONDS));
                    return manager.addUser(new User(1, "张三"));
                });
            }
            var futures = tasks.stream().map(executor::submit).toList();
            start.countDown();
            int successfulCreates = 0;
            for (var future : futures) {
                if (future.get(5, TimeUnit.SECONDS)) {
                    successfulCreates++;
                }
            }
            assertEquals(1, successfulCreates);
            assertEquals(1, manager.getAllUsers().size());
        } finally {
            executor.shutdownNow();
        }
    }
}
