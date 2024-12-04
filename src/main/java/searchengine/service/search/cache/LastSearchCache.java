package searchengine.service.search.cache;

import lombok.Data;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchPage;
import searchengine.dto.search.SearchResponse;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class LastSearchCache {
    private SearchResponse searchResponse = new SearchResponse();
    List<SearchPage> searchPagesList = new ArrayList<>();
    private String query = null;
    private String url = null;
}
