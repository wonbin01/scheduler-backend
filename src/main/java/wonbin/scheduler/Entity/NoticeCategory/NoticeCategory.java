package wonbin.scheduler.Entity.NoticeCategory;

import lombok.Data;
import wonbin.scheduler.Entity.Post.PostInfo;

import java.util.ArrayList;
import java.util.List;

@Data
public class NoticeCategory {
    private String name;
    private List<PostInfo> posts=new ArrayList<>(); //게시물 제목 목록

    public NoticeCategory(String name){
        this.name=name;
    }
}
