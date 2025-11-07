package wonbin.scheduler.Service.scheduleParsar;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.Document.Page;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public List<List<String>> extractSchedule(MultipartFile file) throws IOException {
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
            List<List<String>> docsInfo = extractTableInfo(doc);
            return docsInfo;
        }
    }

    public List<List<String>> extractTableInfo(Document document) {
        List<List<String>> tableData = new ArrayList<>();
        String fullText = document.getText(); // 전체 텍스트를 한 번에 가져옴

        for (Page page : document.getPagesList()) {
            for (Table table : page.getTablesList()) {

                List<TableRow> allRows = new ArrayList<>(); //모든 row를 집어넣음
                allRows.addAll(table.getHeaderRowsList());
                allRows.addAll(table.getBodyRowsList());

                for (TableRow row : allRows) {
                    List<String> rowData = new ArrayList<>();
                    for (TableCell cell : row.getCellsList()) { //각각의 열을 가져와서
                        StringBuilder cellText = new StringBuilder();

                        for (Document.TextAnchor.TextSegment segment :
                                cell.getLayout().getTextAnchor().getTextSegmentsList()) { //각 열의 정보를 합침
                            int startIndex = (int) segment.getStartIndex();
                            int endIndex = (int) segment.getEndIndex();

                            if (startIndex >= 0 && endIndex <= fullText.length()) {
                                cellText.append(fullText, startIndex, endIndex);
                            }
                        }
                        rowData.add(cellText.toString().trim());
                    }
                    tableData.add(rowData);
                }
            }
        }
        List<String> dates = extractDates(tableData);
        return tableData;
    }

    private static List<String> extractDates(List<List<String>> tableData) {
        List<String> strings = tableData.get(0);
        Pattern pattern = Pattern.compile("\\d{2}/\\d{2}\\([가-힣]\\)");
        List<String> dates = new ArrayList<>();
        for (String cell : strings) {
            Matcher matcher = pattern.matcher(cell);
            while (matcher.find()) {
                dates.add(matcher.group());
            }
        }
        return dates;
    }

}