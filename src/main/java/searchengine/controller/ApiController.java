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
     * Getting brief summary of indexed sites.
     *
     * @return ResponseEntity
     * StatisticResponse contains some nested objects:
     * StatisticData, TotalStatistics, DetailedStatisticItem
     */
    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return statisticsService.getStatistics();
    }

    /**
     * Initiation indexing process.
     * - case1: error     return ResultMessage("false", "error text")
     * - case2: success   return ResultMessage("true")
     *
     * @return ResponseEntity contains the operation execution status and the result message.
     * If indexing is activated, the 402 status and an error message are returned.
     * If indexing is activated successfully, the 200 (OK) status is returned with confirmation of revenue receipt.
     */
    @GetMapping("/startIndexing")
    public ResultMessage startIndexing() {
        indexingService.startIndexing();
        return new ResultMessage("true");
    }

    /**
     * Initiation stopping indexing process
     * - case1: error     return ResultMessage("false", "error text")
     * - case2: success   return ResultMessage("true")
     *
     * @return ResponseEntity contains the operation execution status and the result message.
     * If indexing is not activated, the 4029 status and an error message are returned.
     * If indexing is stopped successfully, the 200 (OK) status is returned with confirmation of revenue receipt.
     */
    @GetMapping("/stopIndexing")
    public ResultMessage stopIndexing() {
        indexingService.stopIndexing();
        return new ResultMessage("true");
    }

    /**
     * This method accepts url.
     * Further actions depend on the presence of the url in the database
     * - case1: database contains target site with specified url. Re-indexing target site
     * - case2: No target site with the specified URL was found. Adding the new site to the queue for indexing.
     *
     * @param url for check in database
     * @return ResponseEntity contains the operation execution status and the result message.
     * If indexing is activated, the 402 status and an error message are returned.
     * If there is no connection, return ResultMessage ("false")
     * If adding is successfully, the 200 (OK) status is returned with confirmation ResultMessage("true").
     */
    @PostMapping("/indexPage")
    public ResultMessage indexPage(@RequestBody String url) {
        indexingService.indexPage(url);
        return new ResultMessage("true");
    }

    /**
     * This method finds all pages where all words of the search query are present
     *
     * @param query search query consisting of words separated by space
     * @param site        optional for search by one site;
     * @param offset      optional. Offset from 0 for paginated result
     * @param limit       optional. the number of results to display (optional; if not set, the default value is 20).
     * @return ResponseEntity contains SearchResponse, contains List SearchSnippet, count results and boolean result.
     * Each snippet has relevance.
     * The higher the relevance of the snippet, the higher the snippet will appear in search results.
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
