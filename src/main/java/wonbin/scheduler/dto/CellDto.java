package wonbin.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import wonbin.scheduler.Entity.CellInfo;

@Data
public class CellDto {
    @JsonProperty("image_width")
    private int imageWidth;
    @JsonProperty("image_height")
    private int imageHeight;
    @JsonProperty("cells")
    private List<CellInfo> cells;
}
