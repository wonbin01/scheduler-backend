package wonbin.scheduler.Service.scheduleParsar;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.Document.Page.Table;
import com.google.cloud.documentai.v1.Document.Page.Table.TableCell;
import com.google.cloud.documentai.v1.Document.Page.Table.TableRow;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wonbin.scheduler.Entity.ScheduleEntry;

@Service
public class DocumentAiService {
    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.documentai.processor.id}")
    private String processorId; // Form Parser ID
    @Value("${gcp.documentai.processor.location}")
    private String location;

    public List<ScheduleEntry> extractSchedule(MultipartFile file) throws IOException {
        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create()) {
            String processorName = String.format(
                    "projects/%s/locations/%s/processors/%s",
                    projectId, location, processorId
            );

            ByteString content = ByteString.copyFrom(file.getBytes());
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

            if (doc.getPagesCount() == 0 || doc.getPages(0).getTablesList().isEmpty()) {
                // 표를 찾을 수 없는 경우 빈 리스트 반환
                return new ArrayList<>();
            }

            // 1. 필요한 정보 추출
            Table scheduleTable = doc.getPages(0).getTablesList().get(0); // 첫번째 페이지의 첫번째 테이블
            String fullText = doc.getText();

            // 2. 헤더 분석 (날짜-시간구분 컬럼 목록 생성)
            List<String> combinedHeaders = analyzeHeaders(scheduleTable, fullText);

            // 3. 본문 분석 및 POJO 매핑
            List<ScheduleEntry> scheduleEntries = new ArrayList<>();
            List<TableRow> bodyRows = scheduleTable.getBodyRowsList();

            // 이름과 스케줄 데이터가 있는 행을 순회합니다.
            for (TableRow row : bodyRows) {
                // 셀 텍스트 목록 추출
                List<String> rowCells = row.getCellsList().stream()
                        .map(cell -> getTextFromLayout(cell, fullText))
                        .collect(Collectors.toList());

                // 최소한의 데이터 (구분, 이름, 일수)를 포함
                if (rowCells.size() < 3) {
                    continue;
                }

                String name = rowCells.get(1); // 이름은 두 번째 컬럼 (인덱스 1)

                // 데이터 컬럼은 3번째 셀(인덱스 3)부터 시작
                // 3개 셀마다 하나의 스케줄 (출근/퇴근/근무시간)을 구성
                for (int i = 3; i < rowCells.size(); i += 3) {
                    // combinedHeaders의 인덱스와 rowCells의 인덱스를 맞춤
                    // combinedHeaders는 날짜당 3개씩 ('출근', '퇴근', '근무시간'이 결합된) 헤더를 가짐
                    int headerIndex = (i - 3) / 3;

                    if (headerIndex >= combinedHeaders.size()) {
                        break;
                    }

                    String dateHeader = combinedHeaders.get(headerIndex);

                    // 3개의 데이터 셀이 있는지 확인합니다.
                    if (i + 2 < rowCells.size()) {
                        String checkIn = rowCells.get(i).trim();
                        String checkOut = rowCells.get(i + 1).trim();
                        String workTime = rowCells.get(i + 2).trim();

                        // 세 값이 모두 비어있지 않은 경우에만 스케줄 엔트리를 생성합니다.
                        if (!checkIn.isEmpty() && !checkOut.isEmpty() && !workTime.isEmpty()) {
                            scheduleEntries.add(new ScheduleEntry(
                                    name,
                                    dateHeader,
                                    checkIn,
                                    checkOut,
                                    workTime
                            ));
                        }
                    }
                }
            }

            return scheduleEntries;

        }
    }

    private List<String> analyzeHeaders(Table table, String fullText) {
        List<String> dateHeaders = new ArrayList<>();
        List<TableRow> headerRows = table.getHeaderRowsList(); // 가장 위쪽 행을 가져옴

        if (headerRows.isEmpty()) {
            return dateHeaders;
        }

        // 첫 번째 헤더 행 (날짜 정보)을 분석합니다.
        TableRow dateRow = headerRows.get(0);

        for (TableCell dateCell : dateRow.getCellsList()) {
            String dateText = getTextFromLayout(dateCell, fullText).replace("\n", " ").trim();

            // '구분', '이름', '일수' 같은 스케줄이 아닌 컬럼은 건너뜁니다.
            if (dateText.contains("구분") || dateText.contains("이름") || dateText.contains("일수")) {
                continue;
            }

            if (!dateText.isEmpty()) {
                dateHeaders.add(dateText);
            }
        }

        return dateHeaders;
    }

    private String getTextFromLayout(TableCell cell, String fullText) {
        if (cell.hasLayout() && cell.getLayout().hasTextAnchor()) {
            return cell.getLayout().getTextAnchor().getTextSegmentsList().stream()
                    .map(segment -> {
                        int startIndex = (int) segment.getStartIndex();
                        int endIndex = (int) segment.getEndIndex();
                        // 인덱스 유효성 검사
                        if (startIndex >= 0 && endIndex <= fullText.length() && startIndex <= endIndex) {
                            return fullText.substring(startIndex, endIndex);
                        }
                        return "";
                    })
                    .collect(Collectors.joining(" ")).trim();
        }
        return "";
    }
}