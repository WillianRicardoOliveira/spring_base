package com.empresa.erp.domain.acesso.perfil.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.DetalhePerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.ListaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository repository;

    @Transactional
    public PerfilModel cadastrar(PerfilRecord dados) {
    	if (repository.existsByNomeIgnoreCaseAndStatus(dados.nome(), StatusEnum.ATIVO)) {
    	    throw new ValidacaoException("Perfil ja cadastrado.");
    	}
    	PerfilModel perfil = new PerfilModel(dados);
        repository.save(perfil);
        return perfil;
    }

    @Transactional(readOnly = true)
    public Page<ListaPerfilRecord> listar(Pageable paginacao, String filtro) {
    	if (filtro != null && !filtro.isBlank()) {
    	    return repository.findByNomeContainingIgnoreCaseAndStatus(paginacao, filtro, StatusEnum.ATIVO).map(ListaPerfilRecord::new);
    	}
    	return repository.findAllByStatus(paginacao, StatusEnum.ATIVO).map(ListaPerfilRecord::new);
    }

    @Transactional
    public DetalhePerfilRecord atualizar(AtualizaPerfilRecord dados) {
        if (repository.existsByNomeIgnoreCaseAndStatusAndIdNot(dados.nome(), StatusEnum.ATIVO, dados.id())) {
            throw new ValidacaoException("Perfil ja cadastrado.");
        }
        PerfilModel perfil = repository.getReferenceById(dados.id());
        perfil.atualizar(dados);
        return new DetalhePerfilRecord(perfil);
    }

    @Transactional
    public void excluir(Long id) {
        repository.getReferenceById(id).remover();
    }

    @Transactional(readOnly = true)
    public DetalhePerfilRecord detalhar(Long id) {
        PerfilModel perfil = repository.getReferenceById(id);
        return new DetalhePerfilRecord(perfil);
    }
    
}