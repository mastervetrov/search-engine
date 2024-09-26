package searchengine.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.data.SiteDto;
import searchengine.model.SiteEntity;
import searchengine.repository.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService{
    private final SiteRepository siteJpaRepository;

    @Override
    public void save(SiteEntity siteEntity) {
        siteJpaRepository.save(siteEntity);
    }

    @Override
    public void saveAll(List<SiteEntity> siteEntityList) {
        siteJpaRepository.saveAll(siteEntityList);
    }

    @Override
    public void deleteById(Integer id) {
        siteJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        siteJpaRepository.deleteAll();
    }

    @Override
    public SiteEntity findById(Integer id) {
        return siteJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<SiteEntity> findByIdIn(List<Integer> ids) {
        return siteJpaRepository.findByIdIn(ids);
    }

    @Override
    public List<SiteEntity> findAll() {
        return siteJpaRepository.findAll();
    }


    @Override
    public SiteEntity mapDtoToEntity(SiteDto siteDto) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setId(siteDto.getId());
        siteEntity.setName(siteDto.getName());
        siteEntity.setUrl(siteDto.getUrl());
        siteEntity.setStatus(siteDto.getStatus());
        siteEntity.setStatusTime(siteDto.getStatusTime());
        siteEntity.setLastError(siteDto.getLastError());
        return siteEntity;
    }

    @Override
    public SiteDto mapEntityToDto(SiteEntity siteEntity) {
        SiteDto siteDto = new SiteDto();
        siteDto.setId(siteEntity.getId());
        siteDto.setName(siteEntity.getName());
        siteDto.setUrl(siteEntity.getUrl());
        siteDto.setStatus(siteEntity.getStatus());
        siteDto.setStatusTime(siteEntity.getStatusTime());
        siteDto.setLastError(siteEntity.getLastError());
        return siteDto;
    }

    @Override
    public SiteEntity findByUrl(String url) {
        return siteJpaRepository.findByUrl(url);
    }
}
