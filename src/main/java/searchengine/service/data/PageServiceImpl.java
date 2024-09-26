package searchengine.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.PageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {
    private final PageRepository pageJpaRepository;
    private final SiteService siteService;

    @Override
    public void save(PageEntity pageEntity) {
        pageJpaRepository.save(pageEntity);
    }

    @Override
    public void saveAll(List<PageEntity> pageEntityList) {
        pageJpaRepository.saveAll(pageEntityList);
    }

    @Override
    public void deleteById(Integer id) {
        pageJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        pageJpaRepository.deleteAll();
    }

    @Override
    public PageEntity findById(Integer id) {
        return pageJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<PageEntity> findByIdIn(List<Integer> ids) {
        return pageJpaRepository.findByIdIn(ids);
    }

    @Override
    public List<PageEntity> findAll() {
        return pageJpaRepository.findAll();
    }

    @Override
    public PageEntity mapDtoToEntity(PageDto pageDto) {
        SiteDto siteDto = pageDto.getSiteDto();
        SiteEntity siteEntity = siteService.mapDtoToEntity(siteDto);

        PageEntity pageEntity = new PageEntity();
        pageEntity.setSite(siteEntity);
        pageEntity.setId(pageDto.getId());
        pageEntity.setPath(pageDto.getPath());
        pageEntity.setCode(pageDto.getCode());
        pageEntity.setContent(pageDto.getContent());
        return pageEntity;
    }

    @Override
    public PageDto mapEntityToDto(PageEntity pageEntity) {
        SiteEntity siteEntity = pageEntity.getSite();
        SiteDto siteDto = siteService.mapEntityToDto(siteEntity);

        PageDto pageDto = new PageDto();
        pageDto.setSiteDto(siteDto);
        pageDto.setId(pageEntity.getId());
        pageDto.setPath(pageEntity.getPath());
        pageDto.setCode(pageEntity.getCode());
        pageDto.setContent(pageEntity.getContent());
        return pageDto;
    }

    @Override
    public PageEntity findByUrl(String url) {

        return pageJpaRepository.findByPath(url);
    }
}
