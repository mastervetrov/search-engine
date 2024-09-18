package searchengine.service.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Component
@Slf4j
public class IndexingInformer {
    private List<IndexingInformer> childTaskInformers = new ArrayList<>();
    private HashSet<String> processedUrls = new HashSet<>();
    private HashSet<String> lemmaProcessedHashSet = new HashSet<>();
    private IndexingInformer parentTaskInformer;
    private String domain;
    private SiteEntity siteEntity;
    private PageEntity pageEntity;
    private int currentLevel = 0;
    private int code;

    public IndexingInformer(PageEntity pageEntity, IndexingInformer parentTaskInformer) {
        this.siteEntity = parentTaskInformer.siteEntity;
        this.pageEntity = pageEntity;
        this.parentTaskInformer = parentTaskInformer;
        this.currentLevel = parentTaskInformer.getCurrentLevel() + 1;
        this.domain = parentTaskInformer.getDomain();
    }

    public void addChildSite(String url) {
        PageEntity newPageEntity = new PageEntity();
        newPageEntity.setPath(url);
        this.childTaskInformers.add(new IndexingInformer(newPageEntity, this));
    }

}
