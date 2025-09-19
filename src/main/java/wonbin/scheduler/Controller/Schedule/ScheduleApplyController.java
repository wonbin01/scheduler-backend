package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.AllowedDate;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;
import wonbin.scheduler.Repository.Schedule.AllowedDateRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleApplyRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleApplyController {

    @Autowired
    private final ScheduleApplyRepository scheduleRepository;
    @Autowired
    private final AllowedDateRepository allowedDateRepository;

    @GetMapping("/scheduleApplyPage")
    public ResponseEntity<?> checkSession(HttpSession session){ /// 로그인 여부 및 로그인 정보 받아옴
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
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

    @GetMapping("/allowed-dates")
    public ResponseEntity<?> getAllowedDates(){
        List<AllowedDate> allAllowedDate = allowedDateRepository.findAllAllowedDate();
        if(allAllowedDate.isEmpty()){
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(allAllowedDate);
    }

    @DeleteMapping("/allowed-dates/{date}")
    public ResponseEntity<?> deleteAllowedDate(@PathVariable String date){
        boolean isDeleted = allowedDateRepository.deleteAllowedDate(date); //true면 삭제, false면 삭제 실패
        if(!isDeleted){
            return ResponseEntity.status(404).body("삭제할 날짜가 존재하지 않습니다: " + date);
        }
        log.info("해당 날짜 삭제 완료 date={}",date);
        return ResponseEntity.ok("해당 날짜 삭제 완료 date={}");
    }

    @PostMapping("/allowed-dates/bulk")
    public ResponseEntity<Boolean> saveAllowedDates(@RequestBody List<String> dates) {
        boolean success = allowedDateRepository.saveAllowedDate(dates);
        return ResponseEntity.ok(success); // true=중복 없음, false=중복 있음
    }

}
