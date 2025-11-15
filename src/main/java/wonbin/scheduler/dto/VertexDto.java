package wonbin.scheduler.dto;

import com.google.cloud.documentai.v1.NormalizedVertex;
import java.util.List;
import lombok.Data;

@Data
public class VertexDto {
    private float x;
    private float y;

    public VertexDto(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public VertexDto fromNormalizedVertex(NormalizedVertex v) {
        return new VertexDto(v.getX(), v.getY());
    }

    public List<Float> toList() {
        return List.of(x, y);
    }
}
