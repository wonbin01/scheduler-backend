package wonbin.scheduler.Entity.Post;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="post_info")
@NoArgsConstructor
@Data
@Entity
public class PostInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String content;

    @Column(name = "categoryName")
    private String categoryName;

    public PostInfo(Long id,String title, String content, String categoryName){
        this.id=id;
        this.title=title;
        this.content=content;
        this.categoryName=categoryName;
    }
}
