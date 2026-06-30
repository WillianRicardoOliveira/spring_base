package com.empresa.erp.domain.acesso.permissao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.DetalhePermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.ListaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.padrao.constant.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final PermissaoRepository repository;

    @Transactional
    public PermissaoModel cadastrar(PermissaoRecord dados) {
        if (repository.existsByChaveIgnoreCaseAndStatus(dados.chave(), StatusEnum.ATIVO)) {
            throw new ValidacaoException("Permissao ja cadastrada.");
        }
        PermissaoModel permissao = new PermissaoModel(dados);
        repository.save(permissao);
        return permissao;
    }

    @Transactional(readOnly = true)
    public Page<ListaPermissaoRecord> listar(Pageable paginacao, String filtro) {
        if (filtro != null && !filtro.isBlank()) {
            return repository.findByNomeContainingIgnoreCaseAndStatus(paginacao, filtro, StatusEnum.ATIVO).map(ListaPermissaoRecord::new);
        }
        return repository.findAllByStatus(paginacao, StatusEnum.ATIVO).map(ListaPermissaoRecord::new);
    }

    @Transactional
    public DetalhePermissaoRecord atualizar(AtualizaPermissaoRecord dados) {
        if (repository.existsByChaveIgnoreCaseAndStatusAndIdNot(dados.chave(), StatusEnum.ATIVO, dados.id())) {
            throw new ValidacaoException("Permissao ja cadastrada.");
        }
        PermissaoModel permissao = repository.getReferenceById(dados.id());
        permissao.atualizar(dados);
        return new DetalhePermissaoRecord(permissao);
    }

    @Transactional
    public void excluir(Long id) {
        repository.getReferenceById(id).remover();
    }

    @Transactional(readOnly = true)
    public DetalhePermissaoRecord detalhar(Long id) {
        PermissaoModel permissao = repository.getReferenceById(id);
        return new DetalhePermissaoRecord(permissao);
    }
    
}