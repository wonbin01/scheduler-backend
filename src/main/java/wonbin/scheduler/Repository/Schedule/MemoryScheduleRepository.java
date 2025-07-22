package wonbin.scheduler.Repository.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import wonbin.scheduler.Entity.Schedule.ScheduleInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class MemoryScheduleRepository implements ScheduleRepository{
    HashMap<Integer, ArrayList<ScheduleInfo>> hm=new HashMap<>();

    @Override
    public void save(ScheduleInfo info) {
        int usernum = info.getUsernumber(); // 사용자의 사번
        //유효성 검사
        hm.putIfAbsent(usernum, new ArrayList<>());
        hm.get(usernum).add(info);
    }

    @Override
    public List<ScheduleInfo> findApplyUseMonth(int year,int month) {
        List<ScheduleInfo> result = new ArrayList<>();
        for (int usernum : hm.keySet()) {
            ArrayList<ScheduleInfo> al = hm.get(usernum);
            for (ScheduleInfo info : al) {
                if (info.getApplyDate() != null && info.getApplyDate().getYear()==year
                        &&info.getApplyDate().getMonthValue() == month) {
                    result.add(info);
                }
            }
        }
        return result;
    }
}
