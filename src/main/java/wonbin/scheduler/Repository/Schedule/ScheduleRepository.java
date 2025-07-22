package wonbin.scheduler.Repository.Schedule;

import org.springframework.http.ResponseEntity;
import wonbin.scheduler.Entity.Schedule.ScheduleInfo;

import java.util.List;

public interface ScheduleRepository {
    public void save(ScheduleInfo info);
    public List<ScheduleInfo> findApplyUseMonth(int year,int month);
    public void delete(long applyId);
}
