package searchengine.dto.search;

import lombok.Data;

@Data
public class SearchSnippet {
    private String siteName;
    private String site;
    private String uri;
    private String title;
    private String snippet;
    private double relevance;
}
