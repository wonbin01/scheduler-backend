package wonbin.scheduler.Entity;

import com.google.cloud.documentai.v1.NormalizedVertex;
import java.util.List;
import lombok.Data;

@Data
public class CellInfo {
    private String cellText;
    private List<NormalizedVertex> normalizedVerticesList;

    public CellInfo(String cellText, List<NormalizedVertex> normalizedVerticesList) {
        this.cellText = cellText;
        this.normalizedVerticesList = normalizedVerticesList;
    }
}
