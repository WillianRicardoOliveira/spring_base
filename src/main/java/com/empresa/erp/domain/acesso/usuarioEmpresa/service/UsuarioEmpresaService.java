package com.empresa.erp.domain.acesso.usuarioEmpresa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.DetalheUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.ListaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.UsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.empresa.service.EmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioEmpresaService {

    private final UsuarioEmpresaRepository repository;

    private final UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    private final UsuarioSubsidiariaRepository
            usuarioSubsidiariaRepository;

    private final EmpresaRepository
            empresaRepository;

    private final EmpresaService
            empresaService;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public UsuarioEmpresaModel cadastrar(
            UsuarioEmpresaRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        UsuarioOrganizacaoModel usuarioOrganizacao =
                buscarUsuarioAtivoNaOrganizacao(
                        dados.idUsuario(),
                        idOrganizacao
                );

        EmpresaModel empresa =
                buscarEmpresaAtiva(
                        dados.idEmpresa(),
                        idOrganizacao
                );

        if (repository
                .existsByUsuarioOrganizacaoIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        usuarioOrganizacao.getId(),
                        empresa.getId(),
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Usuario ja vinculado a esta empresa."
            );
        }

        UsuarioEmpresaModel usuarioEmpresa =
                new UsuarioEmpresaModel(
                        usuarioOrganizacao,
                        empresa,
                        dados.todasSubsidiarias()
                );

        return repository.save(usuarioEmpresa);
    }

    @Transactional(readOnly = true)
    public Page<ListaUsuarioEmpresaRecord> listar(
            Pageable paginacao,
            Long idUsuario,
            Long idEmpresa
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        Long idUsuarioOrganizacao =
                null;

        if (idUsuario != null) {
            UsuarioOrganizacaoModel usuarioOrganizacao =
                    buscarUsuarioAtivoNaOrganizacao(
                            idUsuario,
                            idOrganizacao
                    );

            idUsuarioOrganizacao =
                    usuarioOrganizacao.getId();
        }

        Long idEmpresaValidada =
                null;

        if (idEmpresa != null) {
            EmpresaModel empresa =
                    buscarEmpresaAtiva(
                            idEmpresa,
                            idOrganizacao
                    );

            idEmpresaValidada =
                    empresa.getId();
        }

        return repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        idOrganizacao,
                        idUsuarioOrganizacao,
                        idEmpresaValidada,
                        StatusEnum.ATIVO
                )
                .map(ListaUsuarioEmpresaRecord::new);
    }

    @Transactional(readOnly = true)
    public Page<ListaEmpresaRecord> listarEmpresas(
            Pageable paginacao,
            String filtro
    ) {
        return empresaService.listar(
                paginacao,
                filtro
        );
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioEmpresaRecord detalhar(
            Long id
    ) {
        return new DetalheUsuarioEmpresaRecord(
                buscarVinculoAtivo(id)
        );
    }

    @Transactional
    public DetalheUsuarioEmpresaRecord atualizar(
            AtualizaUsuarioEmpresaRecord dados
    ) {
        UsuarioEmpresaModel usuarioEmpresa =
                buscarVinculoAtivo(
                        dados.id()
                );

        validarAlteracaoParaTodasSubsidiarias(
                usuarioEmpresa,
                dados
        );

        usuarioEmpresa.atualizar(dados);

        return new DetalheUsuarioEmpresaRecord(
                usuarioEmpresa
        );
    }

    @Transactional
    public void excluir(
            Long id
    ) {
        UsuarioEmpresaModel usuarioEmpresa =
                buscarVinculoAtivo(id);

        validarAusenciaDeSubsidiariasVinculadas(
                usuarioEmpresa
        );

        Long idUsuario =
                usuarioLogadoService.getId();

        usuarioEmpresa.remover(idUsuario);
    }

    private void validarAlteracaoParaTodasSubsidiarias(
            UsuarioEmpresaModel usuarioEmpresa,
            AtualizaUsuarioEmpresaRecord dados
    ) {
        boolean habilitandoTodasSubsidiarias =
                Boolean.FALSE.equals(
                        usuarioEmpresa.getTodasSubsidiarias()
                )
                && Boolean.TRUE.equals(
                        dados.todasSubsidiarias()
                );

        if (!habilitandoTodasSubsidiarias) {
            return;
        }

        if (usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Remova os vinculos com subsidiarias "
                            + "antes de habilitar o acesso "
                            + "a todas as subsidiarias."
            );
        }
    }

    private void validarAusenciaDeSubsidiariasVinculadas(
            UsuarioEmpresaModel usuarioEmpresa
    ) {
        if (usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "O vinculo entre usuario e empresa "
                            + "possui subsidiarias vinculadas "
                            + "e nao pode ser removido."
            );
        }
    }

    private UsuarioEmpresaModel buscarVinculoAtivo(
            Long id
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        return repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        id,
                        idOrganizacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo entre usuario e empresa "
                                        + "nao encontrado ou removido."
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

    private EmpresaModel buscarEmpresaAtiva(
            Long idEmpresa,
            Long idOrganizacao
    ) {
        return empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        idEmpresa,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Empresa nao encontrada ou removida."
                        )
                );
    }
}