package searchengine.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCleanerImpl implements DataCleaner {
    private final IndexRepository indexJpaRepository;
    private final LemmaRepository lemmaJpaRepository;
    private final PageRepository pageJpaRepository;
    private final SiteRepository siteJpaRepository;
    private final LemmaService lemmaService;
    /**
     * clearing and resetting autoincrement tables "index", "lemma", "page", "site"
     */
    public void cleanAllRepository() {
        log.warn("CLEAN REPOSITORIES STARTING");
        Instant start = Instant.now();

        indexJpaRepositoryClean();
        lemmaJpaRepositoryClean();
        pageJpaRepositoryClean();
        siteJpaRepositoryClean();

        long executionTime = Duration.between(start, Instant.now()).toMillis();
        log.warn("CLEAN REPOSITORIES COMPLETE. Execution time: " + executionTime + " ms");
    }

    /**
     * clearing without resetting autoincrement tables "index", "lemma", "page" by url
     *
     * @param url to remove all related items with the "site" by url
     */
    @Override
    @Transactional
    public void cleanPageByUrl(String url) {
        log.warn("PAGE CLEANING BY URL STARTING: " + url);
        PageEntity pageEntity = pageJpaRepository.findByPath(url);
        if (pageEntity == null) {
            log.warn("PAGE CLEANING COMPLETED: Page not exist in repository");
            return;
        }
        List<IndexEntity> indexEntityList = indexJpaRepository.findByPageId(pageEntity.getId());

        List<Integer> lemmaIds = indexEntityList.stream()
                .map(IndexEntity::getLemma)
                .map(LemmaEntity::getId)
                .toList();

        List<Integer> indexIds = indexEntityList.stream()
                        .map(IndexEntity::getId)
                                .toList();

        indexJpaRepository.deleteByIdIn(indexIds);

        for (Integer id : lemmaIds) {
            lemmaService.decreaseFrequencyOrDeleteLemma(id);
        }
        log.warn("PAGE CLEANING BY URL COMPLETED: " + url);

    }

    @Transactional
    private void indexJpaRepositoryClean() {
        indexJpaRepository.deleteAllCustom();
        indexJpaRepository.resetSequence();
        log.warn("index cleaned");
    }

    @Transactional
    private void lemmaJpaRepositoryClean() {
        lemmaJpaRepository.deleteAllCustom();
        lemmaJpaRepository.resetSequence();
        log.warn("lemma cleaned");
    }

    @Transactional
    private void pageJpaRepositoryClean() {
        pageJpaRepository.deleteAllCustom();
        pageJpaRepository.resetSequence();
        log.warn("page cleaned");
    }

    @Transactional
    private void siteJpaRepositoryClean() {
        siteJpaRepository.deleteAllCustom();
        siteJpaRepository.resetSequence();
        log.warn("site cleaned");
    }
}
