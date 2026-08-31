package com.empresa.erp.domain.acesso.usuarioPerfil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.administrador.service.ProtecaoAdministradorOrganizacaoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.DetalheUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.ListaUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioPerfilService {

    private final UsuarioPerfilRepository repository;

    private final UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    private final PerfilRepository
            perfilRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;
    
    private final ProtecaoAdministradorOrganizacaoService
    		protecaoAdministradorOrganizacaoService;

    @Transactional
    public UsuarioPerfilModel cadastrar(
            UsuarioPerfilRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioOrganizacaoModel usuarioOrganizacao =
                buscarUsuarioAtivoNaOrganizacao(
                        dados.idUsuario(),
                        idOrganizacao
                );

        PerfilModel perfil =
                buscarPerfilAtivo(
                        dados.idPerfil(),
                        idOrganizacao
                );

        if (repository
                .existsByUsuarioOrganizacaoIdAndPerfilIdAndPerfilOrganizacaoIdAndStatus(
                        usuarioOrganizacao.getId(),
                        perfil.getId(),
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Perfil ja vinculado ao usuario."
            );
        }

        UsuarioPerfilModel usuarioPerfil =
                new UsuarioPerfilModel(
                        usuarioOrganizacao,
                        perfil
                );

        return repository.save(usuarioPerfil);
    }

    @Transactional(readOnly = true)
    public List<ListaUsuarioPerfilRecord> listarPorUsuario(
            Long idUsuario
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioOrganizacaoModel usuarioOrganizacao =
                buscarUsuarioAtivoNaOrganizacao(
                        idUsuario,
                        idOrganizacao
                );

        return repository
                .findAllByUsuarioOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                        usuarioOrganizacao.getId(),
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .stream()
                .map(ListaUsuarioPerfilRecord::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioPerfilRecord detalhar(
            Long id
    ) {
        UsuarioPerfilModel usuarioPerfil =
                buscarVinculoAtivo(id);

        return new DetalheUsuarioPerfilRecord(
                usuarioPerfil
        );
    }

    @Transactional
    public void excluir(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioPerfilModel usuarioPerfil =
                buscarVinculoAtivo(id);

        protecaoAdministradorOrganizacaoService
                .validarRemocaoPerfil(
                        usuarioPerfil,
                        idOrganizacao
                );

        Long idUsuario =
                usuarioLogadoService.getId();

        usuarioPerfil.remover(idUsuario);
    }

    private UsuarioPerfilModel buscarVinculoAtivo(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        return repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                        id,
                        idOrganizacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo de perfil nao encontrado "
                                        + "ou removido."
                        )
                );
    }

    private UsuarioOrganizacaoModel
            buscarUsuarioAtivoNaOrganizacao(
                    Long idUsuario,
                    Long idOrganizacao
            ) {
        return usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        idUsuario,
                        idOrganizacao,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Usuario nao encontrado "
                                        + "na organizacao."
                        )
                );
    }

    private PerfilModel buscarPerfilAtivo(
            Long idPerfil,
            Long idOrganizacao
    ) {
        return perfilRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        idPerfil,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Perfil nao encontrado."
                        )
                );
    }
}