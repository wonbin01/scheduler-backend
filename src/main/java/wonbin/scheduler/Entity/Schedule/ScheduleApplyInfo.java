package wonbin.scheduler.Entity.Schedule;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name="apply_info")
@Entity
public class ScheduleApplyInfo {
    private int usernumber;          // 신청한 사람 사번
    private String username;         // 신청한 사람 이름
    private String timeSlot;         // 신청 내용
    private LocalDate applyDate;     // 신청 날짜
    private String reason;           // 사유
    private String alternativePlan;  // 대체 방안
    private String etc;              // 기타

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long applyId;            //신청 아이디 지정
    private LocalDateTime createAt;      //신청 누른 시각
    private boolean updatedAt;       // 수정된 건지 여부 확인용
}
