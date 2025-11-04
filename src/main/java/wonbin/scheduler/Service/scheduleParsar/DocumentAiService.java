package wonbin.scheduler.Service.scheduleParsar;


import org.springframework.beans.factory.annotation.Value;

public class DocumentAiService {
    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.documentai.processor.id}")
    private String processorId;
    @Value("${gcp.documentai.processor.location}")
    private String location;

}
