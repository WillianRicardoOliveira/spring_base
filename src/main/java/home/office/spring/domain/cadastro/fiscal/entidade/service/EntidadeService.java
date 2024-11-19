package home.office.spring.domain.cadastro.fiscal.entidade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import home.office.spring.domain.cadastro.fiscal.entidade.model.EntidadeModel;
import home.office.spring.domain.cadastro.fiscal.entidade.record.AtualizaEntidadeRecord;
import home.office.spring.domain.cadastro.fiscal.entidade.record.DetalheEntidadeRecord;
import home.office.spring.domain.cadastro.fiscal.entidade.record.EntidadeRecord;
import home.office.spring.domain.cadastro.fiscal.entidade.record.ListaEntidadeRecord;
import home.office.spring.domain.cadastro.fiscal.entidade.repository.EntidadeRepository;
import home.office.spring.domain.fiscal.endereco.model.EnderecoModel;
import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.fiscal.endereco.repository.EnderecoRepository;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.repository.RegimeTributacaoFederalRepository;
import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import home.office.spring.domain.fiscal.setorAtividade.repository.SetorAtividadeRepository;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class EntidadeService {
	
	@Autowired
	private EntidadeRepository repository;
	
	@Autowired
	private RegimeTributacaoFederalRepository regimeTributacaoFederalRepository;
	
	@Autowired
	private SetorAtividadeRepository setorAtividadeRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Transactional
	public DetalheEntidadeRecord cadastrar(EntidadeRecord dados) {
		return new DetalheEntidadeRecord(repository.save(new EntidadeModel(dados, buscarRegime(dados.regimeTributacaoFederal().id()), buscarSetor(dados.setorAtividade().id()), criarEndereco(dados.endereco()))));			
	}
	
	@Transactional
	public DetalheEntidadeRecord atualizar(AtualizaEntidadeRecord dados) {		
		 EntidadeModel entidade = repository.findById(dados.id()).orElseThrow(() -> new ValidacaoException("Entidade não encontrada."));		 
		 entidade.atualizar(dados, buscarRegime(dados.regimeTributacaoFederal().id()), buscarSetor(dados.setorAtividade().id()));		
		 return new DetalheEntidadeRecord(entidade);
	}
	
	@Transactional
	public void remover(Long id, Boolean ativo) {
	    EntidadeModel entidade = repository.findById(id).orElseThrow(() -> new ValidacaoException("Entidade não encontrada."));
	    entidade.ativo(ativo);
	    repository.save(entidade);
	}
		
	public DetalheEntidadeRecord detalhar(Long id) {
	    return new DetalheEntidadeRecord(repository.findById(id).orElseThrow(() -> new ValidacaoException("Entidade não encontrada.")));
	}
		
	public Page<ListaEntidadeRecord> listar(Pageable paginacao, String filtro) {
		
		if(filtro != null) {
			return repository.findByNomeCompletoContaining(paginacao, filtro).map(ListaEntidadeRecord::new);
		} else {			
			return repository.findAllByAtivoTrue(paginacao).map(ListaEntidadeRecord::new);
		}
		
	}
	
	private EnderecoModel criarEndereco(EnderecoRecord enderecoRecord) {
	    return enderecoRepository.save(new EnderecoModel(enderecoRecord));	    
	}
	
	private RegimeTributacaoFederalModel buscarRegime(Long id) {
	    return regimeTributacaoFederalRepository.findById(id).orElseThrow(() -> new ValidacaoException("Regime de Tributação Federal não encontrado."));
	}

	private SetorAtividadeModel buscarSetor(Long id) {
	    return setorAtividadeRepository.findById(id).orElseThrow(() -> new ValidacaoException("Setor de Atividade não encontrado."));
	}
			
}
