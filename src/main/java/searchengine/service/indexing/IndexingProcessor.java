package searchengine.service.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.service.data.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IndexingProcessor {
    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;

    public void processing(HashMap<String, Integer> lemmasAndRankHashMapNew, SiteEntity siteEntity, PageEntity pageEntity) {
        siteEntity.setStatus(StatusString.INDEXING);
        siteEntity.setStatusTime(Instant.now());
        siteService.save(siteEntity);
        pageService.save(pageEntity);
        for (Map.Entry<String, Integer> entry : lemmasAndRankHashMapNew.entrySet()) {
            String lemma = entry.getKey();
            Integer rank = entry.getValue();

            LemmaEntity lemmaEntity = lemmaService.findByLemmaAndSiteId(lemma, siteEntity.getId());
            if (lemmaEntity != null) {
                lemmaEntity.setFrequency(lemmaEntity.getFrequency() + 1);
            }
            if (lemmaEntity == null) {
                lemmaEntity = new LemmaEntity();
                lemmaEntity.setLemma(lemma);
                lemmaEntity.setFrequency(1);
                lemmaEntity.setSite(siteEntity);
                lemmaService.save(lemmaEntity);
            }
            IndexEntity indexEntity = new IndexEntity();
            indexEntity.setRank(rank.floatValue());
            indexEntity.setLemma(lemmaEntity);
            indexEntity.setPage(pageEntity);
            indexService.save(indexEntity);
        }
    }

    public void stopAction(SiteEntity siteEntity) {
        siteEntity.setStatus(StatusString.FAILED);
        siteEntity.setLastError("Индексация остановлена пользователем");
        siteService.save(siteEntity);
    }
}
