package searchengine.dto.data;

import lombok.Data;

@Data
public class LemmaDto {
    Integer id;
    SiteDto siteDto;
    String lemma;
    Integer frequency;
}
