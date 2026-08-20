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
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.DetalheUsuarioRecord;
import com.empresa.erp.domain.usuario.record.ListaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService
        implements UserDetailsService {

    private final UsuarioRepository repository;

    private final UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final PasswordEncoder passwordEncoder;

    private final UsuarioAutenticadoService
            usuarioAutenticadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public UsuarioModel cadastrar(
            UsuarioRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        if (repository.existsByEmailIgnoreCase(
                dados.email()
        )) {
            throw new ValidacaoException(
                    "Usuario ja cadastrado."
            );
        }

        var usuario = new UsuarioModel(
                dados,
                passwordEncoder.encode(dados.senha())
        );

        repository.save(usuario);

        var organizacao =
                organizacaoRepository.getReferenceById(
                        idOrganizacao
                );

        usuarioOrganizacaoRepository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        return usuario;
    }

    @Transactional(readOnly = true)
    public Page<ListaUsuarioRecord> listar(
            Pageable paginacao,
            String filtro
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        if (filtro != null && !filtro.isBlank()) {
            return usuarioOrganizacaoRepository
                    .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                            paginacao,
                            idOrganizacao,
                            filtro.trim(),
                            StatusEnum.ATIVO,
                            StatusEnum.ATIVO
                    )
                    .map(vinculo ->
                            new ListaUsuarioRecord(
                                    vinculo.getUsuario()
                            )
                    );
        }

        return usuarioOrganizacaoRepository
                .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                        paginacao,
                        idOrganizacao,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .map(vinculo ->
                        new ListaUsuarioRecord(
                                vinculo.getUsuario()
                        )
                );
    }

    @Transactional
    public void excluir(Long id) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        var vinculo = usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        id,
                        idOrganizacao,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Usuario nao encontrado ou removido."
                        )
                );

        vinculo.inativar();
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioRecord detalhar(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        var vinculo = usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        id,
                        idOrganizacao,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Usuario nao encontrado ou removido."
                        )
                );

        return new DetalheUsuarioRecord(
                vinculo.getUsuario()
        );
    }

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        var usuarioAutenticado =
                usuarioAutenticadoService
                        .buscarPorEmail(username);

        if (usuarioAutenticado == null) {
            throw new UsernameNotFoundException(
                    "Usuario nao encontrado"
            );
        }

        return usuarioAutenticado;
    }
}