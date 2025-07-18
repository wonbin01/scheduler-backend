package wonbin.scheduler.Controller.Notice;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonbin.scheduler.Entity.Comment.CommentInfo;
import wonbin.scheduler.Entity.Member.MemberInfo;
import wonbin.scheduler.Repository.Comment.CommentRepositoy;
import wonbin.scheduler.Repository.Comment.MemoryCommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notice")
public class CommentController {
    CommentRepositoy commentRepositoy= new MemoryCommentRepository();

    @GetMapping("/{category}/{id}/comments")
    public ResponseEntity<List<CommentInfo>> returnComment(
            @PathVariable String category,
            @PathVariable Long id) {

        List<CommentInfo> comments = commentRepositoy.findby_Id(id);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{category}/{id}/comments")
    public ResponseEntity<String> Save_Comment(@PathVariable String category, @PathVariable long id, @RequestBody CommentInfo comment, HttpSession session){
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        comment.setUserId(loginMember.getUsernumber());
        comment.setUsername(loginMember.getUsername());
        comment.setPostedId(id);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setComment_content(comment.getComment_content());
        log.info("댓글 작성 PostedId : {}",id);
        commentRepositoy.save_comment(comment);
        return ResponseEntity.ok("댓글 작성 성공");
    }

    @DeleteMapping("/{category}/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String category,@PathVariable long id, @PathVariable long commentId){
        List<CommentInfo> commentInfos = commentRepositoy.findby_Id(id); //게시글 찾기
        if(commentInfos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다");
        }
        for(CommentInfo candidate : commentInfos){
            if(candidate.getComment_Id()==commentId){
                commentRepositoy.delete_comment(candidate);
                log.info("댓글 삭제 왼료 commentId={}",commentId);
                break;
            }
        }
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
