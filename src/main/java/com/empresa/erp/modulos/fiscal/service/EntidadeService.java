package com.empresa.erp.modulos.fiscal.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;
import com.empresa.erp.domain.fiscal.endereco.record.EnderecoRecord;
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
	private final EnderecoRepository endereco;

	public EntidadeService(EntidadeRepository repository, 
	                       RegimeTributacaoFederalRepository regime, 
	                       SetorAtividadeRepository setor, 
	                       EnderecoRepository endereco) {
	        this.repository = repository;
	        this.regime = regime;
	        this.setor = setor;
	        this.endereco = endereco;
	}

	@Transactional
	public DetalheEntidadeRecord cadastrar(EntidadeRecord dados) {
		return new DetalheEntidadeRecord(				
				repository.save(
						new EntidadeModel(
								dados,								
								buscar(dados.regime().id(), regime, "Regime de Tributação Federal não encontrado."),																
								buscar(dados.setor().id(), setor, "Setor de Atividade não encontrado."),								
								criarEndereco(dados.endereco()),								
								buscar(dados.matriz().id(), repository, "Entidade matriz não encontrada.")								
								)));
	}
	
	@Transactional
	public DetalheEntidadeRecord atualizar(AtualizaEntidadeRecord dados) {		
		 EntidadeModel entidade = repository.findById(dados.id()).orElseThrow(() -> new ValidacaoException("Entidade não encontrada."));		 
		 entidade.atualizar(
				 dados,				 
				 buscar(dados.regime().id(), regime, "Regime de Tributação Federal não encontrado."),				 
				 buscar(dados.setor().id(), setor, "Setor de Atividade não encontrado."),				 
				 buscar(dados.matriz().id(), repository, "Entidade matriz não encontrada.")				 
				 );		
		 return new DetalheEntidadeRecord(entidade);
	}
	
	@Transactional
	public void status(Long id, StatusEnum status) {
	    EntidadeModel entidade = repository.findById(id).orElseThrow(() -> new ValidacaoException("Entidade não encontrada."));
	    entidade.setStatus(status);
	   
	}
		
	public DetalheEntidadeRecord detalhar(Long id) {
		return new DetalheEntidadeRecord(buscar(id, repository, "Entidade não encontrada."));
	}
	
	public Page<ListaEntidadeRecord> listar(Pageable paginacao, String filtro) {
	    return Optional.ofNullable(filtro)
	        .filter(f -> !f.isBlank())
	        .map(f -> repository.findByNomeCompletoContaining(paginacao, f))
	        .orElse(repository.findAllByStatus(paginacao, StatusEnum.ATIVO))
	        .map(ListaEntidadeRecord::new);
	}

	private EnderecoModel criarEndereco(EnderecoRecord enderecoRecord) {
	    return endereco.save(new EnderecoModel(enderecoRecord));
	}
	
	private <T> T buscar(Long id, JpaRepository<T, Long> repository, String mensagemErro) {
	    return repository.findById(id).orElseThrow(() -> new ValidacaoException(mensagemErro));
	}
	
}
