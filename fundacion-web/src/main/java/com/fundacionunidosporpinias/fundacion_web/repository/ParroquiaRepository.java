package com.fundacionunidosporpinias.fundacion_web.repository;

import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParroquiaRepository extends JpaRepository<Parroquia, Long> {

}
