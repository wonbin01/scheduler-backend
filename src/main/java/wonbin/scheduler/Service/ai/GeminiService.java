package wonbin.scheduler.Service.ai;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wonbin.scheduler.dto.CellDto;

@Service
public class GeminiService {

    @Value("${gcp.GEMINI_API_KEY}")
    private String apiKey;

    public String callGEMINI(CellDto cellDto, MultipartFile file, String colorInfo) {
        Client client = Client.builder().apiKey(apiKey).build();
        String req = String.format(
                """
                        첨부된 근무 스케줄표 이미지와 추가 정보를 분석하여, 각 직원의 이름, 날짜, 출근시간, 퇴근시간을 추출한 후,
                        이 결과를 JSON 배열 형식으로만 응답해 주세요.
                        
                        JSON 배열은 반드시 날짜 오름차순(목요일, 금요일, 토요일...)으로 정렬해 주세요.
                        예를 들어, 목요일 근무자 데이터를 모두 나열한 뒤 금요일 근무자 데이터를 나열해야 합니다.
                        
                        직원 이름에 해당하는 근무 시간이 정확히 어떤 날짜 컬럼 아래에 위치하는지 수직적으로 꼼꼼하게 확인한 후 해당 날짜로 매핑해야 합니다.
                        이름은 반드시 추가 정보에 들어있는 이름만 사용하세요.
                        
                        추가 정보2에 들어있는 정보를 활용하여, 스케줄과 포지션을 매칭해 주세요.
                        
                        JSON 객체의 형태 예시는 다음과 같습니다:
                        {
                            "userName": "이하정",
                            "applyDate": "11/06(목)",
                            "startTime": "16:00",
                            "endTime": "22:30",
                            "position": "매점"
                        }
                        
                        추가 정보1: %s,
                        추가 정보2: %s
                        """,
                cellDto, colorInfo
        );

        try {
            GenerateContentResponse response = getLlmAnswer(file, req, client);
            return response.text();
        } catch (IOException e) {
            return "파일 처리 중 오류가 발생했습니다. : " + e.getMessage();
        } catch (Exception e) {
            return "Gemini API 호출 중 오류가 발생했습니다. : " + e.getMessage();
        }
    }

    private static GenerateContentResponse getLlmAnswer(MultipartFile file, String req, Client client)
            throws IOException {
        byte[] bytes = file.getBytes();
        String mimeType = file.getContentType();

        Part imagePart = Part.fromBytes(bytes, mimeType);

        Content content = Content.builder()
                .parts(List.of(
                        imagePart,
                        Part.fromText(req)
                ))
                .build();

        return client.models.generateContent(
                "gemini-2.5-pro",
                content,
                null
        );
    }
}
