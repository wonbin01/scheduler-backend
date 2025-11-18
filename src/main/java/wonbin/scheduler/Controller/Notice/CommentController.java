package wonbin.scheduler.Controller.Notice;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Entity.Comment.CommentInfo;
import wonbin.scheduler.Service.comment.CommentService;

@Slf4j
@RestController
@RequestMapping("/api/notice")
public class CommentController {
    @Autowired
    CommentService commentService;

    @GetMapping("/{category}/{id}/comments")
    public ResponseEntity<List<CommentInfo>> returnComment(
            @PathVariable String category,
            @PathVariable Long id) {
        List<CommentInfo> comments = commentService.findById(id);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{category}/{id}/comments")
    public ResponseEntity<String> Save_Comment(@PathVariable String category, @PathVariable long id,
                                               @RequestBody CommentInfo comment, HttpSession session) {
        commentService.saveComment(id, comment, session);
        log.info("댓글 작성 PostedId : {}", id);
        return ResponseEntity.ok("댓글 작성 성공");
    }

    @DeleteMapping("/{category}/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String category, @PathVariable long id,
                                           @PathVariable long commentId) {
        return commentService.deleteComment(id, commentId);
    }

    @PutMapping("/{category}/{id}/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable String category,
            @PathVariable long id,
            @PathVariable long commentId,
            @RequestBody CommentInfo editCommentContent,
            HttpSession session
    ) {
        return commentService.updateComment(id, commentId, editCommentContent, session);
    }

}
