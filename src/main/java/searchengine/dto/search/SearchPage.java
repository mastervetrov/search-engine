package searchengine.dto.search;

import lombok.Data;
import searchengine.model.IndexEntity;
import searchengine.model.PageEntity;

import java.util.List;

@Data
public class SearchPage {
    private PageEntity pageEntity;
    private List<IndexEntity> indexEntityList;
    private Double absoluteRelevance;
    private Double relativeRelevance;
}
