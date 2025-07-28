package wonbin.scheduler.Controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomePageController {
    @GetMapping("/home")
    public ResponseEntity<?> checkSession(HttpSession session) {
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }
}

