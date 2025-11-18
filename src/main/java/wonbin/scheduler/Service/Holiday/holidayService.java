package wonbin.scheduler.Service.Holiday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import wonbin.scheduler.Repository.holiday.holidayRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class holidayService {
    @Value("${gcp.SERVICE_KEY}")
    private String SERVICE_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final holidayRepository holidayRepository;

    public void updateYearlyHoliday(int year) {
        for (int month = 1; month <= 12; month++) {
            fetchAndSaveHolidays(year, month);
        }
    }

    private void fetchAndSaveHolidays(int year, int month) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                    .queryParam("ServiceKey", SERVICE_KEY)
                    .queryParam("solYear", year)
                    .queryParam("solMonth", String.format("%02d", month))
                    .queryParam("_type", "json")
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);

            JSONObject json = new JSONObject(response);
            JSONObject body = json.getJSONObject("response").getJSONObject("body");

            if (body.isNull("items") || body.get("items").toString().equals("")) {
                System.out.println(year + "-" + month + " : 공휴일 없음");
                return;
            }
            Object itemObj = body.getJSONObject("items").get("item");
            if (itemObj instanceof JSONArray) {
                JSONArray items = (JSONArray) itemObj;
                for (int i = 0; i < items.length(); i++) {
                    JSONObject holiday = items.getJSONObject(i);
                    printHoliday(holiday);
                }
            } else if (itemObj instanceof JSONObject) {
                JSONObject holiday = (JSONObject) itemObj;
                printHoliday(holiday);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printHoliday(JSONObject holiday) {
        String date = holiday.get("locdate").toString(); // ex: 20250101
        String name = holiday.getString("dateName");     // ex: 설날

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        System.out.println("날짜: " + localDate + ", 이름: " + name);

        holidayRepository.save(localDate, name);
        log.info("holiday 저장 : {} , {}", localDate, name);
    }
}
