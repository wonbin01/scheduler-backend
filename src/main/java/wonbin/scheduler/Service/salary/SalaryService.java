package wonbin.scheduler.Service.salary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wonbin.scheduler.Entity.Salary.SalaryDto;
import wonbin.scheduler.Entity.Salary.SalaryInfo;
import wonbin.scheduler.Entity.Schedule.ScheduleViewInfo;
import wonbin.scheduler.Entity.holiday.holidayInfo;
import wonbin.scheduler.Repository.Schedule.ScheduleViewRepository;
import wonbin.scheduler.Repository.holiday.holidayRepository;
import wonbin.scheduler.Repository.salary.SalaryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryService {
    private final ScheduleViewRepository scheduleViewRepository;
    private final holidayRepository holidayRepository;
    private final SalaryRepository salaryRepository;
    private final RestTemplate restTemplate;
    private final String url = "http://calculator:8000/calculate";

    public SalaryInfo calculateSalary(SalaryDto dto) {
        int userNumber = dto.getUsernumber();
        int year = dto.getYear();
        int month = dto.getMonth();

        List<ScheduleViewInfo> result = scheduleViewRepository.findByUsernumberYearMonth(userNumber,
                year, month);
        List<holidayInfo> holidayInfo = holidayRepository.findByYearAndMonth(year, month);
        int bonus = salaryRepository.getNextMonthWeeklyBonus(userNumber, year, month);

        Map<String, Object> request = new HashMap<>();
        request.put("usernumber", userNumber);
        request.put("year", year);
        request.put("month", month);
        request.put("workLogs", result);
        request.put("holidayList", holidayInfo);
        request.put("prev_weekly_bonus", bonus);

        SalaryInfo calculateResult = restTemplate.postForObject(url, request, SalaryInfo.class);
        salaryRepository.saveSalary(calculateResult, userNumber, year, month);
        return calculateResult;
    }
}
