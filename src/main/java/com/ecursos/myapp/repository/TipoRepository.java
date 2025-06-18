package com.ecursos.myapp.repository;

import com.ecursos.myapp.domain.Tipo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tipo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long>, JpaSpecificationExecutor<Tipo> {}
