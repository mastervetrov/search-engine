package searchengine.dto.search;

import lombok.Data;
import searchengine.model.LemmaEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchUnit {
    private String commonLemma;
    private Double commonRank = 0.0;
    private List<LemmaEntity> lemmaEntities = new ArrayList<>();

}
