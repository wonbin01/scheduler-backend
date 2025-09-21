package wonbin.scheduler.Repository.holiday;

import java.time.LocalDate;

public interface holidayRepository {
    public void save(LocalDate date,String name);
}
