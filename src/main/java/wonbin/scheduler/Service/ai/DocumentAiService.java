package wonbin.scheduler.Service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.Document.Page;
import com.google.cloud.documentai.v1.Document.Page.Table;
import com.google.cloud.documentai.v1.Document.Page.Table.TableCell;
import com.google.cloud.documentai.v1.Document.Page.Table.TableRow;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.NormalizedVertex;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import wonbin.scheduler.Entity.CellInfo;
import wonbin.scheduler.dto.CellDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentAiService {
    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.documentai.processor.id}")
    private String processorId; // Form Parser ID
    @Value("${gcp.documentai.processor.location}")
    private String location;

    private final GeminiService geminiService;
    static List<CellInfo> cells = new ArrayList<>();

    //각 셀의 텍스트와 색상을 추출하고 이를 LLM에게 보내, json 형식으로 return
    public String extractSchedule(MultipartFile file) throws IOException {
        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create()) {
            String processorName = getProcessorName();
            ByteString content = ByteString.copyFrom(file.getBytes());

            RawDocument document = getRawDocument(file, content);
            ProcessRequest request = getProcessRequest(processorName, document);
            ProcessResponse response = client.processDocument(request);
            Document doc = response.getDocument();
            List<CellInfo> cellInfos = extractCellsWithText(doc);
            int[] dimensionInfo = extractDimensinos(file);
            CellDto cellDto = getCellDto(dimensionInfo, cellInfos);

            String resColorInfo = sendCellDto(cellDto, file);
            return geminiService.callGEMINI(cellDto, file, resColorInfo);
        }
    }

    @NotNull
    private CellDto getCellDto(int[] dimensionInfo, List<CellInfo> cellInfos) {
        CellDto cellDto = new CellDto();
        cellDto.setImageWidth(dimensionInfo[0]);
        cellDto.setImageHeight(dimensionInfo[1]);
        cellDto.setCells(cellInfos);
        return cellDto;
    }

    @NotNull
    private ProcessRequest getProcessRequest(String processorName, RawDocument document) {
        return ProcessRequest.newBuilder()
                .setName(processorName)
                .setRawDocument(document)
                .build();
    }

    @NotNull
    private RawDocument getRawDocument(MultipartFile file, ByteString content) {
        return RawDocument.newBuilder()
                .setContent(content)
                .setMimeType(file.getContentType() != null ? file.getContentType() : "image/png")
                .build();
    }

    @NotNull
    private String getProcessorName() {
        return String.format(
                "projects/%s/locations/%s/processors/%s",
                projectId, location, processorId
        );
    }

    public String sendCellDto(CellDto cellDto, MultipartFile file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String cellDtoJson = objectMapper.writeValueAsString(cellDto);

        MultiValueMap<String, Object> body = getRequestBody(file, cellDtoJson);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return getCellColorInfo(restTemplate, requestEntity);
    }

    @Nullable
    private String getCellColorInfo(RestTemplate restTemplate,
                                    HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        String colorExtractorUrl = "http://color-extractor-container:8001/extract/colorInfo";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(colorExtractorUrl, requestEntity,
                    String.class);
            System.out.println("Response status: " + response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.err.println("Error response: " + ex.getResponseBodyAsString());
            throw ex;
        }
    }

    @NotNull
    private MultiValueMap<String, Object> getRequestBody(MultipartFile file, String cellDtoJson)
            throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("document_data_json", cellDtoJson); // JSON DTO
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("image_file", fileResource);
        return body;
    }

    public int[] extractDimensinos(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image != null) {
            int height = image.getHeight();
            int width = image.getWidth();
            return new int[]{width, height};
        }
        return new int[]{0, 0};
    }

    private List<CellInfo> extractCellsWithText(Document document) {
        int idCounter = 0;
        for (Page page : document.getPagesList()) {
            for (Table table : page.getTablesList()) {
                idCounter = getCellText(table.getBodyRowsList(), document, cells, idCounter);
                idCounter = getCellText(table.getHeaderRowsList(), document, cells, idCounter);
            }
        }
        return cells;
    }

    private int getCellText(List<TableRow> table, Document document, List<CellInfo> cells, int cellId) {
        for (TableRow row : table) {
            for (TableCell cell : row.getCellsList()) {
                cellId = getCellDetailInfo(document, cells, cellId, cell);
            }
        }
        return cellId;
    }

    //각 셀에서 실제 text 추출 및 각 셀의 좌표받아오는 메서드
    private int getCellDetailInfo(Document document, List<CellInfo> cells, int cellId, TableCell cell) {
        if (cell.hasLayout()) {
            StringBuilder cellText = new StringBuilder();
            for (Document.TextAnchor.TextSegment segment : cell.getLayout().getTextAnchor()
                    .getTextSegmentsList()) {
                int start = (int) segment.getStartIndex();
                int end = (int) segment.getEndIndex();
                cellText.append(document.getText().substring(start, end));
            }
            String finalCellText = cellText.toString();
            if (finalCellText.trim().isEmpty()) {
                return cellId;
            }
            List<NormalizedVertex> normalizedVerticesList = cell.getLayout().getBoundingPoly()
                    .getNormalizedVerticesList();
            cells.add(CellInfo.fromNormalizedVertices(cellId++, finalCellText, normalizedVerticesList));
        }
        return cellId;
    }

}