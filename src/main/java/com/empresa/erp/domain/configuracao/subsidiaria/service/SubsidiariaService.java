package com.empresa.erp.domain.configuracao.subsidiaria.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.record.AtualizaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.DetalheSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.ListaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.SubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
import com.empresa.erp.domain.old.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubsidiariaService {

    private final SubsidiariaRepository repository;

    private final EmpresaRepository empresaRepository;

    private final UsuarioSubsidiariaRepository
            usuarioSubsidiariaRepository;

    private final UsuarioLogadoService usuarioLogadoService;

    @Transactional
    public SubsidiariaModel cadastrar(
            SubsidiariaRecord dados
    ) {
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

        String nome = normalizarNome(dados.nome());

        if (repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                        empresa,
                        nome,
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Subsidiaria ja cadastrada para esta empresa."
            );
        }

        SubsidiariaModel subsidiaria =
                new SubsidiariaModel(
                        empresa,
                        nome
                );

        return repository.save(subsidiaria);
    }

    @Transactional(readOnly = true)
    public Page<ListaSubsidiariaRecord> listar(
            Pageable paginacao,
            Long idEmpresa,
            String filtro
    ) {
        boolean possuiEmpresa = idEmpresa != null;

        boolean possuiFiltro =
                filtro != null && !filtro.isBlank();

        if (possuiEmpresa && possuiFiltro) {
            return repository
                    .findByEmpresaIdAndNomeContainingIgnoreCaseAndStatus(
                            paginacao,
                            idEmpresa,
                            filtro.trim(),
                            StatusEnum.ATIVO
                    )
                    .map(ListaSubsidiariaRecord::new);
        }

        if (possuiEmpresa) {
            return repository
                    .findAllByEmpresaIdAndStatus(
                            paginacao,
                            idEmpresa,
                            StatusEnum.ATIVO
                    )
                    .map(ListaSubsidiariaRecord::new);
        }

        if (possuiFiltro) {
            return repository
                    .findByNomeContainingIgnoreCaseAndStatus(
                            paginacao,
                            filtro.trim(),
                            StatusEnum.ATIVO
                    )
                    .map(ListaSubsidiariaRecord::new);
        }

        return repository
                .findAllByStatus(
                        paginacao,
                        StatusEnum.ATIVO
                )
                .map(ListaSubsidiariaRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalheSubsidiariaRecord detalhar(
            Long id
    ) {
        return new DetalheSubsidiariaRecord(
                buscarSubsidiariaAtiva(id)
        );
    }

    @Transactional
    public DetalheSubsidiariaRecord atualizar(
            AtualizaSubsidiariaRecord dados
    ) {
        SubsidiariaModel subsidiaria =
                buscarSubsidiariaAtiva(dados.id());

        String nome = normalizarNome(dados.nome());

        if (repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        subsidiaria.getEmpresa(),
                        nome,
                        StatusEnum.ATIVO,
                        dados.id()
                )
        ) {
            throw new ValidacaoException(
                    "Subsidiaria ja cadastrada para esta empresa."
            );
        }

        subsidiaria.atualizar(
                new AtualizaSubsidiariaRecord(
                        dados.id(),
                        nome
                )
        );

        return new DetalheSubsidiariaRecord(
                subsidiaria
        );
    }

    @Transactional
    public void excluir(Long id) {
        SubsidiariaModel subsidiaria =
                buscarSubsidiariaAtiva(id);

        if (usuarioSubsidiariaRepository
                .existsBySubsidiariaIdAndStatus(
                        subsidiaria.getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Subsidiaria possui usuarios vinculados "
                            + "e nao pode ser removida."
            );
        }

        Long idUsuario = usuarioLogadoService.getId();

        subsidiaria.remover(idUsuario);
    }

    private SubsidiariaModel buscarSubsidiariaAtiva(
            Long id
    ) {
        return repository
                .findByIdAndStatus(
                        id,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Subsidiaria nao encontrada ou removida."
                        )
                );
    }

    private String normalizarNome(String nome) {
        return nome == null
                ? null
                : nome.trim().replaceAll("\\s+", " ");
    }
}