package searchengine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.service.indexing.IndexingService;
import searchengine.service.search.SearchService;
import searchengine.service.statistics.StatisticsService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;
    private static final int OFFSET_DEFAULT = 0;
    private static final int OFFSET_MIN = 0;
    private static final int LIMIT_DEFAULT = 20;
    private static final int LIMIT_MIN = 1;

    /**
     * GETTING BRIEF SUMMARY OF INDEXED SITES
     *
     * @return StatisticsResponse
     * Contains generalized information
     * and detailed information about each site
     */
    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return statisticsService.getStatistics();
    }

    /**
     * INDEXING LAUNCH
     *
     * @return ResulMessage contains "true" with status OK 200.
     * If indexing is already running, an exception will be thrown "IndexingIsAlreadyRunningException"
     * and return "ResultMessage("false", "Индексация уже запущена");"
     */
    @GetMapping("/startIndexing")
    public ResultMessage startIndexing() {
        indexingService.startIndexing();
        return new ResultMessage("true");
    }

    /**
     * INDEXING STOPPING
     *
     * @return ResulMessage contains "true" with status OK 200.
     * If indexing is already stopping, an exception will be thrown "IndexingIsAlreadyStoppedException"
     * and return "ResultMessage("false", "Индексация не запущена");"
     */
    @GetMapping("/stopIndexing")
    public ResultMessage stopIndexing() {
        indexingService.stopIndexing();
        return new ResultMessage("true");
    }

    /**
     * SINGLE PAGE INDEXATION
     *
     * <p>Further actions depend on the presence of the url in the database</p>
     * <p> case1: database contains target page with specified url. Re-indexing target site</p>
     * <p> case2: database not contains target page with specified url
     * and the page belongs to one of the sites in the configuration file. Indexing target site</p>
     *
     * @param url for check in database
     * @return ResulMessage contains "true" with status OK 200.
     *
     * <p>If indexing is already running,
     * an exception will be thrown "IndexingIsAlreadyRunningException"
     * and return "ResultMessage("false", "Индексация уже запущена");"</p>
     *
     * <p>If the page goes beyond the boundaries of the configuration file,
     * an exception is thrown "PageForIndexationIsOutsideTheConfigurationFileExceptionHandle"
     * and return "ResultMessage("false", "Указанная страница не связана ни с одним сайтом из конфигурации");"</p>
     */
    @PostMapping("/indexPage")
    public ResultMessage indexPage(@RequestBody String url) {
        indexingService.indexPage(url);
        return new ResultMessage("true");
    }

    /**
     * SEARCH BY QUERY (GENERAL AND SINGLE MODES)
     *
     * <p>This method finds all pages where each word of the search query are present</p>
     * <p>Implemented search on all sites: GENERAL MODE</p>
     * <p>Implemented search on one site: SINGLE MODE</p>
     *
     * @param query search query consisting of words separated by space
     * @param site        optional parameter. FOR SEARCH BY SINGLE MODE
     * @param offset      optional parameter. OFFSET from 0 for paginated result
     * @param limit       optional parameter. The number of results to display (if not set, the default value is 20).
     * @return SearchResponse
     * If an empty search query is passed, an exception is thrown EmptySearchQueryExceptionHandle
     * and return ResultMessage("false", "Пустой поисковой запрос");
     */
    @GetMapping("/search")
    public SearchResponse search(@RequestParam String query,
                                 @RequestParam(required = false) String site,
                                 @RequestParam(required = false) Integer offset,
                                 @RequestParam(required = false) Integer limit) {

        int actualOffset = offsetDefinition(offset);
        int actualLimit = limitDefinition(limit);
        SearchResponse response = searchService.search(query, site, actualOffset, actualLimit);
        return response;
    }

    private Integer offsetDefinition(Integer newOffset) {
        if (newOffset == null) {
            return OFFSET_DEFAULT;
        }
        if (newOffset < OFFSET_MIN) {
            throw new IllegalArgumentException();
        }
        return newOffset;
    }

    private Integer limitDefinition(Integer newLimit) {
        if (newLimit == null) {
            return LIMIT_DEFAULT;
        }
        if (newLimit < LIMIT_MIN) {
            throw new IllegalArgumentException();
        }
        return newLimit;
    }
}
