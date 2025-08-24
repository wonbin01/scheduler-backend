package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.AllowedDate;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;
import wonbin.scheduler.Repository.Schedule.AllowedDateRepository;
import wonbin.scheduler.Repository.Schedule.JpaAllowedDateRepository;
import wonbin.scheduler.Repository.Schedule.JpaScheduleApplyRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleApplyRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleApplyController {

//    @Autowired
//    private final ScheduleApplyRepository scheduleRepository;
//    @Autowired
//    private final AllowedDateRepository allowedDateRepository;

    private final JpaScheduleApplyRepository jpaScheduleApplyRepository;
    private final JpaAllowedDateRepository jpaAllowedDateRepository;

    @GetMapping("/scheduleApplyPage") //이 부분 DTO로 변경하는 거 고려
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
        applying.setCreateAt(LocalDateTime.now());
        jpaScheduleApplyRepository.save(applying);
//        scheduleRepository.save(applying);
        return ResponseEntity.ok("스케줄 신청 완료");
    }

    @DeleteMapping("/schedule/apply/{applyId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable long applyId){
        jpaScheduleApplyRepository.deleteById(applyId);
//        scheduleRepository.delete(applyId);
        return ResponseEntity.ok(applyId+"삭제 완료");
    }

    @GetMapping("/schedule/apply/{year}/{month}")
    public ResponseEntity<?> returnApplyList(@PathVariable int year,@PathVariable int month){
//        List<ScheduleApplyInfo> list=scheduleRepository.findApplyUseMonth(year,month);
        List<ScheduleApplyInfo> list = jpaScheduleApplyRepository.findByApplyDateYearAndMonth(year,month);
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
//            scheduleRepository.update(info);
            info.setUpdatedAt(true);
            info.setCreateAt(LocalDateTime.now());
            jpaScheduleApplyRepository.save(info);
            return ResponseEntity.ok("업데이트 완료");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 스케줄이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    @GetMapping("/allowed-dates")
    public ResponseEntity<?> getAllowedDates(){
//        List<AllowedDate> allAllowedDate = allowedDateRepository.findAllAllowedDate();
        List<AllowedDate> allAllowedDate = jpaAllowedDateRepository.findAll();
        if(allAllowedDate.isEmpty()){
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(allAllowedDate);
    }

    @Transactional
    @DeleteMapping("/allowed-dates/{id}") // String으로 보내는것이 아니라 ALlowedDate Id를 보내도록
    public ResponseEntity<?> deleteAllowedDate(@PathVariable long id){
//        boolean isDeleted = allowedDateRepository.deleteAllowedDate(date); //true면 삭제, false면 삭제 실패
        Optional<AllowedDate> founded= jpaAllowedDateRepository.findById(id);
        jpaAllowedDateRepository.deleteById(id);
        Optional<AllowedDate> deleting = jpaAllowedDateRepository.findById(id);
        if(deleting.isPresent()){
            log.info("해당 날짜 삭제 실패 date={}",deleting.get().getDate());
        }
        log.info("해당 날짜 삭제 완료 date={}",founded);
        return ResponseEntity.ok("해당 날짜 삭제 완료 date={}");
    }
    @PostMapping("/allowed-dates/bulk") // 동일한 날짜를 저장하려는 경우 문제 발생
    public ResponseEntity<?> saveAllowedDates(@RequestBody List<String> dates) {
        List<AllowedDate> newDates = new ArrayList<>();

        for (String date : dates) {
            if (date == null || date.isEmpty()) continue; // null/빈 문자열 무시

            String normalizedDate;
            try {
                normalizedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).toString();
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format: " + date);
            }

            if (jpaAllowedDateRepository.findByDate(normalizedDate).isPresent()) continue;

            AllowedDate newDate = new AllowedDate();
            newDate.setDate(normalizedDate);
            newDates.add(newDate);
        }

        jpaAllowedDateRepository.saveAll(newDates);
        return ResponseEntity.ok(newDates);
    }

}
