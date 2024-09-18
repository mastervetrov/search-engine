package searchengine.dto.search;

import lombok.Data;


@Data
public class SearchSentence {
    String content;
    String snippet;
    int rank = 0;
}
