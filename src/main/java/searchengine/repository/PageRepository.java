package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.PageEntity;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {

    List<PageEntity> findByIdIn(List<Integer> ids);

    PageEntity findByPath(String path);

    Long countBySiteId(Long siteId);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE page AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM page", nativeQuery = true)
    void deleteAllCustom();

    void deleteByIdIn(List<Integer> pageIds);
}
