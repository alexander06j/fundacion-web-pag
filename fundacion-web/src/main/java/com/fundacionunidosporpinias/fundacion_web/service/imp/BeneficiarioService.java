package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.mapper.BeneficiarioMapper;
import com.fundacionunidosporpinias.fundacion_web.mapper.EntregaResumenMapper;
import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import com.fundacionunidosporpinias.fundacion_web.repository.BeneficiarioRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.ParroquiaRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IBeneficiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiarioService implements IBeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private BeneficiarioMapper mapper;

    @Autowired
    private ParroquiaRepository parroquiaRepository;

    @Autowired
    private EntregaResumenMapper entregaResumenMapper;

    @Override
    public List<BeneficiarioResponseDTO> findAll() {
        return mapper.toResponseDTOList(beneficiarioRepository.findAll());
    }

    @Override
    public BeneficiarioResponseDTO findById(Long id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Beneficiario no encontrado"));
        return mapper.toResponseDTO(beneficiario);
    }

    @Override
    public BeneficiarioResponseDTO save(
            BeneficiarioRequestDTO dto) {

        Parroquia parroquia =
                parroquiaRepository.findById(dto.getParroquiaId())
                        .orElseThrow(() ->
                                new NotFoundException("Parroquia no encontrada"));

        Beneficiario beneficiario = new Beneficiario();

        beneficiario.setCedula(dto.getCedula());
        beneficiario.setNombre(dto.getNombre());
        beneficiario.setApellido(dto.getApellido());
        beneficiario.setTelefono(dto.getTelefono());
        beneficiario.setDireccion(dto.getDireccion());
        beneficiario.setParroquia(parroquia);

        return mapper.toResponseDTO(
                beneficiarioRepository.save(beneficiario)
        );
    }

    @Override
    public BeneficiarioResponseDTO update(Long id, BeneficiarioRequestDTO dto) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Beneficiario no encontrado"));

        Parroquia parroquia = parroquiaRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Parroquia no encontrada"));

        beneficiario.setCedula(dto.getCedula());
        beneficiario.setNombre(dto.getNombre());
        beneficiario.setApellido(dto.getApellido());
        beneficiario.setTelefono(dto.getTelefono());
        beneficiario.setDireccion(dto.getDireccion());
        beneficiario.setParroquia(parroquia);

        return mapper.toResponseDTO(beneficiarioRepository.save(beneficiario));
    }

    @Override
    public void delete(Long id) {
        beneficiarioRepository.deleteById(id);
    }

    @Override
    public List<BeneficiarioResponseDTO> findByParroquia(Long parroquiaId) {
        return mapper.toResponseDTOList(beneficiarioRepository.findByParroquiaId(parroquiaId));
    }

    @Override
    public BeneficiarioResponseDTO findByCedula(String cedula) {
        Beneficiario beneficiario = beneficiarioRepository.findByCedula(cedula)
                .orElseThrow(()-> new NotFoundException("Beneficiario no encontrado"));
        return mapper.toResponseDTO(beneficiario);
    }

    @Override
    public List<EntregaResumenDTO> obtenerHistorial(Long beneficiarioId) {
        Beneficiario beneficiario = beneficiarioRepository.findById(beneficiarioId)
                .orElseThrow(()-> new NotFoundException("Beneficiario no encontrado"));
        return entregaResumenMapper.toResumenDTOList(beneficiario.getEntregas());
    }
}
