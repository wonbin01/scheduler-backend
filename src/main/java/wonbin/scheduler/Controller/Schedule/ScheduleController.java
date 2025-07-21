package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleInfo;
import wonbin.scheduler.Repository.Schedule.MemoryScheduleRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ScheduleController {

    ScheduleRepository scheduleRepository=new MemoryScheduleRepository();

    @GetMapping("/schedulePage")
    public ResponseEntity<?> checkSession(HttpSession session){ /// 로그인 여부 및 로그인 정보 받아옴
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }

    //TOdo 사용자 별로 update 및 삭제 가능하도록
    @PostMapping("/schedule/apply")
    public ResponseEntity<?> applySchedule(@RequestBody ScheduleInfo applying){
        scheduleRepository.save(applying);
        return ResponseEntity.ok("스케줄 신청 완료");
    }

    @GetMapping("/schedule/apply/{year}/{month}")
    public ResponseEntity<?> returnApplyList(@PathVariable int year,@PathVariable int month){
        List<ScheduleInfo> list=scheduleRepository.findApplyUseMonth(year,month);
        log.info("신청 데이터 전송 Month = {}",month);
        return ResponseEntity.ok(list);
    }

}
