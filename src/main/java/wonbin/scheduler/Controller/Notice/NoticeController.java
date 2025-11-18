package wonbin.scheduler.Controller.Notice;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Entity.Post.PostInfo;
import wonbin.scheduler.Repository.Category.CategoryRepository;
import wonbin.scheduler.Repository.PostNotice.PostRepository;
import wonbin.scheduler.Service.notice.NoticeService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<?> Getcategory_list() {
        return noticeService.getCategoryList();
    }

    @PostMapping("/category")
    public ResponseEntity<?> Addcategory(@RequestBody Map<String, String> body) {
        return noticeService.addCategory(body.get("category"));
    }

    @GetMapping("/{category}")
    public List<PostInfo> getPostByCategory(@PathVariable String category) {
        return noticeService.getPostByCategory(category);
    }

    @PostMapping("/{category}")
    public PostInfo createPost(@PathVariable String category, @RequestBody PostInfo post) {
        return noticeService.createPost(category, post);
    }

    @GetMapping("/{category}/{postId}")
    public ResponseEntity<?> getDetailedPost(@PathVariable String category, @PathVariable Long postId,
                                             HttpSession session) {
        Optional<PostInfo> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            log.info("해당 postId를 찾을 수 없습니다 : {}", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 존재하지 않습니다");
        }
        PostInfo post = optionalPost.get();

        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        // 로그인 정보가 없을 수도 있으니 null 체크 필요
        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("userInfo", loginMember);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{category}/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String category, @PathVariable Long postId) {
        boolean deleted = postRepository.DeleteById(postId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 없습니다.");
        }
        log.info("삭제 성공 ID : {}", postId);
        return ResponseEntity.ok(String.format("삭제 성공 ID=%d", postId));
    }

    @PutMapping("/{category}/{postId}")
    public void updatePost(@PathVariable String category, @PathVariable Long postId, @RequestBody PostInfo update) {
        Optional<PostInfo> variable = postRepository.findById(postId); //postId를 통해서 객체 찾음
        if (variable.isPresent()) {
            PostInfo post = variable.get();
            post.setTitle(update.getTitle());
            post.setContent(update.getContent());
            post.setCategoryName(category);
            postRepository.update(post);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
    }

}
