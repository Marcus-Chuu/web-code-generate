package com.marcuschu.webcodegenerate.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "langchain4j.community.redis.enabled=false")
@Transactional
class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Test
    void userRegisterTest() {
        String userAccount = "test" + System.nanoTime();
        long id = userService.userRegister(userAccount, "Yuna123456", "Yuna123456", "");
        assertTrue(id > 0);
    }

}
