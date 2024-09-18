package searchengine.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.search.SearchUnit;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.PageRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageFinder {
    private final IndexRepository indexJpaRepository;
    private final PageRepository pageJpaRepository;

    @Autowired
    public PageFinder(IndexRepository indexJpaRepository, PageRepository pageJpaRepository) {
        this.indexJpaRepository = indexJpaRepository;
        this.pageJpaRepository = pageJpaRepository;
    }

    public List<PageEntity> findPageEntityListBySearchUnitList(List<SearchUnit> searchUnitList) {
        if (searchUnitList == null || searchUnitList.isEmpty()) {
            return new ArrayList<>();
        }
        List<IndexEntity> maximumPossibleIndexes = new ArrayList<>();
        List<LemmaEntity> maximumLemmas = searchUnitList.get(0).getLemmaEntities();
        for (LemmaEntity lemmaEntity : maximumLemmas) {
            maximumPossibleIndexes = indexJpaRepository.findByLemmaId(lemmaEntity.getId());
        }
        List<Integer> maximumPossibleIdsPages = findPagesIdsByIndexEntities(maximumPossibleIndexes);
        List<PageEntity> maximumPossiblePages = pageJpaRepository.findByIdIn(maximumPossibleIdsPages);
        List<SearchUnit> searchUnitListWithoutFirstElement = new ArrayList<>(searchUnitList);
        searchUnitListWithoutFirstElement.remove(0);
        List<PageEntity> realPageEntityList = selectValidPagesBySearchPagelist(maximumPossiblePages, searchUnitListWithoutFirstElement);
        return realPageEntityList;
    }

    private List<Integer> findPagesIdsByIndexEntities(List<IndexEntity> indexEntities) {
        return indexEntities.stream()
                .map(IndexEntity::getPage)
                .map(PageEntity::getId)
                .toList();
    }

    private List<PageEntity> selectValidPagesBySearchPagelist(List<PageEntity> pageEntityList, List<SearchUnit> searchUnitList) {
        if (searchUnitList == null || searchUnitList.isEmpty()) {
            return pageEntityList;
        }
        List<Integer> pageEntityIds = pageEntityList.stream()
                .map(PageEntity::getId)
                .toList();

        List<IndexEntity> indexEntityList = new ArrayList<>();
        for (LemmaEntity lemmaEntity : searchUnitList.get(0).getLemmaEntities()) {
            indexEntityList = indexJpaRepository.findByPageIdInAndLemmaId(pageEntityIds, lemmaEntity.getId());
        }

        searchUnitList.remove(0);
        pageEntityIds = findPagesIdsByIndexEntities(indexEntityList);
        pageEntityList = pageJpaRepository.findByIdIn(pageEntityIds);
        pageEntityList = selectValidPagesBySearchPagelist(pageEntityList, searchUnitList);
        return pageEntityList;
    }
}
