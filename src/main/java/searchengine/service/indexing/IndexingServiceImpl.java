package searchengine.service.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.exception.IndexingIsAlreadyRunningException;
import searchengine.exception.IndexingIsAlreadyStoppedException;
import searchengine.exception.PageForIndexationIsOutsideTheConfigurationFileException;
import searchengine.model.StatusString;
import searchengine.service.data.DataCleaner;
import searchengine.model.SiteEntity;
import searchengine.service.data.SiteService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    public static boolean isRunning = false;
    private final IndexingManager manager;
    private final DataCleaner dataCleaner;
    private final SiteService siteService;

    @Autowired
    private SitesList sitesList;

    /**
     * START INDEXING
     *
     * <p>This method performs preparation before indexing and execute method start of IndexingManager.
     * IndexingManager runs indexing tasks for sites specified in resources -> application.yml in parallel using ForkJoinPool</p>
     * <p>If indexing is already running, throw IndexingIsAlreadyRunningException<p>
     */
    @Override
    public void startIndexing() {
        if (isRunning) {
            throw new IndexingIsAlreadyRunningException();
        }
        isRunning = true;
        List<SiteEntity> siteEntityList = preparation(sitesList);
        manager.start(siteEntityList);
    }

    /**
     * START INDEXING BY URL (REINDEXING PAGE OR INDEXING PAGE)
     *
     * <p>This method performs preparation before indexing</p>
     * <p>If indexing is already running, throw IndexingIsAlreadyRunningException<p>
     *
     * @param targetUrl target page url
     * CONDITION: the target page must contain the domain of one of the sites specified in application.yml
     * ADDITIONALLY: method contains code fore decoding url and code for check domain
     */
    @Override
    public void indexPage(String targetUrl) {
        if (isRunning) {
            throw new IndexingIsAlreadyRunningException();
        }
        isRunning = true;
        boolean linkAllow = false;
        String decodeRequest = URLDecoder.decode(targetUrl, StandardCharsets.UTF_8);
        String decodeTargetUrl = decodeRequest.replaceAll("url=", "");

        manager.cleanAllTasks();
        String linkDomain = Utils.generateDomain(decodeTargetUrl);
        String targetSite = null;
        for (Site site : sitesList.getSites()) {
            String siteDomain = Utils.generateDomain(site.getUrl());
            if (linkDomain.equals(siteDomain)) {
                linkAllow = true;
                targetSite = site.getUrl();
                break;
            }
        }
        SiteEntity siteEntity = siteService.findByUrl(targetSite);
        if (linkAllow) {
            preparationByUrl(decodeTargetUrl);
            boolean result =  manager.indexPage(siteEntity, decodeTargetUrl);
            if (result) {
                return;
            }
        }
        throw new PageForIndexationIsOutsideTheConfigurationFileException();
    }

    /**
     * STOP INDEXING (SOFTLY)
     *
     * <p>If indexing is already stopping, throw IndexingIsAlreadyStoppedException<p>
     *
     * To stop indexing, necessary switch isRunning to false and call the IndexingManager stop method.
     * Stopping indexing is called softly with the termination of all tasks.
     */
    @Override
    public void stopIndexing() {
        if (!isRunning) {
            throw new IndexingIsAlreadyStoppedException();
        }
        isRunning = false;
        manager.stop();
    }

    private List<SiteEntity> preparation(SitesList sitesList) {
        dataCleaner.cleanAllRepository();
        manager.cleanAllTasks();
        List<Site> siteListOfConfig = sitesList.getSites();
        List<SiteEntity> siteEntityList = getSiteEntityListAndSaveInDatabase(siteListOfConfig);
        return siteEntityList;
    }

    private void preparationByUrl(String url) {
        dataCleaner.cleanPageByUrl(url);
    }

    private List<SiteEntity> getSiteEntityListAndSaveInDatabase(List<Site> siteList) {
        List<SiteEntity> siteEntityList = new ArrayList<>();
        for (Site site : siteList) {
            SiteEntity siteEntity = new SiteEntity();
            siteEntity.setName(site.getName());
            siteEntity.setUrl(site.getUrl());
            siteEntity.setStatus(StatusString.INDEXING);
            siteEntity.setStatusTime(Instant.now());
            siteEntity.setLastError("");
            siteService.save(siteEntity);
            siteEntityList.add(siteEntity);
        }
        return siteEntityList;
    }
}
