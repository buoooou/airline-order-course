package airline.service;

import com.airline.entity.User;
import com.airline.repository.UserRepository;
import com.airline.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public String login(User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (!optionalUser.isPresent()) {
            throw new SecurityException("user does not exist");
        }
        User user1 = optionalUser.get();
        if (!user1.getPassword().equals(user.getPassword()))
            throw new SecurityException("password is wrong");

        Map<String, String> map = new HashMap();
        map.put("id", String.valueOf(user.getId()));
        map.put("username", user.getUsername());
       String token =  jwtUtil.generateToken(user.getUsername(),map);
        return token;
    }
}
