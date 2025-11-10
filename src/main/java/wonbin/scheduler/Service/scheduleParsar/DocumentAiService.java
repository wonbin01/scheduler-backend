package wonbin.scheduler.Service.scheduleParsar;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentAiService {
    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.documentai.processor.id}")
    private String processorId; // Form Parser ID
    @Value("${gcp.documentai.processor.location}")
    private String location;
    @Value("${gcp.GEMINI_API_KEY}")
    private String apiKey;

    public String extractSchedule(MultipartFile file) throws IOException {
        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create()) {
            String processorName = String.format(
                    "projects/%s/locations/%s/processors/%s",
                    projectId, location, processorId
            );

            ByteString content = ByteString.copyFrom(file.getBytes()); //입력 문서를 만듦
            RawDocument document = RawDocument.newBuilder()
                    .setContent(content)
                    .setMimeType(file.getContentType() != null ? file.getContentType() : "image/png")
                    .build();

            ProcessRequest request = ProcessRequest.newBuilder()
                    .setName(processorName)
                    .setRawDocument(document)
                    .build();

            ProcessResponse response = client.processDocument(request);
            Document doc = response.getDocument();
            String result = callGEMINI(doc.getText(), file);
            return result;
        }
    }

    private String callGEMINI(String info, MultipartFile file) {
        Client client = Client.builder().apiKey(apiKey).build();
        String req = String.format(
                """
                        첨부된 근무 스케줄표 이미지와 추가 정보를 분석하여, 각 직원의 이름, 날짜, 출근시간, 퇴근시간을 추출한 후,
                        이 결과를 JSON 배열 형식으로만 응답해 주세요.
                        
                        JSON 배열은 반드시 날짜 오름차순(목요일, 금요일, 토요일...)으로 정렬해 주세요.
                        예를 들어, 목요일 근무자 데이터를 모두 나열한 뒤 금요일 근무자 데이터를 나열해야 합니다.
                        
                        직원 이름에 해당하는 근무 시간이 정확히 어떤 날짜 컬럼 아래에 위치하는지 수직적으로 꼼꼼하게 확인한 후 해당 날짜로 매핑해야 합니다.
                        
                        JSON으로 반환할 때, "포지션" 부분은 빈 문자열("")로 두세요.
                        이름은 반드시 추가 정보에 들어있는 이름만 사용하세요.
                        
                        JSON 객체의 형태 예시는 다음과 같습니다:
                        {
                            "userName": "이하정",
                            "applyDate": "11/06(목)",
                            "startTime": "16:00",
                            "endTime": "22:30",
                            "position": ""
                        }
                        
                        추가 정보: %s
                        """,
                info
        );

        try {
            byte[] bytes = file.getBytes();
            String mimeType = file.getContentType();

            Part imagePart = Part.fromBytes(bytes, mimeType);

            Content content = Content.builder()
                    .parts(List.of(
                            imagePart,
                            Part.fromText(req)
                    ))
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-pro",
                    content,
                    null
            );
            return response.text();
        } catch (IOException e) {
            return "파일 처리 중 오류가 발생했습니다. : " + e.getMessage();
        } catch (Exception e) {
            return "Gemini API 호출 중 오류가 발생했습니다. : " + e.getMessage();
        }
    }

}