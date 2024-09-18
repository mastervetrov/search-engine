package searchengine.dto.data;
import lombok.Data;

@Data
public class PageDto {
    Integer id;
    SiteDto siteDto;
    String path;
    Integer code;
    String content;
}
