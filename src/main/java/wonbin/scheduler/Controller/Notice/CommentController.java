package wonbin.scheduler.Controller.Notice;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import wonbin.scheduler.Entity.Comment.CommentInfo;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Repository.Comment.CommentRepositoy;
import wonbin.scheduler.Service.comment.CommentService;

@Slf4j
@RestController
@RequestMapping("/api/notice")
public class CommentController {
    @Autowired
    CommentRepositoy commentRepositoy;
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
        // 세션에서 로그인 유저 확인
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 댓글 존재 여부 확인
        List<CommentInfo> candidate = commentRepositoy.findby_Id(id); //게시글확인
        if (candidate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다");
        }

        // 권한 확인 (댓글 작성자만 수정 가능)
        for (CommentInfo c : candidate) {
            if (c.getUserId() == loginMember.getUsernumber() &&
                    c.getComment_Id() == commentId) {
                c.setComment_content(editCommentContent.getComment_content());
                c.setCreatedAt(LocalDateTime.now());
                c.setUpdated(true);
                commentRepositoy.update_comment(c);
                log.info("댓글 수정 완료 commentId={}", c.getComment_Id());
                break;
            }
        }
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

}
