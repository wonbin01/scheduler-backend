package wonbin.scheduler.Entity.Schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleViewInfo {
    private int userNumber; //사번
    private String position; // 근무 포지션
    private LocalDate applyDate; //근무 날짜

    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime; // 출근 시간
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime; //퇴근 시간
}
