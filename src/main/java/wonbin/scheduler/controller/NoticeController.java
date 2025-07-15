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
    public ResponseEntity<?> checkSession() {
        // repository에서 카테고리 목록 가져오기
        List<String> categories = repository.find_all_category();
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/category")
    public ResponseEntity<?> addcategory(@RequestBody Map<String,String> body){
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
