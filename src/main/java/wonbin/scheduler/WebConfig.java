package wonbin.scheduler;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class WebConfig  implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowCredentials(true)
                .allowedMethods("*");
    }

    @Component
    @WebFilter(urlPatterns = "/session/*")
    @Slf4j
    public static class LoginCheckFilter implements Filter {
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res=(HttpServletResponse) response;
            String uri=req.getRequestURI();

            if(uri.equals("/signup") || uri.equals("/login") || uri.startsWith("/css") || uri.startsWith("/js")){
                chain.doFilter(request,response);
                return;
            }

            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("loginMember") == null) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401
                log.info("접근 실패. 로그인 페이지로 이동");
                return;
            }

            chain.doFilter(request, response); // 통과
        }
    }

}
