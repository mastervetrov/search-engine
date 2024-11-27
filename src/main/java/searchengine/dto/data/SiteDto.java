package searchengine.dto.data;

import lombok.Data;

import java.time.Instant;

@Data
public class SiteDto {
    private Integer id;
    private String status;
    private Instant statusTime;
    private String lastError;
    private String url;
    private String name;
}
