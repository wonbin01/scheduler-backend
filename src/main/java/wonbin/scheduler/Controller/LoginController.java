package wonbin.scheduler.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Repository.Member.JDBCMemberInfoRepository;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final MemberInfoRepository repository;

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<String> participation(@RequestBody MemberInfo info) {
        log.info("회원가입 요청: MemberId={}, MemberName={}", info.getUsernumber(), info.getUsername());

        try {
            // repository.save() 메서드가 성공하면(예외를 던지지 않으면) 이 부분이 실행됩니다.
            repository.save(info);
            log.info("회원가입 성공: MemberId={}", info.getUsernumber());
            return ResponseEntity.ok("회원가입 성공");
        } catch (JDBCMemberInfoRepository.DuplicateMemberException e) {
            // repository.save()에서 던진 DuplicateMemberException을 여기서 잡습니다.
            log.error("회원가입 실패: {}", e.getMessage());
            // 올바른 HTTP 상태 코드인 409 Conflict와 예외 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
        public ResponseEntity<String> loginV2(@RequestBody MemberInfo info, HttpSession session){
            log.info("로그인 요청 : {}",info.getUsernumber());
            int private_num=info.getUsernumber();
            String private_password=info.getPassword();
            log.info("MemberId={} , Password={}", private_num,private_password);

        Optional<MemberInfo> check = getMemberInfo(private_num);
        if(check.isEmpty()){
            log.warn("로그인 실패 : MemberId={}",private_num);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디");
        }
        MemberInfo foundMember=check.get();
            if(!foundMember.getPassword().equals(private_password)){
                log.warn("비밀번호 불일치 : MemberId={}",private_num);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다");
            }

            log.info("로그인 성공:{}",private_num);

            session.setAttribute("loginMember",foundMember);
            return ResponseEntity.ok("로그인 성공");
        }

    private Optional<MemberInfo> getMemberInfo(int private_num) {
        Optional<MemberInfo> check=repository.findById(private_num);
        return check;
    }

    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 현재 세션 가져오기 (없으면 null 반환)
        if (session != null) {
            session.invalidate(); // 세션 무효화 → 로그아웃 처리
        }
        log.info("로그아웃");
        return ResponseEntity.ok("로그아웃 완료");
    }
}