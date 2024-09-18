package searchengine.dto.search;

import lombok.Data;
import searchengine.model.LemmaEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchUnit {
    String commonLemma;
    Double commonRank = 0.0;
    List<LemmaEntity> lemmaEntities = new ArrayList<>();

}
