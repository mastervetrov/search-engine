package searchengine.dto.data;

import lombok.Data;
import searchengine.model.Status;

import java.time.Instant;

@Data
public class SiteDto {
    private Integer id;
    private Enum<Status> status;
    private Instant statusTime;
    private String lastError;
    private String url;
    private String name;
}
