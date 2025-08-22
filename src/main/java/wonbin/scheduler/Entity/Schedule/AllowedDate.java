package wonbin.scheduler.Entity.Schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Table(name="allowed_date")
@Entity
public class AllowedDate {
    private String date;
    @Id
    private Long id;
}
