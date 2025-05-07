package com.empresa.erp.modulos.fiscal.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;
import com.empresa.erp.domain.fiscal.endereco.repository.EnderecoRepository;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.repository.RegimeTributacaoFederalRepository;
import com.empresa.erp.domain.fiscal.setorAtividade.repository.SetorAtividadeRepository;
import com.empresa.erp.modulos.fiscal.model.EntidadeModel;
import com.empresa.erp.modulos.fiscal.record.AtualizaEntidadeRecord;
import com.empresa.erp.modulos.fiscal.record.DetalheEntidadeRecord;
import com.empresa.erp.modulos.fiscal.record.EntidadeRecord;
import com.empresa.erp.modulos.fiscal.record.ListaEntidadeRecord;
import com.empresa.erp.modulos.fiscal.repository.EntidadeRepository;
import com.empresa.erp.padrao.constant.StatusEnum;

import jakarta.transaction.Transactional;

@Service
public class EntidadeService {
	
	private final EntidadeRepository repository;
	private final RegimeTributacaoFederalRepository regime;
	private final SetorAtividadeRepository setor;

	public EntidadeService(EntidadeRepository repository, 
	                       RegimeTributacaoFederalRepository regime, 
	                       SetorAtividadeRepository setor, 
	                       EnderecoRepository endereco) {
	        this.repository = repository;
	        this.regime = regime;
	        this.setor = setor;
	}

	@Transactional
	public DetalheEntidadeRecord cadastrar(EntidadeRecord dados) {
					
		var regimeModel = regime.findById(dados.regime().id())
				.orElseThrow(() -> new ValidacaoException("Regime de Tributação Federal não encontrado."));
				
		var setorModel = setor.findById(dados.setor().id())
				.orElseThrow(() -> new ValidacaoException("Setor de Atividade não encontrado."));
		
		var enderecoModel = new EnderecoModel(dados.endereco());
		
		var matriz = dados.matriz() != null ? repository.findById(dados.matriz().id()).orElseThrow(() -> new ValidacaoException("Entidade matriz não encontrada.")) : null; 
		
		var entidade = new EntidadeModel(dados, regimeModel, setorModel, enderecoModel, matriz);
		
		return new DetalheEntidadeRecord(repository.save(entidade));
				
	}
		
	@Transactional
	public DetalheEntidadeRecord atualizar(AtualizaEntidadeRecord dados) {
		
		var entidade = repository.findById(dados.id())
				.orElseThrow(() -> new ValidacaoException("Entidade não encontrada."));		 
		 
		var regimeModel = regime.findById(dados.regime().id())
				.orElseThrow(() -> new ValidacaoException("Regime de Tributação Federal não encontrado."));
		 
		var setorModel = setor.findById(dados.setor().id())
				.orElseThrow(() -> new ValidacaoException("Setor de Atividade não encontrado."));
		
		var matriz = dados.matriz() != null ? repository.findById(dados.matriz().id()).orElseThrow(() -> new ValidacaoException("Entidade matriz não encontrada.")) : null; 
		 
		entidade.atualizar(dados, regimeModel, setorModel, matriz);
		 
		return new DetalheEntidadeRecord(entidade);
		
	}
			
	public DetalheEntidadeRecord detalhar(Long id) {
		
		var entidade = repository.findById(id)
				.orElseThrow(() -> new ValidacaoException("Entidade não encontrada."));
		
		return new DetalheEntidadeRecord(entidade);
		
	}
	
	public Page<ListaEntidadeRecord> listar(Pageable paginacao, String filtro) {
		
	    return Optional.ofNullable(filtro)
	        .filter(f -> !f.isBlank())
	        .map(f -> repository.findByNomeCompletoContaining(paginacao, f))
	        .orElse(repository.findAllByStatus(paginacao, StatusEnum.ATIVO))
	        .map(ListaEntidadeRecord::new);
	    
	}
	
}
