package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.dto.usuario.UsuarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.usuario.UsuarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.model.Usuario;
import com.fundacionunidosporpinias.fundacion_web.repository.UsuarioRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO findById(Long id) {
        return toResponseDTO(buscar(id));
    }

    @Override
    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setUsername(dto.getUsername().trim());
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscar(id);

        // El nombre de usuario no se modifica (es la identidad de inicio de sesión)
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setRol(dto.getRol());

        // Solo se cambia la contraseña si se escribió una nueva
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponseDTO cambiarEstado(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(!usuario.isActivo());
        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
