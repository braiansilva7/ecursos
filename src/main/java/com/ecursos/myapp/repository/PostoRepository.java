package com.ecursos.myapp.repository;

import com.ecursos.myapp.domain.Posto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import com.ecursos.myapp.service.dto.PostoDTO;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Posto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostoRepository extends JpaRepository<Posto, Long>, JpaSpecificationExecutor<Posto> {
    Optional<Posto> findByCodSigpes(Integer codSigpes);
}
