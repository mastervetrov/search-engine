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
    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE page_id_seq RESTART WITH 1", nativeQuery = true)
    void resetSequence();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM page", nativeQuery = true)
    void deleteAllCustom();

    List<PageEntity> findByIdIn(List<Integer> ids);

    PageEntity findByPath(String path);

    Long countBySiteId(Long siteId);
}
