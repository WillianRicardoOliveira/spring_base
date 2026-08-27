package com.empresa.erp.domain.configuracao.empresa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.DetalheEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final SubsidiariaRepository
            subsidiariaRepository;

    private final UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional
    public EmpresaModel cadastrar(
            EmpresaRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        String nome = normalizarNome(dados.nome());

        if (repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        idOrganizacao,
                        nome,
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Empresa ja cadastrada."
            );
        }

        OrganizacaoModel organizacao =
                organizacaoRepository.getReferenceById(
                        idOrganizacao
                );

        EmpresaModel empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(nome)
        );

        return repository.save(empresa);
    }

    @Transactional(readOnly = true)
    public Page<ListaEmpresaRecord> listar(
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
                    .map(ListaEmpresaRecord::new);
        }

        return repository
                .findAllByOrganizacaoIdAndStatus(
                        paginacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .map(ListaEmpresaRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalheEmpresaRecord detalhar(
            Long id
    ) {
        EmpresaModel empresa =
                buscarEmpresaAtiva(id);

        return new DetalheEmpresaRecord(empresa);
    }

    @Transactional
    public DetalheEmpresaRecord atualizar(
            AtualizaEmpresaRecord dados
    ) {
        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        String nome = normalizarNome(dados.nome());

        EmpresaModel empresa =
                buscarEmpresaAtiva(dados.id());

        if (repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        idOrganizacao,
                        nome,
                        StatusEnum.ATIVO,
                        empresa.getId()
                )
        ) {
            throw new ValidacaoException(
                    "Empresa ja cadastrada."
            );
        }

        empresa.atualizar(
                new AtualizaEmpresaRecord(
                        empresa.getId(),
                        nome
                )
        );

        return new DetalheEmpresaRecord(empresa);
    }

    @Transactional
    public void excluir(Long id) {
        EmpresaModel empresa =
                buscarEmpresaAtiva(id);

        validarAusenciaDeSubsidiarias(empresa);
        validarAusenciaDeUsuarios(empresa);

        Long idUsuario =
                usuarioLogadoService.getId();

        empresa.remover(idUsuario);
    }

    private void validarAusenciaDeSubsidiarias(
            EmpresaModel empresa
    ) {
        if (subsidiariaRepository
                .existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Empresa possui subsidiarias ativas "
                            + "e nao pode ser removida."
            );
        }
    }

    private void validarAusenciaDeUsuarios(
            EmpresaModel empresa
    ) {
        if (usuarioEmpresaRepository
                .existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                )
        ) {
            throw new ValidacaoException(
                    "Empresa possui usuarios vinculados "
                            + "e nao pode ser removida."
            );
        }
    }

    private EmpresaModel buscarEmpresaAtiva(
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
                                "Empresa nao encontrada ou removida."
                        )
                );
    }

    private String normalizarNome(String nome) {
        return nome == null
                ? null
                : nome.trim().replaceAll("\\s+", " ");
    }
}