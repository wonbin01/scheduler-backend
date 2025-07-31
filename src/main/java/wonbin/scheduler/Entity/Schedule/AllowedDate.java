package wonbin.scheduler.Entity.Schedule;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("allowed_date")
public class AllowedDate {
    private String date;
    private Long id;
}
