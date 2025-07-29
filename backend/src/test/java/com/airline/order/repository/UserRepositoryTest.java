package com.airline.order.repository;

import com.airline.order.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository 单元测试
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setPassword("password1");
        testUser1.setRole("USER");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");
        testUser2.setRole("USER");

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpass");
        adminUser.setRole("ADMIN");

        // 保存测试数据
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(adminUser);
    }

    @Test
    void testFindByUsername_UserExists() {
        // 执行测试
        Optional<User> result = userRepository.findByUsername("testuser1");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("testuser1", result.get().getUsername());
        assertEquals("USER", result.get().getRole());
    }

    @Test
    void testFindByUsername_UserNotExists() {
        // 执行测试
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // 验证结果
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByRole() {
        // 执行测试
        List<User> userRoleUsers = userRepository.findByRole("USER");
        List<User> adminRoleUsers = userRepository.findByRole("ADMIN");

        // 验证结果
        assertEquals(2, userRoleUsers.size());
        assertEquals(1, adminRoleUsers.size());
        
        assertTrue(userRoleUsers.stream().allMatch(user -> "USER".equals(user.getRole())));
        assertTrue(adminRoleUsers.stream().allMatch(user -> "ADMIN".equals(user.getRole())));
    }

    @Test
    void testExistsByUsername_UserExists() {
        // 执行测试
        boolean exists = userRepository.existsByUsername("testuser1");

        // 验证结果
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_UserNotExists() {
        // 执行测试
        boolean exists = userRepository.existsByUsername("nonexistent");

        // 验证结果
        assertFalse(exists);
    }

    @Test
    void testFindByUsernameContaining() {
        // 执行测试
        List<User> result = userRepository.findByUsernameContaining("testuser");

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(user -> user.getUsername().contains("testuser")));
    }

    @Test
    void testFindByUsernameContaining_NoMatch() {
        // 执行测试
        List<User> result = userRepository.findByUsernameContaining("nomatch");

        // 验证结果
        assertEquals(0, result.size());
    }

    @Test
    void testFindByRoleAndUsername() {
        // 执行测试
        Optional<User> result = userRepository.findByRoleAndUsername("USER", "testuser1");
        Optional<User> noResult = userRepository.findByRoleAndUsername("ADMIN", "testuser1");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("testuser1", result.get().getUsername());
        assertEquals("USER", result.get().getRole());
        
        assertFalse(noResult.isPresent());
    }

    @Test
    void testCountByRole() {
        // 执行测试
        long userCount = userRepository.countByRole("USER");
        long adminCount = userRepository.countByRole("ADMIN");
        long nonexistentCount = userRepository.countByRole("NONEXISTENT");

        // 验证结果
        assertEquals(2, userCount);
        assertEquals(1, adminCount);
        assertEquals(0, nonexistentCount);
    }

    @Test
    void testFindUsersWithOrders() {
        // 注意：这个测试需要有订单数据才能验证
        // 由于我们没有创建订单数据，这里只测试方法不会抛出异常
        
        // 执行测试
        List<User> result = userRepository.findUsersWithOrders();

        // 验证结果（没有订单数据，应该返回空列表）
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSaveUser() {
        // 准备测试数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setRole("USER");

        // 执行测试
        User savedUser = userRepository.save(newUser);

        // 验证结果
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("USER", savedUser.getRole());

        // 验证数据库中确实保存了
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("newuser", foundUser.get().getUsername());
    }

    @Test
    void testDeleteUser() {
        // 获取要删除的用户ID
        Long userId = testUser1.getId();

        // 执行删除
        userRepository.deleteById(userId);
        entityManager.flush();

        // 验证删除结果
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());

        // 验证其他用户仍然存在
        Optional<User> otherUser = userRepository.findById(testUser2.getId());
        assertTrue(otherUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        // 获取要更新的用户
        User userToUpdate = testUser1;
        String originalUsername = userToUpdate.getUsername();
        
        // 更新用户信息
        userToUpdate.setRole("ADMIN");
        User updatedUser = userRepository.save(userToUpdate);
        entityManager.flush();

        // 验证更新结果
        assertEquals(originalUsername, updatedUser.getUsername());
        assertEquals("ADMIN", updatedUser.getRole());

        // 从数据库重新查询验证
        Optional<User> reloadedUser = userRepository.findById(userToUpdate.getId());
        assertTrue(reloadedUser.isPresent());
        assertEquals("ADMIN", reloadedUser.get().getRole());
    }
}