package searchengine.service.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.config.JsoupProperties;
import searchengine.service.data.DataCleaner;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.model.Status;
import searchengine.service.data.ServiceGroup;
import searchengine.service.text.LemmaProcessor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexingManagerImpl implements IndexingManager {
    private final ServiceGroup serviceGroup;
    private final JsoupProperties jsoupProperties;
    private final PageConnector pageConnector;
    private final IndexingProcessor indexingProcessor;
    private final LemmaProcessor lemmaProcessor;
    private final DataCleaner dataCleanerService;
    public static List<RecursiveAction> indexingTaskList = new ArrayList<>();
    public static boolean indexingIsAllow = false;
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(32);

    @Async
    @Override
    public void start(List<SiteEntity> siteEntityList) {
        indexingIsAllow = true;
        cleanAll();
        createAndRunTasks(siteEntityList);
    }

    @Override
    public void stop() {
        indexingIsAllow = false;
    }

    public void cleanAll() {
        indexingTaskList.clear();
        RecursiveAction.resetCounter();
        indexingIsAllow = true;
    }


    @Override
    public boolean indexPage(SiteEntity siteEntity, String url) {
        IndexingServiceImpl.isRunning = true;
        String regexUrl = siteEntity.getUrl();

        regexUrl = regexUrl.replaceAll("http://www\\.", "http://");
        regexUrl = regexUrl.replaceAll("https://www\\.", "https://");
        url = url.replaceAll("http://www\\.", "http://");
        url = url.replaceAll("https://www\\.", "https://");

        regexUrl = Utils.makeShieldForSpecialCharacters(regexUrl);
        url = url.replaceAll(regexUrl, "////");
        url = url.replaceAll("/////", "/");
        url = url.replaceAll("////", "/");
        PageEntity pageEntity = serviceGroup.getPageService().findByUrl(url);
        if (pageEntity == null) {
            pageEntity = new PageEntity();
            pageEntity.setPath(url);
        }
        SimpleAction simpleAction = new SimpleAction(indexingProcessor, pageConnector, lemmaProcessor, pageEntity, siteEntity);
        boolean result = simpleAction.compute();
        IndexingServiceImpl.isRunning = false;
        return result;
    }

    /**
     * For every siteEntity is being created Informer and PageEntity
     * Each task launched here is linked to the previous one by Informer.
     * Informer also contains lists of processed pages, to avoid duplicate pages.
     * ForkJoinPools inside Completable is being carried out asynchronously
     *
     * @param siteEntityList contains List<SiteEntity>
     */
    private void createAndRunTasks(List<SiteEntity> siteEntityList) {
        log.warn("INDEXING STARTING");
        List<CompletableFuture<Void>> completableList = new ArrayList<>();
        for (SiteEntity siteEntity : siteEntityList) {
            PageEntity pageEntity = new PageEntity();
            pageEntity.setSite(siteEntity);
            pageEntity.setPath(siteEntity.getUrl());

            IndexingInformer informer = new IndexingInformer();
            informer.setPageEntity(pageEntity);
            informer.setSiteEntity(siteEntity);
            informer.setDomain(Utils.generateDomain(siteEntity.getUrl()));


            RecursiveAction action = new RecursiveAction(indexingProcessor, lemmaProcessor, pageConnector, informer);
            CompletableFuture<Void> completable = CompletableFuture.runAsync(() -> forkJoinPool.invoke(action));
            completable.thenRun(() -> indexingTaskResultAction(siteEntity));
            completableList.add(completable);
        }
        for (CompletableFuture<Void> completableFuture : completableList) {
            completableFuture.join();
        }
        log.warn("INDEXING COMPLETE");
        cleanAll();
        indexingIsAllow = false;
        IndexingServiceImpl.isRunning = false;
    }

    private void indexingTaskResultAction(SiteEntity siteEntity) {
        log.warn("Indexing " + siteEntity.getName() + " complete!");
        siteEntity.setStatusTime(Instant.now());
        if (siteEntity.getLastError().equals("")) {
            siteEntity.setStatus(Status.INDEXED);
            siteEntity.setStatusTime(Instant.now());
        }
        if (!siteEntity.getLastError().equals("")) {
            siteEntity.setStatus(Status.FAILED);
            siteEntity.setStatusTime(Instant.now());
        }
        serviceGroup.getSiteService().save(siteEntity);
    }
}
