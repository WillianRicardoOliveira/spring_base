package com.empresa.erp.domain.plataforma.organizacao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.plataforma.organizacao.record.DetalheOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.ListaOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.OrganizacaoRecord;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizacaoPlataformaService {

    private final OrganizacaoRepository repository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    @Transactional(readOnly = true)
    public Page<ListaOrganizacaoRecord> listar(
            Pageable paginacao,
            String filtro
    ) {
        String filtroNormalizado =
                normalizarFiltro(filtro);

        return repository
                .findByNomeContainingIgnoreCase(
                        paginacao,
                        filtroNormalizado
                )
                .map(ListaOrganizacaoRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalheOrganizacaoRecord detalhar(
            Long id
    ) {
        OrganizacaoModel organizacao =
                repository.findById(id)
                        .orElseThrow(
                                EntityNotFoundException::new
                        );

        return new DetalheOrganizacaoRecord(
                organizacao
        );
    }

    @Transactional
    public DetalheOrganizacaoRecord editar(
            Long id,
            OrganizacaoRecord dados
    ) {
        OrganizacaoModel organizacao =
                buscarNaoRemovidaParaAtualizacao(id);

        organizacao.atualizarNome(
                dados.nome()
        );

        return new DetalheOrganizacaoRecord(
                organizacao
        );
    }

    @Transactional
    public DetalheOrganizacaoRecord inativar(
            Long id
    ) {
        OrganizacaoModel organizacao =
                buscarNaoRemovidaParaAtualizacao(id);

        if (!StatusEnum.INATIVO.equals(
                organizacao.getStatus()
        )) {
            organizacao.inativar();
        }

        return new DetalheOrganizacaoRecord(
                organizacao
        );
    }

    @Transactional
    public DetalheOrganizacaoRecord reativar(
            Long id
    ) {
        OrganizacaoModel organizacao =
                buscarNaoRemovidaParaAtualizacao(id);

        if (!StatusEnum.ATIVO.equals(
                organizacao.getStatus()
        )) {
            organizacao.reativar();
        }

        return new DetalheOrganizacaoRecord(
                organizacao
        );
    }

    @Transactional
    public void remover(
            Long id
    ) {
        OrganizacaoModel organizacao =
                buscarNaoRemovidaParaAtualizacao(id);

        Long idUsuario =
                usuarioLogadoService.getId();

        organizacao.remover(idUsuario);
    }

    private OrganizacaoModel
            buscarNaoRemovidaParaAtualizacao(
                    Long id
            ) {
        return repository
                .buscarPorIdNaoRemovidoParaAtualizacao(
                        id,
                        StatusEnum.REMOVIDO
                )
                .orElseThrow(
                        EntityNotFoundException::new
                );
    }

    private String normalizarFiltro(
            String filtro
    ) {
        return filtro == null
                ? ""
                : filtro.trim();
    }
}