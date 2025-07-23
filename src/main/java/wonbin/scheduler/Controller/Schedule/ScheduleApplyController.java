package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;
import wonbin.scheduler.Repository.Schedule.MemoryScheduleApplyRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleApplyRepository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScheduleApplyController {

    private final ScheduleApplyRepository scheduleRepository;

    @GetMapping("/scheduleApplyPage")
    public ResponseEntity<?> checkSession(HttpSession session){ /// 로그인 여부 및 로그인 정보 받아옴
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }

    @PostMapping("/schedule/apply")
    public ResponseEntity<?> applySchedule(@RequestBody ScheduleApplyInfo applying){
        scheduleRepository.save(applying);
        return ResponseEntity.ok("스케줄 신청 완료");
    }

    @DeleteMapping("/schedule/apply/{applyId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable long applyId){
        scheduleRepository.delete(applyId);
        return ResponseEntity.ok(applyId+"삭제 완료");
    }

    @GetMapping("/schedule/apply/{year}/{month}")
    public ResponseEntity<?> returnApplyList(@PathVariable int year,@PathVariable int month){
        List<ScheduleApplyInfo> list=scheduleRepository.findApplyUseMonth(year,month);
        log.info("신청 데이터 전송 Month = {}",month);
        return ResponseEntity.ok(list);
    }

    @PutMapping("schedule/apply/{applyId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable long applyId,
            @RequestBody ScheduleApplyInfo info) {
        // 1. applyId와 body의 applyId가 다르면 거부
        if (info.getApplyId() != applyId) {
            return ResponseEntity.badRequest().body("applyId 불일치");
        }
        try {
            scheduleRepository.update(info);
            return ResponseEntity.ok("업데이트 완료");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 스케줄이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

}
