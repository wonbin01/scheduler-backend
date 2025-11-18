package wonbin.scheduler.Controller.Salary;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wonbin.scheduler.Entity.Salary.SalaryDto;
import wonbin.scheduler.Entity.Salary.SalaryInfo;
import wonbin.scheduler.Service.salary.SalaryService;

@RestController
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;

    @PostMapping("/api/salary")
    public SalaryInfo calculateSalary(@RequestBody SalaryDto req) {
        return salaryService.calculateSalary(req);
    }
}
