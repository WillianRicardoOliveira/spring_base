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
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;
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

    private static final String MOTIVO_ALTERACAO_SENHA = "ALTERACAO_SENHA";
    private static final String MOTIVO_USUARIO_REMOVIDO = "USUARIO_REMOVIDO";

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioLogadoService usuarioLogadoService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final UsuarioSessaoService usuarioSessaoService;

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
            return repository.findByEmailContainingIgnoreCaseAndStatus(paginacao, filtro, StatusEnum.ATIVO)
                    .map(ListaUsuarioRecord::new);
        }

        return repository.findAllByStatus(paginacao, StatusEnum.ATIVO)
                .map(ListaUsuarioRecord::new);
    }

    @Transactional
    public DetalheUsuarioRecord atualizar(AtualizaUsuarioRecord dados) {
        if (repository.existsByEmailIgnoreCaseAndIdNot(dados.email(), dados.id())) {
            throw new ValidacaoException("Usuario ja cadastrado.");
        }

        UsuarioModel usuario = repository.findByIdAndStatus(dados.id(), StatusEnum.ATIVO)
                .orElseThrow(() -> new ValidacaoException("Usuario nao encontrado ou removido."));

        usuario.atualizar(dados);

        return new DetalheUsuarioRecord(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Long idUsuarioLogado = usuarioLogadoService.getId();

        UsuarioModel usuario = repository.getReferenceById(id);
        usuario.remover(idUsuarioLogado);

        usuarioSessaoService.revogarSessoesDoUsuario(
                usuario.getId(),
                idUsuarioLogado,
                MOTIVO_USUARIO_REMOVIDO
        );
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioRecord detalhar(Long id) {
        return new DetalheUsuarioRecord(repository.getReferenceById(id));
    }

    @Transactional
    public DetalheUsuarioRecord atualizarSenha(AtualizaSenhaUsuarioRecord dados) {
        Long idUsuarioLogado = usuarioLogadoService.getId();

        UsuarioModel usuario = repository.findByIdAndStatus(dados.id(), StatusEnum.ATIVO)
                .orElseThrow(() -> new ValidacaoException("Usuario nao encontrado ou removido."));

        usuario.atualizarSenha(passwordEncoder.encode(dados.senha()));

        usuarioSessaoService.revogarSessoesDoUsuario(
                usuario.getId(),
                idUsuarioLogado,
                MOTIVO_ALTERACAO_SENHA
        );

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