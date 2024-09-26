package searchengine.dto.search;

import lombok.Data;


@Data
public class SearchSentence {
    private String content;
    private String snippet;
    private Integer rank = 0;
}
