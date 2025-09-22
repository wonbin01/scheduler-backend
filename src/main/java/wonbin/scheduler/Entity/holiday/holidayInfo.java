package wonbin.scheduler.Entity.holiday;

import lombok.Data;

import java.time.LocalDate;

@Data
public class holidayInfo {
    private LocalDate date;
    private String name;

    public holidayInfo(LocalDate date, String name) {
        this.date = date;
        this.name = name;
    }
}
