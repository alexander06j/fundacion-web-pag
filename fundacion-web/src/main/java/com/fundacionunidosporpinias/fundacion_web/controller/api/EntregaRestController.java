package com.fundacionunidosporpinias.fundacion_web.controller.api;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IEntregaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
public class EntregaRestController {

    @Autowired
    private IEntregaService entregaService;


    @GetMapping
    public ResponseEntity<List<EntregaResumenDTO>> listar() {

        return ResponseEntity.ok(
                entregaService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaResponseDTO> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                entregaService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<EntregaResponseDTO> registrarEntrega(@Valid @RequestBody EntregaRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entregaService.registrarEntrega(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entregaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
