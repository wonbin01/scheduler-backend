package wonbin.scheduler.Repository.salary;

import wonbin.scheduler.Entity.Salary.SalaryInfo;

public interface SalaryRepository {
    int getNextMonthWeeklyBonus(int usernumber, int year, int month);

    void saveSalary(SalaryInfo salary, int usernumber, int year, int month);
}
