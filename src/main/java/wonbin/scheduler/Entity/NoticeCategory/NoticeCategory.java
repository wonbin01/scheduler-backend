package wonbin.scheduler.Entity.NoticeCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="notice_category")
public class NoticeCategory {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    public NoticeCategory(String name){
        this.name=name;
    }
}
