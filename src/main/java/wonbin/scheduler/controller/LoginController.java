package wonbin.scheduler.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Repository.Member.MemoryRepository;
import wonbin.scheduler.Repository.Member.Repository;
import wonbin.scheduler.Entity.member.MemberInfo;

@RestController
@Slf4j
public class LoginController {

    Repository repository=new MemoryRepository(); //지금은 일단 메모리에 저장

    @PostMapping("/signup") //회원가입
    public ResponseEntity<String> participation(@RequestBody MemberInfo info) {
        log.info("회원가입");
        int private_num=info.getUsername();
        String private_password=info.getPassword();
        log.info("MemberId={} , PassWord={}",private_num,private_password);
        //Todo - 지금은 메모리에 저장하지만, 나중에 DB연결까지 해야됨
        repository.save(info);
        return ResponseEntity.ok("회원가입 성공");
    }

    //@PostMapping("/login")
    public ResponseEntity<String> loginV1(@RequestBody MemberInfo info)
    {
        log.info("로그인");
        int private_num=info.getUsername();
        String private_password=info.getPassword();
        log.info("MemberId={} , PassWord={}",private_num,private_password);
        MemberInfo check=repository.findById(private_num);

        if(check==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디");

        if(!check.getPassword().equals(private_password))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");

        log.info("로그인 성공: {}",private_num);

        ResponseCookie cookie=ResponseCookie.from("loginToken",String.valueOf(private_num))
                .httpOnly(true) //javascript에서 접근 x
                .path("/") //전체 경로에 대해서 쿠키 유효
                .maxAge(3600) //1시간만 유효한 쿠키
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("로그인 성공");
    }
    @PostMapping("/login")
        public ResponseEntity<String> loginV2(@RequestBody MemberInfo info, HttpSession session){
            log.info("로그인 요청 V2: {}",info.getUsername());
            int private_num=info.getUsername();
            String private_password=info.getPassword();
            log.info("MemberId={} , Password={}", private_num,private_password);

            MemberInfo check=repository.findById(private_num);
            if(check==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디");

            if(!check.getPassword().equals(private_password))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다");

            log.info("로그인 성공:{}",private_num);

            session.setAttribute("loginMember",check);
            return ResponseEntity.ok("로그인 성공");
        }
}