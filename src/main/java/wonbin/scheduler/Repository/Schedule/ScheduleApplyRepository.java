package wonbin.scheduler.Repository.Schedule;

import wonbin.scheduler.Entity.Schedule.ScheduleApplyInfo;

import java.util.List;

public interface ScheduleApplyRepository {
    public void save(ScheduleApplyInfo info);
    public List<ScheduleApplyInfo> findApplyUseMonth(int year, int month);
    public void delete(long applyId);
    public ScheduleApplyInfo findByApplyId(long applyId);
    public void update(ScheduleApplyInfo info);
}
