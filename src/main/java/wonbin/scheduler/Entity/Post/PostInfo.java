package wonbin.scheduler.Entity.Post;

import lombok.Data;

@Data
public class PostInfo {
    private Long id;
    private String title;
    private String content;
    private String categoryName;

    public PostInfo(Long id,String title, String content, String categoryName){
        this.id=id;
        this.title=title;
        this.content=content;
        this.categoryName=categoryName;
    }
}
