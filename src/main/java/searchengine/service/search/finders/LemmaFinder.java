package searchengine.service.search.finders;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchUnit;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.LemmaRepository;
import searchengine.service.text.LemmaProcessor;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LemmaFinder {
    private static final Logger log = LoggerFactory.getLogger(LemmaFinder.class);
    private final int MAXIMUM_LEMMA_FREQUENCY_IN_PERCENTAGE = 100;
    private final LemmaRepository lemmaJpaRepository;
    private final LemmaProcessor lemmaProcessor;

    public List<SearchUnit> createSearchUnitList(String query, List<SiteEntity> targetSiteEntityList) {
        List<LemmaEntity> lemmaEntityList = new ArrayList<>();
        List<Integer> allSitesIds = getAllSiteIds(targetSiteEntityList);
        HashSet<String> allLemmas = lemmaProcessor.extractLemmas(query); //чехол телефон
        List<LemmaEntity> allLemmaEntity = lemmaJpaRepository.findBySiteIdInAndLemmaInAndFrequencyLessThanEqual(
                allSitesIds,
                allLemmas,
                MAXIMUM_LEMMA_FREQUENCY_IN_PERCENTAGE);
        HashMap<Integer, Integer> siteIdsAndLemmaCount = new HashMap<>();
        for (Integer siteId : allSitesIds) {
            siteIdsAndLemmaCount.put(siteId, 0);
        }

        List<Integer> siteIds = new ArrayList<>();
        for (LemmaEntity lemmaEntity : allLemmaEntity) {
            int key = lemmaEntity.getSite().getId();
            int newValue = siteIdsAndLemmaCount.get(key) + 1;
            siteIdsAndLemmaCount.put(key, newValue);
            if (newValue == allLemmas.size()) {
                siteIds.add(key);
            }
        }

        for (LemmaEntity lemmaEntity : allLemmaEntity) {
            if (siteIds.contains(lemmaEntity.getSite().getId())) {
                lemmaEntityList.add(lemmaEntity);
            }
        }

        return generateSearchUnitsSortedByRankMinToMax(lemmaEntityList);
    }

    private List<Integer> getAllSiteIds(List<SiteEntity> siteEntityList) {
        return siteEntityList.stream()
                .map(SiteEntity::getId)
                .toList();
    }

    private List<SearchUnit> generateSearchUnitsSortedByRankMinToMax(List<LemmaEntity> lemmaEntityList) {
        List<SearchUnit> searchUnitList = new ArrayList<>();
        Set<String> lemmasSet = new HashSet<>();
        for (LemmaEntity lemmaEntity : lemmaEntityList) {

            lemmasSet.add(lemmaEntity.getLemma());
        }
        for (String lemma : lemmasSet) {
            SearchUnit searchUnit = new SearchUnit();
            searchUnit.setCommonLemma(lemma);
            searchUnitList.add(searchUnit);
        }
        for (LemmaEntity lemmaEntity : lemmaEntityList) {
            for (SearchUnit searchUnit : searchUnitList) {
                if (searchUnit.getCommonLemma().equals(lemmaEntity.getLemma())) {
                    searchUnit.setCommonRank(searchUnit.getCommonRank() + lemmaEntity.getFrequency());
                    searchUnit.getLemmaEntities().add(lemmaEntity);
                }
            }
        }
        sortByRankMinToMax(searchUnitList);
        return searchUnitList;
    }

    private void sortByRankMinToMax(List<SearchUnit> searchUnitList) {
        searchUnitList.sort(Comparator.comparingDouble(SearchUnit::getCommonRank));
    }
}
