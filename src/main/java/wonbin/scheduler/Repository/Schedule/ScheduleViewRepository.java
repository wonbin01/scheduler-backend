package wonbin.scheduler.Repository.Schedule;

import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;

import java.util.ArrayList;
import java.util.List;

public interface ScheduleViewRepository {
    public List<ScheduleViewInfo> findByYear_Month(int year,int month);
    public void save(ScheduleViewInfo info);
    public void saveAll(List<ScheduleViewInfo> infoList);
}
