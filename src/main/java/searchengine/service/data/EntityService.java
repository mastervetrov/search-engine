package searchengine.service.data;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EntityService<Entity, Dto> {
    void save(Entity entity);

    void saveAll(List<Entity> entityList);

    void deleteById(Integer id);

    void deleteAll();

    Entity findById(Integer id);

    List<Entity> findByIdIn(List<Integer> ids);

    List<Entity> findAll();

    Dto mapEntityToDto(Entity entity);

    Entity mapDtoToEntity(Dto dto);
}
