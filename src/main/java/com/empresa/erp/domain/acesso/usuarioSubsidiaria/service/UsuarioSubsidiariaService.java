package com.empresa.erp.domain.acesso.usuarioSubsidiaria.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.DetalheUsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.ListaUsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.UsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioSubsidiariaService {

    private final UsuarioSubsidiariaRepository repository;

    private final UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    private final SubsidiariaRepository
            subsidiariaRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public UsuarioSubsidiariaModel cadastrar(
            UsuarioSubsidiariaRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioEmpresaModel usuarioEmpresa =
                buscarUsuarioEmpresaAtivo(
                        dados.idUsuarioEmpresa(),
                        idOrganizacao
                );

        if (Boolean.TRUE.equals(
                usuarioEmpresa.getTodasSubsidiarias()
        )) {
            throw new ValidacaoException(
                    "O usuario possui acesso a todas as "
                            + "subsidiarias desta empresa."
            );
        }

        SubsidiariaModel subsidiaria =
                buscarSubsidiariaAtiva(
                        dados.idSubsidiaria(),
                        idOrganizacao
                );

        validarEmpresa(
                usuarioEmpresa,
                subsidiaria
        );

        if (repository
                .existsByUsuarioEmpresaAndSubsidiariaAndStatus(
                        usuarioEmpresa,
                        subsidiaria,
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Usuario ja vinculado a esta subsidiaria."
            );
        }

        UsuarioSubsidiariaModel usuarioSubsidiaria =
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                );

        return repository.save(usuarioSubsidiaria);
    }

    @Transactional(readOnly = true)
    public Page<ListaUsuarioSubsidiariaRecord> listar(
            Pageable paginacao,
            Long idUsuarioEmpresa
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        Long idUsuarioEmpresaValidado =
                null;

        if (idUsuarioEmpresa != null) {
            UsuarioEmpresaModel usuarioEmpresa =
                    buscarUsuarioEmpresaAtivo(
                            idUsuarioEmpresa,
                            idOrganizacao
                    );

            idUsuarioEmpresaValidado =
                    usuarioEmpresa.getId();
        }

        return repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        idOrganizacao,
                        idUsuarioEmpresaValidado,
                        StatusEnum.ATIVO
                )
                .map(
                        ListaUsuarioSubsidiariaRecord::new
                );
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioSubsidiariaRecord detalhar(
            Long id
    ) {
        return new DetalheUsuarioSubsidiariaRecord(
                buscarVinculoAtivo(id)
        );
    }

    @Transactional
    public void excluir(
            Long id
    ) {
        UsuarioSubsidiariaModel usuarioSubsidiaria =
                buscarVinculoAtivo(id);

        Long idUsuario =
                usuarioLogadoService.getId();

        usuarioSubsidiaria.remover(idUsuario);
    }

    private void validarEmpresa(
            UsuarioEmpresaModel usuarioEmpresa,
            SubsidiariaModel subsidiaria
    ) {
        Long idEmpresaDoVinculo =
                usuarioEmpresa
                        .getEmpresa()
                        .getId();

        Long idEmpresaDaSubsidiaria =
                subsidiaria
                        .getEmpresa()
                        .getId();

        if (!Objects.equals(
                idEmpresaDoVinculo,
                idEmpresaDaSubsidiaria
        )) {
            throw new ValidacaoException(
                    "A subsidiaria nao pertence a empresa "
                            + "vinculada ao usuario."
            );
        }
    }

    private UsuarioEmpresaModel buscarUsuarioEmpresaAtivo(
            Long idUsuarioEmpresa,
            Long idOrganizacao
    ) {
        return usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        idUsuarioEmpresa,
                        idOrganizacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo entre usuario "
                                        + "e empresa nao encontrado "
                                        + "ou removido."
                        )
                );
    }

    private SubsidiariaModel buscarSubsidiariaAtiva(
            Long idSubsidiaria,
            Long idOrganizacao
    ) {
        return subsidiariaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        idSubsidiaria,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Subsidiaria nao encontrada "
                                        + "ou removida."
                        )
                );
    }

    private UsuarioSubsidiariaModel buscarVinculoAtivo(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        return repository
                .findByIdAndUsuarioEmpresaUsuarioOrganizacaoOrganizacaoIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        id,
                        idOrganizacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo entre usuario e "
                                        + "subsidiaria nao encontrado "
                                        + "ou removido."
                        )
                );
    }
}