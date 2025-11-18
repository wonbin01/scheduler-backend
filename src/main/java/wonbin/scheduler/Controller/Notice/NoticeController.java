package wonbin.scheduler.Controller.Notice;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Entity.Post.PostInfo;
import wonbin.scheduler.Service.notice.NoticeService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

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
        return noticeService.getDetailedPost(postId, session);
    }

    @DeleteMapping("/{category}/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String category, @PathVariable Long postId) {
        return noticeService.deletePost(postId);
    }

    @PutMapping("/{category}/{postId}")
    public void updatePost(@PathVariable String category, @PathVariable Long postId, @RequestBody PostInfo update) {
        noticeService.updatePost(category, postId, update);
    }

}
