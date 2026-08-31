package com.empresa.erp.domain.acesso.perfilPermissao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.DetalhePerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.ListaPerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilPermissaoService {

    private static final EscopoPermissaoEnum ESCOPO_ORGANIZACAO =
            EscopoPermissaoEnum.ORGANIZACAO;

    private final PerfilPermissaoRepository repository;

    private final PerfilRepository
            perfilRepository;

    private final PermissaoRepository
            permissaoRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public PerfilPermissaoModel cadastrar(
            PerfilPermissaoRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        PerfilModel perfil =
                buscarPerfilAtivoParaAtualizacao(
                        dados.idPerfil(),
                        idOrganizacao
                );

        validarPerfilCritico(perfil);

        PermissaoModel permissao =
                permissaoRepository
                        .findByIdAndEscopoAndStatus(
                                dados.idPermissao(),
                                ESCOPO_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
                        .orElseThrow(() ->
                                new ValidacaoException(
                                        "Permissao nao encontrada."
                                )
                        );

        PerfilPermissaoModel vinculoExistente =
                repository
                        .findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                                perfil.getId(),
                                permissao.getId(),
                                idOrganizacao
                        )
                        .orElse(null);

        if (vinculoExistente != null) {
            if (vinculoExistente.getStatus()
                    == StatusEnum.ATIVO) {
                throw new ValidacaoException(
                        "Permissao ja vinculada ao perfil."
                );
            }

            vinculoExistente.reativar();

            return repository.save(vinculoExistente);
        }

        PerfilPermissaoModel perfilPermissao =
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                );

        return repository.save(perfilPermissao);
    }

    @Transactional(readOnly = true)
    public Page<ListaPerfilPermissaoRecord> listarPorPerfil(
            Pageable paginacao,
            Long idPerfil
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        buscarPerfilAtivo(
                idPerfil,
                idOrganizacao
        );

        return repository
                .findAllByPerfilIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                        paginacao,
                        idPerfil,
                        idOrganizacao,
                        ESCOPO_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .map(ListaPerfilPermissaoRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalhePerfilPermissaoRecord detalhar(
            Long id
    ) {
        PerfilPermissaoModel perfilPermissao =
                buscarVinculoAtivo(id);

        return new DetalhePerfilPermissaoRecord(
                perfilPermissao
        );
    }

    @Transactional
    public void excluir(Long id) {
        PerfilPermissaoModel perfilPermissao =
                buscarVinculoAtivo(id);

        validarPerfilCritico(
                perfilPermissao.getPerfil()
        );

        Long idUsuario =
                usuarioLogadoService.getId();

        perfilPermissao.remover(idUsuario);
    }

    private PerfilPermissaoModel buscarVinculoAtivo(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        return repository
                .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                        id,
                        idOrganizacao,
                        ESCOPO_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo de permissao nao encontrado "
                                        + "ou removido."
                        )
                );
    }

    private PerfilModel buscarPerfilAtivoParaAtualizacao(
            Long idPerfil,
            Long idOrganizacao
    ) {
        return perfilRepository
                .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                        idPerfil,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Perfil nao encontrado ou removido."
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
                                "Perfil nao encontrado ou removido."
                        )
                );
    }

    private void validarPerfilCritico(
            PerfilModel perfil
    ) {
        if (perfil.isSistema()) {
            throw new ValidacaoException(
                    "Permissoes de perfil critico do sistema "
                            + "nao podem ser alteradas."
            );
        }
    }
}