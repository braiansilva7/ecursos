package com.ecursos.myapp.repository;

import com.ecursos.myapp.domain.Militar;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Militar entity.
 */
@Repository
public interface MilitarRepository extends JpaRepository<Militar, Long>, JpaSpecificationExecutor<Militar> {
    default Optional<Militar> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    boolean existsBySaram(String saram);
    
    Optional<Militar> findBySaram(String saram);

    default List<Militar> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Militar> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select militar from Militar militar left join fetch militar.posto",
        countQuery = "select count(militar) from Militar militar"
    )
    Page<Militar> findAllWithToOneRelationships(Pageable pageable);

    @Query("select militar from Militar militar left join fetch militar.posto")
    List<Militar> findAllWithToOneRelationships();

    @Query("select militar from Militar militar left join fetch militar.posto where militar.id =:id")
    Optional<Militar> findOneWithToOneRelationships(@Param("id") Long id);
}
