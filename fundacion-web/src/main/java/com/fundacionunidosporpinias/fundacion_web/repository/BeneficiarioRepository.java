package com.fundacionunidosporpinias.fundacion_web.repository;

import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    /**
     * Filtro de búsqueda compartido: nombre, apellido, nombre completo, cédula o parroquia.
     * Con :q = '' (cadena vacía) devuelve todos los registros.
     */
    String FILTRO_BUSQUEDA = """
             where lower(b.nombre) like lower(concat('%', :q, '%'))
                or lower(b.apellido) like lower(concat('%', :q, '%'))
                or lower(concat(b.nombre, ' ', b.apellido)) like lower(concat('%', :q, '%'))
                or b.cedula like concat('%', :q, '%')
                or lower(p.nombre) like lower(concat('%', :q, '%'))
            """;

    Optional<Beneficiario> findByCedula(String cedula);

    List<Beneficiario> findByParroquiaId(Long parroquiaId);

    // Búsqueda paginada en servidor (la parroquia se trae en la misma consulta para evitar N+1)
    @Query(value = "select b from Beneficiario b join fetch b.parroquia p" + FILTRO_BUSQUEDA,
            countQuery = "select count(b) from Beneficiario b join b.parroquia p" + FILTRO_BUSQUEDA)
    Page<Beneficiario> buscar(@Param("q") String q, Pageable pageable);

    // Misma búsqueda, sin paginar, para la exportación a Excel
    @Query("select b from Beneficiario b join fetch b.parroquia p" + FILTRO_BUSQUEDA
            + " order by b.apellido, b.nombre")
    List<Beneficiario> buscarParaReporte(@Param("q") String q);


}
