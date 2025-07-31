package wonbin.scheduler.Repository.Schedule;

import wonbin.scheduler.Entity.Schedule.AllowedDate;

import java.util.List;
import java.util.Optional;

public interface AllowedDateRepository {
    List<AllowedDate> findAllAllowedDate();
    boolean deleteAllowedDate(String date);
    boolean saveAllowedDate(List<String> dates);
}
