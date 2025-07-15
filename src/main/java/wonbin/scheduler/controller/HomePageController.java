package wonbin.scheduler.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.member.MemberInfo;

import java.util.Map;

@Slf4j
@RestController
public class HomePageController {
    @GetMapping("/home")
    public ResponseEntity<?> checkSession(HttpSession session){
        MemberInfo loginMember=(MemberInfo) session.getAttribute("loginMember");

        if(loginMember!=null){
            log.info("Session 확인, memberId={}",loginMember.getUsername());
            return ResponseEntity.ok(Map.of(
                    "id", loginMember.getUsername()
            ));
        }
        else
        {
            log.info("Session 확인 실패, 로그인 페이지로 이동");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }
    }
}
