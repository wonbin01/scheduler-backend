package wonbin.scheduler.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Entity.member.MemberInfo;

import java.util.Map;

@Slf4j
@RestController
public class ScheduleController {
    @GetMapping("/schedule")
    public ResponseEntity<?> checkSession(HttpSession session){
        return ResponseEntity.ok("schedule 페이지입니다");
    }
}
