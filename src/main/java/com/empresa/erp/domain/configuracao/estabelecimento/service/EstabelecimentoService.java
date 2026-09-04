package com.empresa.erp.domain.configuracao.estabelecimento.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.repository.UsuarioEstabelecimentoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.record.AtualizaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.DetalheEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.EstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.ListaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.repository.EstabelecimentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final UsuarioLogadoService usuarioLogadoService;
    private final ContextoOrganizacao contextoOrganizacao;

    @Transactional
    public EstabelecimentoModel cadastrar(EstabelecimentoRecord dados) {
        Long idOrganizacao = contextoOrganizacao.getIdOrganizacao();

        EmpresaModel empresa = empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        dados.idEmpresa(),
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Empresa nao encontrada ou removida."
                        )
                );

        String nome = normalizarNome(dados.nome());

        if (repository.existsByEmpresaAndNomeIgnoreCaseAndStatus(
                empresa,
                nome,
                StatusEnum.ATIVO
        )) {
            throw new ValidacaoException(
                    "Estabelecimento ja cadastrado para esta empresa."
            );
        }

        EstabelecimentoModel estabelecimento =
                new EstabelecimentoModel(empresa, nome);

        return repository.save(estabelecimento);
    }

    @Transactional(readOnly = true)
    public Page<ListaEstabelecimentoRecord> listar(
            Pageable paginacao,
            Long idEmpresa,
            String filtro
    ) {
        Long idOrganizacao = contextoOrganizacao.getIdOrganizacao();

        boolean possuiEmpresa = idEmpresa != null;
        boolean possuiFiltro = filtro != null && !filtro.isBlank();

        if (possuiEmpresa && possuiFiltro) {
            return repository
                    .findByEmpresaIdAndEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                            paginacao,
                            idEmpresa,
                            idOrganizacao,
                            filtro.trim(),
                            StatusEnum.ATIVO
                    )
                    .map(ListaEstabelecimentoRecord::new);
        }

        if (possuiEmpresa) {
            return repository
                    .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                            paginacao,
                            idEmpresa,
                            idOrganizacao,
                            StatusEnum.ATIVO
                    )
                    .map(ListaEstabelecimentoRecord::new);
        }

        if (possuiFiltro) {
            return repository
                    .findByEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                            paginacao,
                            idOrganizacao,
                            filtro.trim(),
                            StatusEnum.ATIVO
                    )
                    .map(ListaEstabelecimentoRecord::new);
        }

        return repository
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .map(ListaEstabelecimentoRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalheEstabelecimentoRecord detalhar(Long id) {
        return new DetalheEstabelecimentoRecord(
                buscarEstabelecimentoAtivo(id)
        );
    }

    @Transactional
    public DetalheEstabelecimentoRecord atualizar(
            AtualizaEstabelecimentoRecord dados
    ) {
        EstabelecimentoModel estabelecimento =
                buscarEstabelecimentoAtivo(dados.id());

        String nome = normalizarNome(dados.nome());

        if (repository.existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                estabelecimento.getEmpresa(),
                nome,
                StatusEnum.ATIVO,
                estabelecimento.getId()
        )) {
            throw new ValidacaoException(
                    "Estabelecimento ja cadastrado para esta empresa."
            );
        }

        estabelecimento.atualizar(
                new AtualizaEstabelecimentoRecord(
                        estabelecimento.getId(),
                        nome
                )
        );

        return new DetalheEstabelecimentoRecord(estabelecimento);
    }

    @Transactional
    public void excluir(Long id) {
        EstabelecimentoModel estabelecimento =
                buscarEstabelecimentoAtivo(id);

        if (usuarioEstabelecimentoRepository.existsByEstabelecimentoIdAndStatus(
                estabelecimento.getId(),
                StatusEnum.ATIVO
        )) {
            throw new ValidacaoException(
                    "Estabelecimento possui usuarios vinculados e nao pode ser removido."
            );
        }

        estabelecimento.remover(usuarioLogadoService.getId());
    }

    private EstabelecimentoModel buscarEstabelecimentoAtivo(Long id) {
        Long idOrganizacao = contextoOrganizacao.getIdOrganizacao();

        return repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        id,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Estabelecimento nao encontrado ou removido."
                        )
                );
    }

    private String normalizarNome(String nome) {
        return nome == null
                ? null
                : nome.trim().replaceAll("\\s+", " ");
    }
}