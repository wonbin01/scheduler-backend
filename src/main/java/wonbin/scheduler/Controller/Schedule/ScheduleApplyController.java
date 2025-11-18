package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;
import wonbin.scheduler.Service.schedule.ScheduleApplyService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleApplyController {
    private final ScheduleApplyService scheduleApplyService;

    @GetMapping("/scheduleApplyPage")
    public ResponseEntity<?> checkSession(HttpSession session) { /// 로그인 여부 및 로그인 정보 받아옴
        return scheduleApplyService.checkSession(session);
    }

    @PostMapping("/schedule/apply")
    public ResponseEntity<?> applySchedule(@RequestBody ScheduleApplyInfo applying) {
        return scheduleApplyService.applySchedule(applying);
    }

    @DeleteMapping("/schedule/apply/{applyId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable long applyId) {
        return scheduleApplyService.deleteSchedule(applyId);
    }

    @GetMapping("/schedule/apply/{year}/{month}")
    public ResponseEntity<?> returnApplyList(@PathVariable int year, @PathVariable int month) {
        return scheduleApplyService.returnApplyList(year, month);
    }

    @PutMapping("schedule/apply/{applyId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable long applyId,
            @RequestBody ScheduleApplyInfo info) {
        // 1. applyId와 body의 applyId가 다르면 거부
        return scheduleApplyService.updateSchedule(applyId, info);
    }

    @GetMapping("/allowed-dates")
    public ResponseEntity<?> getAllowedDates() {
        return scheduleApplyService.getAllowedDates();
    }

    @DeleteMapping("/allowed-dates/{date}")
    public ResponseEntity<?> deleteAllowedDate(@PathVariable String date) {
        return scheduleApplyService.deleteAllowedDate(date);
    }

    @PostMapping("/allowed-dates/bulk")
    public ResponseEntity<Boolean> saveAllowedDates(@RequestBody List<String> dates) {
        return scheduleApplyService.saveAllowedDates(dates);
    }

}
