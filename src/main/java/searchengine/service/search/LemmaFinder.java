package searchengine.service.search;

import lombok.RequiredArgsConstructor;
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
    private final int MAXIMUM_LEMMA_FREQUENCY_IN_PERCENTAGE = 100;
    private final LemmaRepository lemmaJpaRepository;
    private final LemmaProcessor lemmaProcessor;

    public List<SearchUnit> createSearchUnitList(String query, List<SiteEntity> targetSiteEntityList) {

        List<Integer> allSitesIds = getAllSiteIds(targetSiteEntityList);
        HashSet<String> allLemmas = lemmaProcessor.extractLemmas(query);
        List<LemmaEntity> allLemmaEntity = lemmaJpaRepository.findBySiteIdInAndLemmaInAndFrequencyLessThanEqual(
                allSitesIds,
                allLemmas,
                MAXIMUM_LEMMA_FREQUENCY_IN_PERCENTAGE);

        return generateSearchUnitsSortedByRankMinToMax(allLemmaEntity);
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
