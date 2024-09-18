package searchengine.service.data;

import org.springframework.stereotype.Service;
import searchengine.dto.data.PageDto;
import searchengine.model.PageEntity;

@Service
public interface PageService extends EntityService<PageEntity, PageDto> {
    PageEntity findByUrl(String url);
}
