package wonbin.scheduler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Repository.MemoryRepository;
import wonbin.scheduler.Repository.Repository;
import wonbin.scheduler.member.MemberInfo;

@RestController
@Slf4j
@CrossOrigin(origins ="https://localhost:3000")
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberInfo info)
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
        return ResponseEntity.ok("로그인 성공");
    }
}