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

    @Override
    public SearchResponse search(String query, String url, Integer offset, Integer limit) {
        if (query.isEmpty()) {
            throw new EmptySearchQueryException();
        }
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResult(true);
        searchResponse.setData(new ArrayList<>());
        searchResponse.setCount(0);

        List<SearchSnippet> searchSnippetList = generateSnippetListByUrl(query, url);
        if (searchSnippetList.isEmpty()|| searchSnippetList == null) {
            return searchResponse;
        }
        searchResponse.setResult(true);
        searchResponse.setCount(searchSnippetList.size());
        searchResponse.setData(searchSnippetList);
        return searchResponse;
    }

    private List<SearchSnippet> generateSnippetListByUrl(String query, String url) {
        boolean singleMode = url != null;
        boolean fullMode = url == null;

        List<SiteEntity> targetSiteEntityList = new ArrayList<>();

        if (singleMode) {
            targetSiteEntityList.add(siteFinder.findByUrl(url));
        }
        if (fullMode) {
            targetSiteEntityList.addAll(siteFinder.findAll());
        }
        List<SearchSnippet> result = findBySiteEntityList(targetSiteEntityList, query);
        return result;
    }

    private List<SearchSnippet> findBySiteEntityList(List<SiteEntity> targetSiteEntityList, String query) {
        List<SearchUnit> allSearchUnits = lemmaFinder.createSearchUnitList(query, targetSiteEntityList);
        if (allSearchUnits.isEmpty() || allSearchUnits.isEmpty()) {
            return new ArrayList<>();
        }
        List<PageEntity> allPages = pageFinder.findPageEntityListBySearchUnitList(allSearchUnits);
        List<IndexEntity> allIndexes = indexFinder.findAllIndexEntityBySearchUnitsAndPageEntities(allSearchUnits, allPages);
        List<SearchPage> searchPages = indexFinder.generateSearchPages(allIndexes, allPages);
        List<SearchSnippet> searchSnippetList = snippetGenerator.generateSnippetsResponse(searchPages);

        searchSnippetList.sort(Comparator.comparing(SearchSnippet::getRelevance).reversed());
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResult(true);
        searchResponse.setData(searchSnippetList);
        searchResponse.setCount(allPages.size());

        return searchSnippetList;
    }
}
