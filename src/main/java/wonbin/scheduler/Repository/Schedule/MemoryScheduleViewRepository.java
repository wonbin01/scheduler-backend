package wonbin.scheduler.Repository.Schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemoryScheduleViewRepository implements ScheduleViewRepository{
    ConcurrentHashMap<Integer,List<ScheduleViewInfo>> hm=new ConcurrentHashMap<>();

    @Override
    public void save(ScheduleViewInfo info) {
        int usernum = info.getUserNumber();
        List<ScheduleViewInfo> list = hm.putIfAbsent(usernum, new ArrayList<>());
        if (list == null) {
            // 새로 추가했으니, 새로 만든 리스트에 추가
            log.info("info= {}",info);
            hm.get(usernum).add(info);
        } else {
            // 기존 리스트에 추가
            list.add(info);
            log.info("info= {}",info);
        }
    }

    @Override
    public void saveAll(List<ScheduleViewInfo> infoList) {
        for (ScheduleViewInfo info : infoList) {
            save(info);
        }
    }

    @Override
    public List<ScheduleViewInfo> findByYear_Month(int year, int month) {
        List<ScheduleViewInfo> result = new ArrayList<>();
        for (int member : hm.keySet()) {
            List<ScheduleViewInfo> schedules = hm.get(member);
            for (ScheduleViewInfo info : schedules) {
                if (info.getApplyDate() != null && info.getApplyDate().getYear() == year && info.getApplyDate().getMonthValue() == month) {
                    result.add(info);
                }
            }
        }
        return result;
    }

}
