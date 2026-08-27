package com.empresa.erp.domain.acesso.permissao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.DetalhePermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.ListaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private static final EscopoPermissaoEnum ESCOPO_ORGANIZACAO =
            EscopoPermissaoEnum.ORGANIZACAO;

    private final PermissaoRepository repository;

    @Transactional(readOnly = true)
    public Page<ListaPermissaoRecord> listar(
            Pageable paginacao,
            String filtro
    ) {
        if (filtro != null && !filtro.isBlank()) {
            return repository
                    .findByNomeContainingIgnoreCaseAndEscopoAndStatus(
                            paginacao,
                            filtro,
                            ESCOPO_ORGANIZACAO,
                            StatusEnum.ATIVO
                    )
                    .map(ListaPermissaoRecord::new);
        }

        return repository
                .findAllByEscopoAndStatus(
                        paginacao,
                        ESCOPO_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
                .map(ListaPermissaoRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalhePermissaoRecord detalhar(Long id) {
        PermissaoModel permissao = repository
                .findByIdAndEscopoAndStatus(
                        id,
                        ESCOPO_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Permissao nao encontrada ou removida."
                        )
                );

        return new DetalhePermissaoRecord(permissao);
    }
}