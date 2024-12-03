package searchengine.service.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.search.*;
import searchengine.exception.EmptySearchQueryException;
import searchengine.model.IndexEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.service.search.finders.IndexFinder;
import searchengine.service.search.finders.LemmaFinder;
import searchengine.service.search.finders.PageFinder;
import searchengine.service.search.finders.SiteFinder;

import java.util.*;

@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SiteFinder siteFinder;
    private final PageFinder pageFinder;
    private final LemmaFinder lemmaFinder;
    private final IndexFinder indexFinder;
    private final SnippetGenerator snippetGenerator;
    private final CacheManager cacheManager;

    /**
     * SEARCH
     *
     * <p>The cache stores data from the ONE previous request.
     * Data is loaded from the cache and processed in portions,
     * in accordance with the limit</p>
     *
     * @param query search request, words separated by spaces (For example "чехол для смартфона")
     * @param url for search in SINGLE_MODE. Must match one of the sites resources -> Application.yml.
     *            If the url is not specified, the search occurs in FULL_MODE
     * @param offset offset
     * @param limit limit
     * @return SearchResponse
     */
    @Override
    public SearchResponse search(String query, String url, Integer offset, Integer limit) {
        if (query.isEmpty()) {
            throw new EmptySearchQueryException();
        }
        SearchResponse searchResponse = cacheManager.checkAndGet(query, url);
        if (searchResponse != null) {
            List<SearchSnippet> searchSnippetList = snippetGenerator.generateSnippetsResponseUsingTheCache(limit);
            searchResponse.setData(searchSnippetList);
            return searchResponse;
        }
        cacheManager.cleanCache();
        searchResponse = shapingSearchResponse(query, url, offset, limit);
        cacheManager.saveSearchResponse(query, url, searchResponse);
        return searchResponse;
    }

    private SearchResponse shapingSearchResponse(String query, String url, Integer offset, Integer limit) {
        boolean singleMode = url != null;
        boolean fullMode = url == null;

        List<SiteEntity> targetSiteEntityList = new ArrayList<>();

        if (singleMode) {
            log.info("SINGLE_MODE SEARCH STARTING");
            targetSiteEntityList.add(siteFinder.findByUrl(url));
        }
        if (fullMode) {
            log.info("FULL_MODE SEARCH STARTING");
            targetSiteEntityList.addAll(siteFinder.findAll());
        }
        return processing(targetSiteEntityList, query, offset, limit);
    }

    private SearchResponse processing(List<SiteEntity> targetSiteEntityList, String query, Integer offset, Integer limit) {
        SearchResponse searchResponse = new SearchResponse();
        List<SearchUnit> allSearchUnits = lemmaFinder.createSearchUnitList(query, targetSiteEntityList);

        if (allSearchUnits.isEmpty()) {
            searchResponse.setResult(true);
            searchResponse.setCount(0);
            searchResponse.setData(new ArrayList<>());
            return searchResponse;
        }

        List<PageEntity> allPages = pageFinder.findPageEntityListBySearchUnitList(allSearchUnits);
        List<IndexEntity> allIndexes = indexFinder.findAllIndexEntityBySearchUnitsAndPageEntities(allSearchUnits, allPages);
        List<SearchPage> searchPages = indexFinder.generateSearchPages(allIndexes, allPages);
        int count = searchPages.size();
        List<SearchSnippet> searchSnippetList = snippetGenerator.generateSnippetsResponse(searchPages, offset, limit);
        searchSnippetList.sort(Comparator.comparing(SearchSnippet::getRelevance).reversed());

        searchResponse.setResult(true);
        searchResponse.setCount(count);
        searchResponse.setData(searchSnippetList);
        return searchResponse;
    }
}
