package wonbin.scheduler.Entity.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name="comment_info")
@Entity
public class CommentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_Id;
    private String username;
    private int userId;
    private String comment_content;
    private Long postedId;
    private boolean updated=false;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
