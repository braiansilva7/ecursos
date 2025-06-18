package com.ecursos.myapp.repository;

import com.ecursos.myapp.domain.Turma;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Turma entity.
 */
@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long>, JpaSpecificationExecutor<Turma> {
    default Optional<Turma> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Turma> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Turma> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select turma from Turma turma left join fetch turma.curso", countQuery = "select count(turma) from Turma turma")
    Page<Turma> findAllWithToOneRelationships(Pageable pageable);

    @Query("select turma from Turma turma left join fetch turma.curso")
    List<Turma> findAllWithToOneRelationships();

    @Query("select turma from Turma turma left join fetch turma.curso where turma.id =:id")
    Optional<Turma> findOneWithToOneRelationships(@Param("id") Long id);
}
