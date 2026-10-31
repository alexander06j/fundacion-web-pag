package com.fundacionunidosporpinias.fundacion_web.repository;

import com.fundacionunidosporpinias.fundacion_web.model.DetalleEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleEntregaRepository extends JpaRepository<DetalleEntrega, Long> {
}
