package wonbin.scheduler.Repository.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;

@Slf4j
@RequiredArgsConstructor
//@Repository
public class MemoryScheduleViewRepository implements ScheduleViewRepository {
    @Override
    public int findByUserName(String username) {
        return 0;
    }

    private final MemberInfoRepository memberInfoRepository;
    ConcurrentHashMap<Integer, List<ScheduleViewInfo>> hm = new ConcurrentHashMap<>();
    long scheduleId = 0L;

    @Override
    public void delete(ScheduleViewInfo info) {
        for (Map.Entry<Integer, List<ScheduleViewInfo>> entry : hm.entrySet()) {
            List<ScheduleViewInfo> list = entry.getValue();
            boolean removed = list.removeIf(s -> s.getScheduleEventId() == info.getScheduleEventId());
            if (removed) {
                // 만약 리스트가 비었으면, 맵에서 키 제거
                if (list.isEmpty()) {
                    hm.remove(entry.getKey());
                }
                break; // 삭제 완료했으면 루프 종료
            }
        }
    }

    @Override
    public List<ScheduleViewInfo> findByUsernumberYearMonth(int usernumber, int year, int month) {
        return List.of();
    }


    @Override
    public ScheduleViewInfo findByScheduleId(long id) {
        for (List<ScheduleViewInfo> candidate : hm.values()) {
            for (ScheduleViewInfo info : candidate) {
                if (info.getScheduleEventId() == id) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public void save(ScheduleViewInfo info) {
        int usernum = info.getUserNumber();
        List<ScheduleViewInfo> list = hm.putIfAbsent(usernum, new ArrayList<>());
        if (list == null) {
            // 새로 추가했으니, 새로 만든 리스트에 추가
            log.info("info= {}", info);
            info.setScheduleEventId(scheduleId++);
            hm.get(usernum).add(info);
        } else {
            // 기존 리스트에 추가
            info.setScheduleEventId(scheduleId++);
            list.add(info);
            log.info("info= {}", info);
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
                if (info.getApplyDate() != null && info.getApplyDate().getYear() == year
                        && info.getApplyDate().getMonthValue() == month) {
                    Optional<MemberInfo> optionalMember = memberInfoRepository.findById(info.getUserNumber());
                    optionalMember.ifPresent(Member -> {
                        info.setUserName(Member.getUsername());
                    });
                    result.add(info);
                }
            }
        }
        return result;
    }

}
