package wonbin.scheduler.Configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("인터셉터 실행 : {}", uri);

        // 세션 체크 예외 경로 목록
        if (uri.equals("/") || uri.equals("/index.html") || uri.equals("/login") || uri.equals("/signup") || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images")) {
            return true; // 예외 경로는 체크하지 않고 통과
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("session 확인 실패: {}", uri);
            response.sendError(401);
            return false;
        }
        return true;
    }

}
