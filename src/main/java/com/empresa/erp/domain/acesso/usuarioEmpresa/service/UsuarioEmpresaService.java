package com.empresa.erp.domain.acesso.usuarioEmpresa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.DetalheUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.ListaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.UsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.empresa.service.EmpresaService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioEmpresaService {

    private final UsuarioEmpresaRepository repository;

    private final UsuarioSubsidiariaRepository
            usuarioSubsidiariaRepository;

    private final UsuarioRepository usuarioRepository;

    private final EmpresaRepository empresaRepository;

    private final EmpresaService empresaService;

    private final UsuarioLogadoService usuarioLogadoService;

    @Transactional
    public UsuarioEmpresaModel cadastrar(
            UsuarioEmpresaRecord dados
    ) {
        UsuarioModel usuario = usuarioRepository
                .findByIdAndStatus(
                        dados.idUsuario(),
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Usuario nao encontrado ou removido."
                        )
                );

        EmpresaModel empresa = empresaRepository
                .findByIdAndStatus(
                        dados.idEmpresa(),
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Empresa nao encontrada ou removida."
                        )
                );

        if (repository.existsByUsuarioAndEmpresaAndStatus(
                usuario,
                empresa,
                StatusEnum.ATIVO
        )) {
            throw new ValidacaoException(
                    "Usuario ja vinculado a esta empresa."
            );
        }

        UsuarioEmpresaModel usuarioEmpresa =
                new UsuarioEmpresaModel(
                        usuario,
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
        boolean possuiUsuario = idUsuario != null;
        boolean possuiEmpresa = idEmpresa != null;

        if (possuiUsuario && possuiEmpresa) {
            return repository
                    .findAllByUsuarioIdAndEmpresaIdAndStatus(
                            paginacao,
                            idUsuario,
                            idEmpresa,
                            StatusEnum.ATIVO
                    )
                    .map(ListaUsuarioEmpresaRecord::new);
        }

        if (possuiUsuario) {
            return repository
                    .findAllByUsuarioIdAndStatus(
                            paginacao,
                            idUsuario,
                            StatusEnum.ATIVO
                    )
                    .map(ListaUsuarioEmpresaRecord::new);
        }

        if (possuiEmpresa) {
            return repository
                    .findAllByEmpresaIdAndStatus(
                            paginacao,
                            idEmpresa,
                            StatusEnum.ATIVO
                    )
                    .map(ListaUsuarioEmpresaRecord::new);
        }

        return repository
                .findAllByStatus(
                        paginacao,
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
                buscarVinculoAtivo(dados.id());

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
    public void excluir(Long id) {
        UsuarioEmpresaModel usuarioEmpresa =
                buscarVinculoAtivo(id);

        validarAusenciaDeSubsidiariasVinculadas(
                usuarioEmpresa
        );

        Long idUsuario = usuarioLogadoService.getId();

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
        return repository
                .findByIdAndStatus(
                        id,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo entre usuario e empresa "
                                        + "nao encontrado ou removido."
                        )
                );
    }
}