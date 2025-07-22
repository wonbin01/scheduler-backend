package wonbin.scheduler.Entity.Schedule;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScheduleInfo {
    private int usernumber;          // 신청한 사람 사번
    private String username;         // 신청한 사람 이름
    private String timeSlot;
    private LocalDate applyDate;     // 신청 날짜
    private String reason;           // 사유
    private String alternativePlan;  // 대체 방안
    private String etc;              // 기타
    private long applyId;            //신청 아이디 지정
}
