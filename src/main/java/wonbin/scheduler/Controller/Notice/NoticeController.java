package wonbin.scheduler.Controller.Notice;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Post.PostInfo;
import wonbin.scheduler.Repository.Category.CategoryRepository;
import wonbin.scheduler.Repository.PostNotice.MemoryPostRepository;
import wonbin.scheduler.Repository.PostNotice.PostRepository;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final CategoryRepository categoryRepository;
    PostRepository postRepository=new MemoryPostRepository();

    @GetMapping
    public ResponseEntity<?> Getcategory_list() {
        // repository에서 카테고리 목록 가져오기
        List<String> categories = categoryRepository.find_all_category();
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/category")
    public ResponseEntity<?> Addcategory(@RequestBody Map<String,String> body){
        String newcategory=body.get("name");
        List<String> all_category=categoryRepository.find_all_category();
        if(all_category.contains(newcategory.trim())){
            log.info("중복된 카테고리입니다");
            return ResponseEntity.badRequest().body("중복된 카테고리");
        }
        if (newcategory == null || newcategory.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("카테고리 이름이 비어있습니다.");
        }

        categoryRepository.save_category(newcategory);
        log.info("카테고리 : {} 추가",newcategory.trim());
        return ResponseEntity.ok("카테고리 추가 성공");
    }

    @GetMapping("/{category}")
    public List<PostInfo> getPostByCategory(@PathVariable String category){
        log.info("category : {}",category);
        List<PostInfo> posts=postRepository.findByCategory(category);
        posts.sort(Comparator.comparing(PostInfo::getId).reversed());
        return posts;
    }

    @PostMapping("/{category}")
    public PostInfo createPost(@PathVariable String category, @RequestBody PostInfo post){
        post.setCategoryName(category);
        PostInfo savePost=postRepository.save(post.getTitle(),post.getContent(),category);
        log.info("post 성공! : {}",category);
        return savePost;
    }
    @GetMapping("/{category}/{postId}")
    public ResponseEntity<?> getDetailedPost(@PathVariable String category, @PathVariable Long postId, HttpSession session) {
        Optional<PostInfo> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.info("해당 postId를 찾을 수 없습니다 : {}", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 존재하지 않습니다");
        }
        PostInfo post = optionalPost.get();

        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        // 로그인 정보가 없을 수도 있으니 null 체크 필요
        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("userInfo", loginMember);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{category}/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String category, @PathVariable Long postId){
        boolean deleted = postRepository.DeleteById(postId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 없습니다.");
        }
        log.info("삭제 성공 ID : {}",postId);
        return ResponseEntity.ok(String.format("삭제 성공 ID=%d", postId));
    }
    @PutMapping("/{category}/{postId}")
    public void updatePost(@PathVariable String category, @PathVariable Long postId, @RequestBody PostInfo update){
        Optional<PostInfo> variable=postRepository.findById(postId); //postId를 통해서 객체 찾음
        if(variable.isPresent()){
            PostInfo post=variable.get();
            post.setTitle(update.getTitle());
            post.setContent(update.getContent());
            postRepository.update(post);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"게시글을 찾을 수 없습니다.");
        }
    }

}
