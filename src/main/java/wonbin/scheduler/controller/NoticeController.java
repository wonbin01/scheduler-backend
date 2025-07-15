package wonbin.scheduler.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.member.MemberInfo;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {
    //Todo : 나중에 repository 주입
    private final List<String> categories= Collections.synchronizedList(new ArrayList<>(
            Arrays.asList("매점","웰컴","엔젤","인사","소방","기타")
    ));
    @GetMapping
    // repositoy에서 카테고리의 list를 찾아서 json으로 전송해줌
    public ResponseEntity<?> checkSession(HttpSession session){
        MemberInfo loginMember=(MemberInfo) session.getAttribute("loginMember");

        if(loginMember!=null){
            log.info("Session 확인, memberId={}",loginMember.getUsername());
            Map<String,Object> response=new HashMap<>();
            response.put("categories",new ArrayList<>(categories));
            return ResponseEntity.ok(response);
        }
        else
        {
            log.info("Session 확인 실패, 로그인 페이지로 이동");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }
    }
    @PostMapping("/category")
    public ResponseEntity<?> addcategory(@RequestBody Map<String,String> body,HttpSession session){
        MemberInfo loginMember=(MemberInfo) session.getAttribute("loginMember");
        String newcategory=body.get("name");
        if(newcategory==null || newcategory.trim().isEmpty()){
            log.info("빈 카테고리입니다");
            return ResponseEntity.badRequest().body("카테고리 이름을 입력하시오");
        }
        synchronized (categories){
            if(categories.contains(newcategory.trim())){
                return ResponseEntity.badRequest().body("이미 존재하는 카테고리입니다");
            }
            categories.add(newcategory.trim());
        }
        log.info("카테고리 : {} 추가",newcategory.trim());

        return ResponseEntity.ok("카테고리 추가 성공");
    }
}
