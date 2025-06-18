package com.ecursos.myapp.repository;

import com.ecursos.myapp.domain.Capacitacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Capacitacao entity.
 */
@Repository
public interface CapacitacaoRepository extends JpaRepository<Capacitacao, Long>, JpaSpecificationExecutor<Capacitacao> {
    default Optional<Capacitacao> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Capacitacao> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Capacitacao> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select capacitacao from Capacitacao capacitacao left join fetch capacitacao.militar",
        countQuery = "select count(capacitacao) from Capacitacao capacitacao"
    )
    Page<Capacitacao> findAllWithToOneRelationships(Pageable pageable);

    @Query("select capacitacao from Capacitacao capacitacao left join fetch capacitacao.militar")
    List<Capacitacao> findAllWithToOneRelationships();

    @Query("select capacitacao from Capacitacao capacitacao left join fetch capacitacao.militar where capacitacao.id =:id")
    Optional<Capacitacao> findOneWithToOneRelationships(@Param("id") Long id);

    /** True se já existe inscrição desse militar nessa turma */
    boolean existsByMilitarIdAndTurmaId(Long militarId, Long turmaId);

    @Query("SELECT COUNT(c) FROM Capacitacao c WHERE c.turma.id = :turmaId")
    long countByTurmaId(@Param("turmaId") Long turmaId);
}
