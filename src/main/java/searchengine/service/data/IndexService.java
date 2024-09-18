package searchengine.service.data;

import org.springframework.stereotype.Service;
import searchengine.dto.data.IndexDto;
import searchengine.model.IndexEntity;

@Service
public interface IndexService extends EntityService<IndexEntity, IndexDto> {
}
