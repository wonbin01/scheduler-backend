package wonbin.scheduler.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.member.MemberInfo;
import wonbin.scheduler.Repository.Category.CategoryRepository;
import wonbin.scheduler.Repository.Category.MemoryCategoryRepository;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {

    CategoryRepository repository=new MemoryCategoryRepository();

    @GetMapping
    public ResponseEntity<?> checkSession(HttpSession session) {
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");

        if (loginMember != null) {
            log.info("Session 확인, memberId={}", loginMember.getUsername());

            // repository에서 카테고리 목록 가져오기
            List<String> categories = repository.find_all_category();

            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            return ResponseEntity.ok(response);
        } else {
            log.info("Session 확인 실패, 로그인 페이지로 이동");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }
    }

    @PostMapping("/category")
    public ResponseEntity<?> addcategory(@RequestBody Map<String,String> body,HttpSession session){
        MemberInfo loginMember=(MemberInfo) session.getAttribute("loginMember");
        String newcategory=body.get("name");
        List<String> all_category=repository.find_all_category();
        if(all_category.contains(newcategory.trim())){
            log.info("중복된 카테고리입니다");
            return ResponseEntity.badRequest().body("중복된 카테고리");
        }
        if (newcategory == null || newcategory.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("카테고리 이름이 비어있습니다.");
        }

        repository.save_category(newcategory);
        log.info("카테고리 : {} 추가",newcategory.trim());
        return ResponseEntity.ok("카테고리 추가 성공");
    }
}
