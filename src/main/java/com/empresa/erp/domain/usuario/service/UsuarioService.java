package com.empresa.erp.domain.usuario.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.administrador.service.ProtecaoAdministradorOrganizacaoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.criacao.service.CriacaoUsuarioService;
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

    private final ProtecaoAdministradorOrganizacaoService
            protecaoAdministradorOrganizacaoService;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final CriacaoUsuarioService
            criacaoUsuarioService;

    private final UsuarioAutenticadoService
            usuarioAutenticadoService;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public UsuarioOrganizacaoModel cadastrar(
            UsuarioRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioModel usuario =
                criacaoUsuarioService.criar(
                        dados.email(),
                        dados.senha()
                );

        var organizacao =
                organizacaoRepository.getReferenceById(
                        idOrganizacao
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        return usuarioOrganizacaoRepository.save(
                usuarioOrganizacao
        );
    }

    @Transactional(readOnly = true)
    public Page<ListaUsuarioRecord> listar(
            Pageable paginacao,
            String filtro,
            StatusEnum status
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        StatusEnum statusVinculo =
                validarStatusDeListagem(status);

        if (filtro != null && !filtro.isBlank()) {
            return usuarioOrganizacaoRepository
                    .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                            paginacao,
                            idOrganizacao,
                            filtro.trim(),
                            statusVinculo,
                            StatusEnum.ATIVO
                    )
                    .map(ListaUsuarioRecord::new);
        }

        return usuarioOrganizacaoRepository
                .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                        paginacao,
                        idOrganizacao,
                        statusVinculo,
                        StatusEnum.ATIVO
                )
                .map(ListaUsuarioRecord::new);
    }

    @Transactional
    public void excluir(
            Long id
    ) {
        Long idUsuarioLogado =
                usuarioLogadoService.getId();

        if (Objects.equals(
                id,
                idUsuarioLogado
        )) {
            throw new ValidacaoException(
                    "O usuario nao pode remover o proprio "
                            + "acesso a organizacao."
            );
        }

        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioOrganizacaoModel vinculo =
                usuarioOrganizacaoRepository
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

        protecaoAdministradorOrganizacaoService
                .validarInativacaoUsuario(
                        vinculo,
                        idOrganizacao
                );

        vinculo.inativar();
    }

    @Transactional
    public DetalheUsuarioRecord reativar(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioOrganizacaoModel vinculo =
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                id,
                                idOrganizacao,
                                StatusEnum.INATIVO,
                                StatusEnum.ATIVO
                        )
                        .orElseThrow(() ->
                                new ValidacaoException(
                                        "Usuario inativo nao encontrado."
                                )
                        );

        vinculo.reativar();

        return new DetalheUsuarioRecord(
                vinculo
        );
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioRecord detalhar(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioOrganizacaoModel vinculo =
                usuarioOrganizacaoRepository
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
                vinculo
        );
    }

    private StatusEnum validarStatusDeListagem(
            StatusEnum status
    ) {
        StatusEnum statusValidado =
                status == null
                        ? StatusEnum.ATIVO
                        : status;

        if (statusValidado != StatusEnum.ATIVO
                && statusValidado != StatusEnum.INATIVO) {
            throw new ValidacaoException(
                    "Status de usuario invalido."
            );
        }

        return statusValidado;
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