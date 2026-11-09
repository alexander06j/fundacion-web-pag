package com.fundacionunidosporpinias.fundacion_web.repository;

import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {


    // Carga completa de una entrega (beneficiario, parroquia y productos) para el Acta PDF
    @Query("select e from Entrega e join fetch e.beneficiario b join fetch b.parroquia p"
            + " left join fetch e.detalles d left join fetch d.producto"
            + " where e.id = :id")
    Optional<Entrega> buscarConDetallePorId(@Param("id") Long id);

    /**
     * Filtro compartido: texto (beneficiario, cédula o parroquia) + rango de fechas opcional.
     * Con :q = '' y fechas nulas devuelve todos los registros.
     */
    String FILTRO_BUSQUEDA = """
             where (lower(concat(b.nombre, ' ', b.apellido)) like lower(concat('%', :q, '%'))
                    or b.cedula like concat('%', :q, '%')
                    or lower(p.nombre) like lower(concat('%', :q, '%')))
               and (:desde is null or e.fechaEntrega >= :desde)
               and (:hasta is null or e.fechaEntrega <= :hasta)
            """;

    // Misma búsqueda, sin paginar y con los productos entregados, para la exportación a Excel
    @Query("select distinct e from Entrega e join fetch e.beneficiario b join fetch b.parroquia p"
            + " left join fetch e.detalles d left join fetch d.producto"
            + FILTRO_BUSQUEDA
            + " order by e.fechaEntrega desc, e.id desc")
    List<Entrega> buscarParaReporte(@Param("q") String q,
                                    @Param("desde") LocalDate desde,
                                    @Param("hasta") LocalDate hasta);

    // Número de entregas por beneficiario: [beneficiarioId, total]
    @Query("select e.beneficiario.id, count(e) from Entrega e group by e.beneficiario.id")
    List<Object[]> contarPorBeneficiario();

}
