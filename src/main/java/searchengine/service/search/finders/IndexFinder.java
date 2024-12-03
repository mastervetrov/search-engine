package searchengine.service.search.finders;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchPage;
import searchengine.dto.search.SearchUnit;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.repository.IndexRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IndexFinder {
    private final IndexRepository indexJpaRepository;

    public List<IndexEntity> findValidIndexesForPageEntity(List<IndexEntity> indexEntities, PageEntity pageEntity, SearchPage searchPage) {
        List<IndexEntity> validIndexEntity = new ArrayList<>();
        for (IndexEntity index : indexEntities) {
            if (pageEntity.getId().equals(index.getPage().getId())) {
                validIndexEntity.add(index);
            }
        }
        return validIndexEntity;
    }

    public List<IndexEntity> findAllIndexEntityBySearchUnitsAndPageEntities(List<SearchUnit> searchUnitList, List<PageEntity> pageEntityList) {
        List<LemmaEntity> allLemmas = new ArrayList<>();
        for (SearchUnit unit : searchUnitList) {
            allLemmas.addAll(unit.getLemmaEntities());
        }
        List<Integer> pagesIds = getPagesIds(pageEntityList);
        List<Integer> lemmasIds = getLemmasIds(allLemmas);
        return indexJpaRepository.findByPageIdInAndLemmaIdIn(pagesIds, lemmasIds);
    }

    private List<Integer> getLemmasIds(List<LemmaEntity> lemmaEntityList) {
        return lemmaEntityList.stream()
                .map(LemmaEntity::getId)
                .toList();
    }

    private List<Integer> getPagesIds(List<PageEntity> pageEntityList) {
        return pageEntityList.stream()
                .map(PageEntity::getId)
                .toList();
    }

    public List<SearchPage> generateSearchPages(List<IndexEntity> indexEntities, List<PageEntity> pageEntities) {
        double maxRelevance = 0;
        List<SearchPage> searchPages = new ArrayList<>();
        for (PageEntity pageEntity : pageEntities) {
            SearchPage searchPage = new SearchPage();
            List<IndexEntity> validIndexEntity = findValidIndexesForPageEntity(indexEntities, pageEntity, searchPage);
            double absoluteRelevance = calculateAbsoluteRelevanceSearchPage(validIndexEntity);
            searchPage.setAbsoluteRelevance(absoluteRelevance);
            searchPage.setIndexEntityList(validIndexEntity);
            searchPage.setPageEntity(pageEntity);
            if (searchPage.getAbsoluteRelevance() > maxRelevance) {
                maxRelevance = searchPage.getAbsoluteRelevance();
            }
            searchPages.add(searchPage);
        }
        for (SearchPage searchPage : searchPages) {
            searchPage.setRelativeRelevance(searchPage.getAbsoluteRelevance() / maxRelevance);
        }
        searchPages.sort(Comparator.comparingDouble(SearchPage::getRelativeRelevance).reversed());
        return searchPages;
    }

    private double calculateAbsoluteRelevanceSearchPage(List<IndexEntity> indexEntities) {
        double relevance = 0;
        for (IndexEntity indexEntity : indexEntities) {
            relevance = relevance + indexEntity.getRank();
        }
        return relevance;
    }
}
