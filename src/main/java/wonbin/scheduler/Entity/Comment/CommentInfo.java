package wonbin.scheduler.Entity.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
import wonbin.scheduler.Entity.Member.MemberInfo;

@Data
@Table("comment_info")
public class CommentInfo {
    private Long comment_Id;
    private String username;
    private int userId;
    private String comment_content;
    private Long postedId;
    private boolean updated = false;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public void setCommentInfo(long id, CommentInfo comment, HttpSession session) {
        MemberInfo loginMember = (MemberInfo) session.getAttribute("loginMember");
        loginMember.setPassword("");
        comment.setUserId(loginMember.getUsernumber());
        comment.setUsername(loginMember.getUsername());
        comment.setPostedId(id);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setComment_content(comment.getComment_content());
    }
}
