package searchengine.dto.data;

import lombok.Data;

@Data
public class IndexDto {
    Integer id;
    PageDto pageDto;
    LemmaDto lemmaDto;
    Float rank;
}
