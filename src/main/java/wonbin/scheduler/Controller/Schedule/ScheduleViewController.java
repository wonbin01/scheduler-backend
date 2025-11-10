package wonbin.scheduler.Controller.Schedule;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;
import wonbin.scheduler.Repository.Schedule.ScheduleViewRepository;
import wonbin.scheduler.Service.scheduleParsar.DocumentAiService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleViewController {
    private final ScheduleViewRepository viewRepository;
    private final MemberInfoRepository memberRepository;
    private final DocumentAiService documentAiService;

    @GetMapping("/scheduleView")
    public ResponseEntity<?> checkSession(HttpSession session) { /// 로그인 여부 및 로그인 정보 받아옴
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        log.info("로그인 여부 확인");
        return ResponseEntity.ok(Map.of(
                "id", loginMember.getUsernumber(),
                "name", loginMember.getUsername()
        ));
    }

    @GetMapping("/scheduleview/{year}/{month}")
    public ResponseEntity<?> returnViewList(@PathVariable int year, @PathVariable int month) {
        List<ScheduleViewInfo> result = viewRepository.findByYear_Month(year, month);
        log.info("스케줄 view 정보 전달 : {}-{}", year, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/member/all")
    public ResponseEntity<?> returnAllMember() {
        List<MemberInfo> info = memberRepository.findAll();
        for (MemberInfo candidate : info) {
            candidate.setPassword("");
        }
        return ResponseEntity.ok(info);
    }

    @PostMapping("/scheduleview/apply")
    public ResponseEntity<?> saveSchedule(@RequestBody List<ScheduleViewInfo> list) {
        if (list == null || list.isEmpty()) {
            return ResponseEntity.badRequest().body("스케줄 데이터가 없습니다");
        }

        Integer userNumber = list.get(0).getUserNumber();
        String userName = null;
        if (userNumber != null) {
            Optional<MemberInfo> optionalMember = memberRepository.findById(userNumber);
            if (optionalMember.isPresent()) {
                userName = optionalMember.get().getUsername();
            } else {
                return ResponseEntity.badRequest().body("사용자 찾기 실패");
            }
        } else {
            return ResponseEntity.badRequest().body("사용자 번호가 제공되지 않았습니다.");
        }
        String finalUserName = userName;
        list.forEach(info -> info.setUserName(finalUserName));

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

    @PostMapping("/file")
    public ResponseEntity<?> extractFile(@RequestParam("file") MultipartFile file) {
        // 1. 파일 유효성 검사
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return new ResponseEntity<>("파일이 비어있거나 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            String totalSchedules = documentAiService.extractSchedule(file);

            if (totalSchedules.isEmpty()) {
                return new ResponseEntity<>("파일에서 유효한 스케줄 데이터를 추출하지 못했습니다.", HttpStatus.NO_CONTENT);
            }
            System.out.println(totalSchedules);
            return new ResponseEntity<>(totalSchedules, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>("파일 처리 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("스케줄 추출 중 시스템 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("file/extract/scheduleApply")
    public ResponseEntity<?> makeScheduleViewInfoAndSave(@RequestBody List<ScheduleViewInfo> list) {
        if (list == null || list.isEmpty()) {
            return ResponseEntity.badRequest().body("스케줄 데이터가 없습니다");
        }
        List<ScheduleViewInfo> resultList = new ArrayList<>();
        for (ScheduleViewInfo dto : list) {
            int userNumber = viewRepository.findByUserName(dto.getUserName());
            if (userNumber == -1) {
                return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
            }
            ScheduleViewInfo entity = new ScheduleViewInfo();
            entity.setUserNumber(userNumber);
            entity.setUserName(dto.getUserName());
            entity.setApplyDate(dto.getApplyDate());
            entity.setStartTime(dto.getStartTime());
            entity.setEndTime(dto.getEndTime());
            entity.setPosition(dto.getPosition());
            resultList.add(entity);
        }
        viewRepository.saveAll(resultList);
        return ResponseEntity.ok("스케줄이 정상적으로 저장되었습니다.");
    }

}
