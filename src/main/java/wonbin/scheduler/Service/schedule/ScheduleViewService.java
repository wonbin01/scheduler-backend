package wonbin.scheduler.Service.schedule;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleViewRepository;
import wonbin.scheduler.Service.ai.DocumentAiService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleViewService {
    private final ScheduleViewRepository scheduleViewRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final DocumentAiService documentAiService;

    public ResponseEntity<?> checkSession(HttpSession session) {
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        log.info("로그인 여부 확인");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }

    public ResponseEntity<?> getViewList(int year, int month) {
        List<ScheduleViewInfo> result = scheduleViewRepository.findByYear_Month(year, month);
        log.info("스케줄 view 정보 전달 : {}-{}", year, month);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getAllMember() {
        List<MemberInfo> allMembers = memberInfoRepository.findAll();
        for (MemberInfo candidate : allMembers) {
            candidate.setPassword("");
        }
        return ResponseEntity.ok(allMembers);
    }

    public ResponseEntity<?> saveAllSchedule(List<ScheduleViewInfo> viewList) {
        if (viewList == null || viewList.isEmpty()) {
            return ResponseEntity.badRequest().body("스케줄 데이터가 없습니다");
        }
        Integer userNumber = viewList.get(0).getUserNumber();
        String userName = null;
        if (userNumber != null) {
            Optional<MemberInfo> optionalMember = memberInfoRepository.findById(userNumber);
            if (optionalMember.isPresent()) {
                userName = optionalMember.get().getUsername();
            } else {
                return ResponseEntity.badRequest().body("사용자 찾기 실패");
            }
        } else {
            return ResponseEntity.badRequest().body("사용자 번호가 제공되지 않았습니다.");
        }
        String finalUserName = userName;
        viewList.forEach(info -> info.setUserName(finalUserName));
        scheduleViewRepository.saveAll(viewList);
        return ResponseEntity.ok(viewList);
    }

    public ResponseEntity<?> deleteSchedule(long id) {
        ScheduleViewInfo deleteCandidate = scheduleViewRepository.findByScheduleId(id);
        if (deleteCandidate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID를 찾지 못했습니다.");
        }
        scheduleViewRepository.delete(deleteCandidate);
        return ResponseEntity.ok("스케줄 삭제완료 : " + id);
    }
}
