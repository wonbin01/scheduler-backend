package wonbin.scheduler.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Repository.Member.MemberInfoRepository;
import wonbin.scheduler.Entity.Member.MemberInfo;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final MemberInfoRepository repository; //지금은 일단 메모리에 저장

    @PostMapping("/signup") //회원가입
    public ResponseEntity<String> participation(@RequestBody MemberInfo info) {
        log.info("회원가입");
        int private_num=info.getUsernumber();
        String private_password=info.getPassword();
        String private_name=info.getUsername();
        log.info("MemberId={} ,MemberName={}, PassWord={}",private_num,private_name,private_password);
        repository.save(info);
        return ResponseEntity.ok("회원가입 성공");
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 현재 세션 가져오기 (없으면 null 반환)
        if (session != null) {
            session.invalidate(); // 세션 무효화 → 로그아웃 처리
        }
        log.info("로그아웃");
        return ResponseEntity.ok("로그아웃 완료");
    }
}