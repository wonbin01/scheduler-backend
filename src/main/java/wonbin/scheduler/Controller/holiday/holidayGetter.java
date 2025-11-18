package wonbin.scheduler.Controller.holiday;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Service.Holiday.holidayService;

@RestController
@RequiredArgsConstructor
public class holidayGetter {
    private final holidayService holidayService;

    @PostMapping("/api/holidays/update")
    public void holidayUpdate() {
        holidayService.updateYearlyHoliday(LocalDate.now().getYear());
    }
}
