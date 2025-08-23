package wonbin.scheduler.Entity.Schedule;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Table(name="allowed_date")
@Entity
public class AllowedDate {
    private String date;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
