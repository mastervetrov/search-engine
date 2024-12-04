package searchengine.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchPage;
import searchengine.dto.search.SearchResponse;
import searchengine.service.search.cache.LastSearchCache;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheManager {
    private final LastSearchCache lastSearchCache;

    protected SearchResponse checkAndGet(String query, String url) {
        if (lastSearchCache.getQuery() == null) { return null;}
        boolean condition1 = url != null && lastSearchCache.getUrl() != null;
        boolean condition2 = url == null && lastSearchCache.getUrl() == null;

        if (condition1) {
            if (lastSearchCache.getQuery().equals(query) && lastSearchCache.getUrl().equals(url)) {
                return lastSearchCache.getSearchResponse();
            }
        }

        if (condition2) {
            if (lastSearchCache.getQuery().equals(query)) {
                return lastSearchCache.getSearchResponse();
            }
        }
        return null;
    }

    protected void saveSearchResponse(String query, String url, SearchResponse searchResponse) {
        lastSearchCache.setSearchResponse(searchResponse);
        lastSearchCache.setQuery(query);
        lastSearchCache.setUrl(url);
    }

    protected void cleanCache() {
        lastSearchCache.setSearchResponse(new SearchResponse());
        lastSearchCache.setQuery(null);
        lastSearchCache.setUrl(null);
    }


    protected void saveSearchPageList(List<SearchPage> searchPageList) {
        lastSearchCache.setSearchPagesList(searchPageList);
    }

    protected List<SearchPage> getSearchPageList() {
        return lastSearchCache.getSearchPagesList();
    }
}
