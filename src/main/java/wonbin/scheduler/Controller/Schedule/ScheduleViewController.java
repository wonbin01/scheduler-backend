package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleViewRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleViewController {
     private final ScheduleViewRepository viewRepository;
     private final MemberInfoRepository memberRepository;

    @GetMapping("/scheduleView")
    public ResponseEntity<?> checkSession(HttpSession session){ /// 로그인 여부 및 로그인 정보 받아옴
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        log.info("로그인 여부 확인");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }

    @GetMapping("/scheduleview/{year}/{month}")
    public ResponseEntity<?> returnViewList(@PathVariable int year, @PathVariable int month){
        List<ScheduleViewInfo> result=viewRepository.findByYear_Month(year,month);
        log.info("스케줄 view 정보 전달 : {}-{}",year,month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/member/all")
    public ResponseEntity<?> returnAllMember() {
        List<MemberInfo> info=memberRepository.findAll();
        for(MemberInfo candidate : info){
            candidate.setPassword("");
        }
        return ResponseEntity.ok(info);
    }

    @PostMapping("/scheduleview/apply")
    public ResponseEntity<?> saveSchedule(@RequestBody List<ScheduleViewInfo> list){
        if(list == null || list.isEmpty()){
            return ResponseEntity.badRequest().body("스케줄 데이터가 없습니다");
        }

        Integer userNumber=list.get(0).getUserNumber();
        String userName=null;
        if(userNumber!=null){
            Optional<MemberInfo> optionalMember=memberRepository.findById(userNumber);
            if(optionalMember.isPresent()){
                userName=optionalMember.get().getUsername();
            } else{
                return ResponseEntity.badRequest().body("사용자 찾기 실패");
            }
        } else {
            return ResponseEntity.badRequest().body("사용자 번호가 제공되지 않았습니다.");
        }
        String finalUserName=userName;
        list.forEach(info->info.setUserName(finalUserName));

        viewRepository.saveAll(list);  // 한 번에 처리
        return ResponseEntity.ok("스케줄이 정상적으로 저장되었습니다");
    }

    @DeleteMapping("/scheduleview/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable long id) {
        ScheduleViewInfo byScheduleId = viewRepository.findByScheduleId(id);
        if (byScheduleId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 id를 찾지 못했습니다");
        }
        viewRepository.delete(byScheduleId);
        return ResponseEntity.ok("해당 스케줄 삭제 완료");
    }
}
