package searchengine.service.search;

import searchengine.dto.search.SearchResponse;

public interface SearchService {
    SearchResponse search(String query, String site, Integer actualOffset, Integer actualLimit);
}
