package searchengine.dto.data;

import lombok.Data;
import searchengine.model.Status;

import java.time.Instant;

@Data
public class SiteDto {
    Integer id;
    Enum<Status> status;
    Instant statusTime;
    String lastError;
    String url;
    String name;
}
