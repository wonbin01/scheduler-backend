package wonbin.scheduler.Entity;

import com.google.cloud.documentai.v1.NormalizedVertex;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class CellInfo {
    private int cell_id;
    private String cell_text;
    private List<List<Float>> normalized_vertices;

    // NormalizedVertex 리스트를 받아서 CellInfo 생성
    public static CellInfo fromNormalizedVertices(int cellId, String text,
                                                  List<NormalizedVertex> vertices) {
        CellInfo info = new CellInfo();
        info.cell_id = cellId;
        info.cell_text = text;
        info.normalized_vertices = vertices.stream()
                .map(v -> List.of(v.getX(), v.getY()))
                .collect(Collectors.toList());
        return info;
    }
}
