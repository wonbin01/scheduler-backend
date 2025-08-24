package wonbin.scheduler.Entity.Schedule;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name="allowed_date")
@Data
public class AllowedDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
}
