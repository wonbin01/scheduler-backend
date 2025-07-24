package wonbin.scheduler.Entity.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("comment_info")
public class CommentInfo {
    private Long comment_Id;
    private String username;
    private int userId;
    private String comment_content;
    private Long postedId;
    private boolean updated=false;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
