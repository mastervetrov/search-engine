package searchengine.service.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.data.SiteDto;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteEntity;
import searchengine.model.Status;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.service.data.LemmaService;
import searchengine.service.data.PageService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesList sites;
    @Autowired
    private SiteRepository siteJpaRepository;
    @Autowired
    private PageService pageService;
    @Autowired
    private PageRepository pageJpaRepository;
    @Autowired
    private LemmaService lemmaService;
    @Autowired
    private LemmaRepository lemmaJpaRepository;

    @Override
    public StatisticsResponse getStatistics() {
        if (siteJpaRepository.count() >= 0) {
            List<DetailedStatisticsItem> detailedStatisticsItems = new ArrayList<>();
            TotalStatistics total = new TotalStatistics();
            total.setSites(sites.getSites().size());
            total.setIndexing(true);
            for (int i = 1; i < siteJpaRepository.count() + 1; i++) {
                SiteEntity siteEntity = siteJpaRepository.findById(i).orElse(null);
                if (siteEntity == null) {
                    continue;
                }
                SiteDto siteDto = siteEntityToDto(siteJpaRepository.findByUrl(siteEntity.getUrl()));
                DetailedStatisticsItem item = new DetailedStatisticsItem();

                setDataForDetailedStatisticItem(item, siteDto);
                setDataForTotalStatistics(item, total);

                detailedStatisticsItems.add(item);
            }

            StatisticsResponse response = new StatisticsResponse();
            StatisticsData data = new StatisticsData();
            data.setTotal(total);
            data.setDetailed(detailedStatisticsItems);
            response.setStatistics(data);
            response.setResult(true);
            return response;
        }
        return getStatisticResponseIfSiteRepositoryIsEmptyOrNull();
    }

    private SiteDto siteEntityToDto(SiteEntity siteEntity) {
        SiteDto siteDto = new SiteDto();
        siteDto.setName(siteEntity.getName());
        siteDto.setStatus(siteEntity.getStatus());
        siteDto.setUrl(siteEntity.getUrl());
        siteDto.setId(siteEntity.getId());
        siteDto.setStatusTime(siteEntity.getStatusTime());
        siteDto.setLastError(siteEntity.getLastError());
        return siteDto;
    }

    private void setDataForDetailedStatisticItem(DetailedStatisticsItem item, SiteDto siteDto){
        Long pages = pageJpaRepository.countBySiteId((long) siteDto.getId());
        Long lemmas = lemmaJpaRepository.countBySiteId(siteDto.getId());

        item.setPages(pages.intValue());
        item.setLemmas(lemmas.intValue());

        item.setStatus(siteDto.getStatus().toString());
        item.setStatusTime(Instant.now().toEpochMilli());
        item.setError(siteDto.getLastError());
        item.setName(siteDto.getName());
        item.setUrl(siteDto.getUrl());
    }

    private void setDataForTotalStatistics(DetailedStatisticsItem item, TotalStatistics total){
        total.setPages(total.getPages() + item.getPages());
        total.setLemmas(total.getLemmas() + item.getLemmas());
    }

    private StatisticsResponse getStatisticResponseIfSiteRepositoryIsEmptyOrNull() {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setIndexing(true);
        totalStatistics.setPages(0);
        totalStatistics.setSites(sites.getSites().size());
        totalStatistics.setLemmas(0);

        data.setTotal(totalStatistics);
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (int i = 0; i < sites.getSites().size(); i++) {
            Site siteOfConfig = sites.getSites().get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setError("");
            item.setLemmas(0);
            item.setPages(0);
            item.setStatus(Status.INDEXING.toString());
            item.setUrl(siteOfConfig.getUrl());
            item.setName(siteOfConfig.getName());
            item.setStatusTime(Instant.now().toEpochMilli());
            detailed.add(item);
        }
        data.setDetailed(detailed);
        statisticsResponse.setStatistics(data);
        statisticsResponse.setResult(true);
        return statisticsResponse;
    }
}