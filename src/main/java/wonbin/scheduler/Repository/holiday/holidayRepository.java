package wonbin.scheduler.Repository.holiday;

import wonbin.scheduler.Entity.holiday.holidayInfo;

import java.time.LocalDate;
import java.util.List;

public interface holidayRepository {
    public void save(LocalDate date,String name);
    public List<holidayInfo> findByYearAndMonth(int year, int month);
}
