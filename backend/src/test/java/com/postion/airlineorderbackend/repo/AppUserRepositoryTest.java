package com.postion.airlineorderbackend.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.postion.airlineorderbackend.entity.AppUser;
import com.postion.airlineorderbackend.repository.AppUserRepository;

@DataJpaTest // 自动配置JPA测试环境
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 禁用嵌入式数据库替换
public class AppUserRepositoryTest {

    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserRepositoryTest(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Test
    public void testSaveAndFindByIdAndDelete() {

        // 1. 准备测试数据
        AppUser user = new AppUser();
        user.setUsername("testUser");
        user.setPassword("<PASSWORD>");
        user.setRole("admin");

        // 2. 调用Repository方法（保存用户）
        AppUser savedUser = appUserRepository.save(user);
        AppUser foundUser = appUserRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        assertEquals("admin", foundUser.getRole());

        appUserRepository.delete(foundUser);
        AppUser delUser = appUserRepository.findById(savedUser.getId()).orElse(null);
        assertEquals(null, delUser);
    }

}
