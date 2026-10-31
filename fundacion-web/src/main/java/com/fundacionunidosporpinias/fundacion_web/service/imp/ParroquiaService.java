package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.mapper.ParroquiaMapper;
import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import com.fundacionunidosporpinias.fundacion_web.repository.ParroquiaRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IParroquiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParroquiaService implements IParroquiaService {

    @Autowired
    private ParroquiaRepository repository;

    @Autowired
    private ParroquiaMapper mapper;

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ParroquiaResponseDTO> findAll() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Override
    public ParroquiaResponseDTO findById(Long id) {
        Parroquia parroquia = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Parroquia no encontrada"));
        return mapper.toResponseDTO(parroquia);
    }

    @Override
    public ParroquiaResponseDTO save(ParroquiaRequestDTO dto) {
        Parroquia parroquia = mapper.toEntity(dto);

        return mapper.toResponseDTO(repository.save(parroquia));
    }

    @Override
    public ParroquiaResponseDTO update(Long id, ParroquiaRequestDTO dto) {

        Parroquia parroquia = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Parroquia no encontrada"));
        parroquia.setNombre(dto.getNombre());
        parroquia.setCanton(dto.getCanton());

        return mapper.toResponseDTO(repository.save(parroquia));
    }
}
