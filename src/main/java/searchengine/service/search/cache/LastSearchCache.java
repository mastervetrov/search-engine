package searchengine.service.search.cache;

import lombok.Data;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchPage;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.search.SearchSnippet;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class LastSearchCache {
    private SearchResponse searchResponse = new SearchResponse();
    List<SearchSnippet> generatedSnippets = new ArrayList<>();
    List<SearchPage> searchPagesListNext = new ArrayList<>();
    private String query = null;
    private String url = null;
}
