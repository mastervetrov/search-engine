package searchengine.service.data;

import org.springframework.stereotype.Service;
import searchengine.dto.data.LemmaDto;
import searchengine.model.LemmaEntity;

@Service
public interface LemmaService extends EntityService<LemmaEntity, LemmaDto> {
    LemmaEntity findByLemmaAndSiteId(String lemma, Integer siteId);
    void decreaseFrequencyOrDeleteLemma(Integer id);
}
