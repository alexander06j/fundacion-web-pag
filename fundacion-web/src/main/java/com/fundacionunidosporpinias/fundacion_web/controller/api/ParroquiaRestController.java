package com.fundacionunidosporpinias.fundacion_web.controller.api;

import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.service.imp.ParroquiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parroquias")
public class ParroquiaRestController {

    @Autowired
    private ParroquiaService parroquiaService;

    //READ-ALL
    @GetMapping
    public ResponseEntity<List<ParroquiaResponseDTO>> listar(){
        return ResponseEntity.ok(parroquiaService.findAll());
    }

    //SAVE-CREATE
    @PostMapping
    public ResponseEntity<ParroquiaResponseDTO> guardar(@Valid @RequestBody ParroquiaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parroquiaService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParroquiaResponseDTO> actualizar(@PathVariable Long id,@Valid @RequestBody ParroquiaRequestDTO dto){
        return ResponseEntity.ok(parroquiaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        parroquiaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
