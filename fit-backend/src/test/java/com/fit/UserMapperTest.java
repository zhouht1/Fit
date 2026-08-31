package com.fit;

import com.fit.entity.User;
import com.fit.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsertAndSelect() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@fit.com");
        user.setPassword("password123");

        int result = userMapper.insert(user);
        assertEquals(1, result);
        assertNotNull(user.getId());

        User found = userMapper.selectById(user.getId());
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
        assertEquals("test@fit.com", found.getEmail());

        // Cleanup
        userMapper.deleteById(user.getId());
    }
}