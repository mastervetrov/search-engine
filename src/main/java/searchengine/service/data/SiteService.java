package searchengine.service.data;

import org.springframework.stereotype.Service;
import searchengine.dto.data.SiteDto;
import searchengine.model.SiteEntity;

@Service
public interface SiteService extends EntityService<SiteEntity, SiteDto> {
    SiteEntity findByUrl(String url);
}
