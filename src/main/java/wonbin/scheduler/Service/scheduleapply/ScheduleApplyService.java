package wonbin.scheduler.Service.scheduleapply;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.AllowedDate;
import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;
import wonbin.scheduler.Repository.Schedule.AllowedDateRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleApplyRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleApplyService {
    private final ScheduleApplyRepository scheduleRepository;
    private final AllowedDateRepository allowedDateRepository;

    public ResponseEntity<?> checkSession(HttpSession session) {
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }

    public ResponseEntity<?> applySchedule(ScheduleApplyInfo applying) {
        scheduleRepository.save(applying);
        return ResponseEntity.ok("스케줄 신청 완료");
    }

    public ResponseEntity<?> deleteSchedule(long applyId) {
        scheduleRepository.delete(applyId);
        return ResponseEntity.ok(applyId + " 삭제 완료");
    }

    public ResponseEntity<?> returnApplyList(int year, int month) {
        List<ScheduleApplyInfo> applyInfo = scheduleRepository.findApplyUseMonth(year, month);
        log.info("신청 데이터 전송 Month = {}", month);
        return ResponseEntity.ok(applyInfo);
    }

    public ResponseEntity<?> updateSchedule(long applyId, ScheduleApplyInfo applyInfo) {
        if (applyInfo.getApplyId() != applyId) {
            return ResponseEntity.badRequest().body("applyId 불일치");
        }
        try {
            scheduleRepository.update(applyInfo);
            return ResponseEntity.ok("업데이트 완료");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 스케줄이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    public ResponseEntity<?> getAllowedDates() {
        List<AllowedDate> allAllowedDate = allowedDateRepository.findAllAllowedDate();
        if (allAllowedDate.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(allAllowedDate);
    }

    public ResponseEntity<?> deleteAllowedDate(String date) {
        boolean isDeleted = allowedDateRepository.deleteAllowedDate(date); //true면 삭제, false면 삭제 실패
        if (!isDeleted) {
            return ResponseEntity.status(404).body("삭제할 날짜가 존재하지 않습니다: " + date);
        }
        log.info("해당 날짜 삭제 완료 date={}", date);
        return ResponseEntity.ok("해당 날짜 삭제 완료 date={}");
    }

    public ResponseEntity<Boolean> saveAllowedDates(List<String> dates) {
        boolean success = allowedDateRepository.saveAllowedDate(dates);
        return ResponseEntity.ok(success); // true=중복 없음, false=중복 있음
    }
}
