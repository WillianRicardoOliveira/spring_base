package com.empresa.erp.domain.acesso.perfil.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.DetalhePerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.ListaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository repository;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final PerfilPermissaoRepository
            perfilPermissaoRepository;

    private final UsuarioPerfilRepository
            usuarioPerfilRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public PerfilModel cadastrar(
            PerfilRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        String nome =
                normalizarNome(dados.nome());

        if (repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        idOrganizacao,
                        nome,
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Perfil ja cadastrado."
            );
        }

        OrganizacaoModel organizacao =
                organizacaoRepository.getReferenceById(
                        idOrganizacao
                );

        PerfilModel perfil =
                new PerfilModel(
                        organizacao,
                        new PerfilRecord(
                                nome,
                                normalizarDescricao(
                                        dados.descricao()
                                )
                        )
                );

        return repository.save(perfil);
    }

    @Transactional(readOnly = true)
    public Page<ListaPerfilRecord> listar(
            Pageable paginacao,
            String filtro
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        if (filtro != null && !filtro.isBlank()) {
            return repository
                    .findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                            paginacao,
                            idOrganizacao,
                            filtro.trim(),
                            StatusEnum.ATIVO
                    )
                    .map(ListaPerfilRecord::new);
        }

        return repository
                .findAllByOrganizacaoIdAndStatus(
                        paginacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .map(ListaPerfilRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalhePerfilRecord detalhar(
            Long id
    ) {
        PerfilModel perfil =
                buscarPerfilAtivo(id);

        return new DetalhePerfilRecord(perfil);
    }

    @Transactional
    public DetalhePerfilRecord atualizar(
            AtualizaPerfilRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        String nome =
                normalizarNome(dados.nome());

        PerfilModel perfil =
                buscarPerfilAtivo(dados.id());

        validarPerfilCritico(perfil);

        if (repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        idOrganizacao,
                        nome,
                        StatusEnum.ATIVO,
                        perfil.getId()
                )
        ) {
            throw new ValidacaoException(
                    "Perfil ja cadastrado."
            );
        }

        perfil.atualizar(
                new AtualizaPerfilRecord(
                        perfil.getId(),
                        nome,
                        normalizarDescricao(
                                dados.descricao()
                        )
                )
        );

        return new DetalhePerfilRecord(perfil);
    }

    @Transactional
    public void excluir(
            Long id
    ) {
        PerfilModel perfil =
                buscarPerfilAtivo(id);

        validarPerfilCritico(perfil);
        validarAusenciaDeUsuarios(perfil);
        validarAusenciaDePermissoes(perfil);

        Long idUsuario =
                usuarioLogadoService.getId();

        perfil.remover(idUsuario);
    }

    private void validarAusenciaDeUsuarios(
            PerfilModel perfil
    ) {
        if (usuarioPerfilRepository
                .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                        perfil.getId(),
                        perfil.getOrganizacao().getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Perfil possui usuarios vinculados "
                            + "e nao pode ser removido."
            );
        }
    }

    private void validarAusenciaDePermissoes(
            PerfilModel perfil
    ) {
        if (perfilPermissaoRepository
                .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                        perfil.getId(),
                        perfil.getOrganizacao().getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Perfil possui permissoes vinculadas "
                            + "e nao pode ser removido."
            );
        }
    }

    private PerfilModel buscarPerfilAtivo(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        return repository
                .findByIdAndOrganizacaoIdAndStatus(
                        id,
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
                    "Perfil critico do sistema "
                            + "nao pode ser alterado."
            );
        }
    }

    private String normalizarNome(
            String nome
    ) {
        return nome == null
                ? null
                : nome.trim()
                        .replaceAll("\\s+", " ");
    }

    private String normalizarDescricao(
            String descricao
    ) {
        if (descricao == null) {
            return null;
        }

        String descricaoNormalizada =
                descricao.trim()
                        .replaceAll("\\s+", " ");

        return descricaoNormalizada.isBlank()
                ? null
                : descricaoNormalizada;
    }
}