package airline.interceptor;


import com.airline.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.airline.dto.JwtResponse;
import com.airline.exception.ServiceException;
import io.jsonwebtoken.Claims;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {


    @Autowired
    private JwtUtil jwtUtil;
    //目标资源方法执行前执行。 返回true：放行    返回false：不放行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1,先获取请求头
        String token = request.getHeader("Authorization");
        response.setContentType("application/json;charset = UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        //2,判断请求头是否存在
        if (token == null || "".equals(token)){
            //请求头不存在或者请求头为空
            log.info("token不存在");

            String result = mapper.writeValueAsString( new JwtResponse("Invalid token"));
            response.getWriter().write(result);
            throw new ServiceException();
        }
        //3,请求头不正确
        try {
            Claims claims = jwtUtil.parseToken(token);
            String id = claims.getId();
        }  catch (Exception e)  {
            log.info("请求头不正确!!");
            // Result error = Result.error(false, "NOT_LOGIN");
            String result = mapper.writeValueAsString(new JwtResponse("Invalid token"));
            response.getWriter().write(result);
            throw new ServiceException();
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle ... ");
    }

    //视图渲染完毕后执行，最后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion .... ");
    }
}