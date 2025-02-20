package com.empresa.erp.domain.fiscal.setorAtividade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import com.empresa.erp.domain.fiscal.setorAtividade.record.AtualizaSetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.ListaSetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.SetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.repository.SetorAtividadeRepository;

import jakarta.transaction.Transactional;

@Service
public class SetorAtividadeService {
	
	@Autowired
	private SetorAtividadeRepository repository;
	
	@Transactional
	public SetorAtividadeModel cadastrar(SetorAtividadeRecord dados) {		
		try {
			var setorAtividade = new SetorAtividadeModel(dados);
			repository.save(setorAtividade);		
			return setorAtividade;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do Setor de Atividade.");
		}
	}
	
	public Page<ListaSetorAtividadeRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaSetorAtividadeRecord::new);
			} else {			
				return repository.findAllByAtivoTrue(paginacao).map(ListaSetorAtividadeRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos Setores de Atividade.");
		}
	}
	
	@Transactional
	public DetalheSetorAtividadeRecord atualizar(AtualizaSetorAtividadeRecord dados) {
		try {
			SetorAtividadeModel setorAtividade = repository.getReferenceById(dados.id());
			setorAtividade.atualizar(dados);
			return new DetalheSetorAtividadeRecord(setorAtividade);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do Setor de Atividade.");
		}
	}
	
	@Transactional
	public void excluir(Long id) {
		try {
			repository.getReferenceById(id).inativar();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do Setor de Atividade.");
		}
	}
	
	public DetalheSetorAtividadeRecord detalhar(Long id) {
		try {
			SetorAtividadeModel setorAtividade = repository.getReferenceById(id);
			return new DetalheSetorAtividadeRecord(setorAtividade);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do Setor de Atividade.");
		}
	}
	
}
