package wonbin.scheduler.Entity.NoticeCategory;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("notice_category")
public class NoticeCategory {
    private String name;

    public NoticeCategory(String name){
        this.name=name;
    }
}
