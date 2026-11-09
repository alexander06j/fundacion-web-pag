package com.fundacionunidosporpinias.fundacion_web.controller.api;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IBeneficiarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiarios")
public class BeneficiarioRestController {

    @Autowired
    private IBeneficiarioService beneficiarioService;

    @GetMapping("/paginado")
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listarPaginado(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        int tamano = (size < 1 || size > 100) ? 10 : size;
        return ResponseEntity.ok(
                beneficiarioService.buscarPaginado(q, PageRequest.of(Math.max(page, 0), tamano, Sort.by(Sort.Direction.DESC, "id")))
        );
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioResponseDTO>> listar() {

        return ResponseEntity.ok(
                beneficiarioService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(@PathVariable("id") Long id) {

        return ResponseEntity.ok(
                beneficiarioService.findById(id)
        );
    }

    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorCedula(@PathVariable("cedula") String cedula) {

        return ResponseEntity.ok(
                beneficiarioService.findByCedula(cedula)
        );
    }

    @GetMapping("/parroquia/{parroquiaId}")
    public ResponseEntity<List<BeneficiarioResponseDTO>> buscarPorParroquia(@PathVariable("parroquiaId") Long parroquiaId) {

        return ResponseEntity.ok(
                beneficiarioService.findByParroquia(parroquiaId)
        );
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EntregaResumenDTO>> historial(@PathVariable("id") Long id) {

        return ResponseEntity.ok(
                beneficiarioService.obtenerHistorial(id)
        );
    }

    @PostMapping
    public ResponseEntity<BeneficiarioResponseDTO> guardar(@Valid @RequestBody BeneficiarioRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(beneficiarioService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiarioResponseDTO> actualizar(@PathVariable("id") Long id,
                                                              @Valid @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(
                beneficiarioService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        beneficiarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
