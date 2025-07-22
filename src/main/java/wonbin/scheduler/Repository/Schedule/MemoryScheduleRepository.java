package wonbin.scheduler.Repository.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import wonbin.scheduler.Entity.Schedule.ScheduleInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class MemoryScheduleRepository implements ScheduleRepository{
    HashMap<Integer, ArrayList<ScheduleInfo>> hm=new HashMap<>();
    private long applyIdSequence = 0;

    @Override
    public ScheduleInfo findByApplyId(long applyId) {
        for (int id : hm.keySet()) {
            List<ScheduleInfo> list = hm.get(id);
            if (list == null) continue;
            for (ScheduleInfo info : list) {
                if (info.getApplyId() == applyId) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public void update(ScheduleInfo info) {
        ScheduleInfo update = findByApplyId(info.getApplyId());
        if (update == null) {
            log.warn("스케줄 업데이트 실패 - 존재하지 않음 applyId: {}", info.getApplyId());
            throw new NoSuchElementException("해당 스케줄이 존재하지 않습니다.");
        }

        log.info("스케줄 업데이트 성공 - applyId: {}", info.getApplyId());

        update.setApplyDate(info.getApplyDate());
        update.setTimeSlot(info.getTimeSlot());
        update.setReason(info.getReason());
        update.setAlternativePlan(info.getAlternativePlan());
        update.setEtc(info.getEtc());
        update.setCreateAt(LocalDateTime.now());
        update.setUpdatedAt(true);
    }

    @Override
    public void delete(long applyId) {
        for (int id : hm.keySet()) {
            List<ScheduleInfo> list = hm.get(id);
            boolean removed = list.removeIf(info -> info.getApplyId() == applyId);
            if (removed){
                log.info("applyId 삭제  : {}",applyId);// 삭제 완료하면 종료
                break;
            }
        }
    }

    @Override
    public void save(ScheduleInfo info) {
        int usernum = info.getUsernumber(); // 사용자의 사번
        applyIdSequence++;
        info.setApplyId(applyIdSequence);
        info.setCreateAt(LocalDateTime.now());
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
