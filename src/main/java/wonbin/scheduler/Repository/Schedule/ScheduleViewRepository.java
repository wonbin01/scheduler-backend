package wonbin.scheduler.Repository.Schedule;

import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;

import java.util.ArrayList;
import java.util.List;

public interface ScheduleViewRepository {
    public List<ScheduleViewInfo> findByYear_Month(int year,int month);
    public void save(ScheduleViewInfo info);
    public void saveAll(List<ScheduleViewInfo> infoList);
    public ScheduleViewInfo findByScheduleId(long id);
    public void delete(ScheduleViewInfo info);
    public List<ScheduleViewInfo> findByUsernumberYearMonth(int usernumber,int year,int month);
}
