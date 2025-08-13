package airline.controller;

import airline.service.UserService;
import airline.dto.JwtResponse;
import airline.entity.User;
import airline.exception.ServiceException;
import airline.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制层
 *
 */
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public JwtResponse login(@RequestBody User user){
        try {
            log.info("登录{}",user);
            String jwt = userService.login(user);
            return  new JwtResponse(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }
}