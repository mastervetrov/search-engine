package searchengine.dto.search;

import lombok.Data;
import searchengine.model.IndexEntity;
import searchengine.model.PageEntity;

import java.util.List;

@Data
public class SearchPage {
    PageEntity pageEntity;
    List<IndexEntity> indexEntityList;
    double absoluteRelevance;
    double relativeRelevance;
}
