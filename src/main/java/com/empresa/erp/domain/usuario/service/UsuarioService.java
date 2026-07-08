package com.empresa.erp.domain.usuario.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.AtualizaSenhaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.DetalheUsuarioRecord;
import com.empresa.erp.domain.usuario.record.ListaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    
    private final UsuarioLogadoService usuarioLogadoService;
    
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    
    @Transactional
    public UsuarioModel cadastrar(UsuarioRecord dados) {
        if (repository.existsByEmailIgnoreCase(dados.email())) {
            throw new ValidacaoException("Usuario ja cadastrado.");
        }
        var usuario = new UsuarioModel(dados, passwordEncoder.encode(dados.senha()));
        repository.save(usuario);
        return usuario;
    }

    @Transactional(readOnly = true)
    public Page<ListaUsuarioRecord> listar(Pageable paginacao, String filtro) {
        if (filtro != null && !filtro.isBlank()) {
        	return repository.findByEmailContainingIgnoreCaseAndStatus(paginacao, filtro, StatusEnum.ATIVO).map(ListaUsuarioRecord::new);
        }
        return repository.findAllByStatus(paginacao, StatusEnum.ATIVO).map(ListaUsuarioRecord::new);
    }

    @Transactional
    public DetalheUsuarioRecord atualizar(AtualizaUsuarioRecord dados) {
        if (repository.existsByEmailIgnoreCaseAndIdNot(dados.email(), dados.id())) {
            throw new ValidacaoException("Usuario ja cadastrado.");
        }
        UsuarioModel usuario = repository.getReferenceById(dados.id());
        usuario.atualizar(dados);
        return new DetalheUsuarioRecord(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Long idUsuario = usuarioLogadoService.getId();

        UsuarioModel usuario = repository.getReferenceById(id);
        usuario.remover(idUsuario);
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioRecord detalhar(Long id) {
        return new DetalheUsuarioRecord(repository.getReferenceById(id));
    }
    
    @Transactional
    public DetalheUsuarioRecord atualizarSenha(AtualizaSenhaUsuarioRecord dados) {
        UsuarioModel usuario = repository.getReferenceById(dados.id());
        usuario.atualizarSenha(passwordEncoder.encode(dados.senha()));
        return new DetalheUsuarioRecord(usuario);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuarioAutenticado = usuarioAutenticadoService.buscarPorEmail(username);

        if (usuarioAutenticado == null) {
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }

        return usuarioAutenticado;
    }
    
}