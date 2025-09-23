package wonbin.scheduler.Entity.Salary;

import lombok.Data;

@Data
public class SalaryInfo {
    private int usernumber;
    private int year;
    private  int month;
    private int default_salary;
    private int night_salary;
    private int weekly_bonus;
    private int holiday_bonus;
    private int before_tax;
    private int after_tax;
    private int next_month_weekly_bonus;
}
