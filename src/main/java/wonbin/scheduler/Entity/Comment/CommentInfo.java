package wonbin.scheduler.Entity.Comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentInfo {
    private Long comment_Id;
    private String Username;
    private String Comment_content;
    private String Category;
    private Long PostedId;
    private LocalDateTime CreatedAt;
}
