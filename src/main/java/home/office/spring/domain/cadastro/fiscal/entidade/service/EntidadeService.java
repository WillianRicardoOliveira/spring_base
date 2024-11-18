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
import home.office.spring.domain.fiscal.endereco.repository.EnderecoRepository;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.repository.RegimeTributacaoFederalRepository;
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
	public EntidadeModel cadastrar(EntidadeRecord dados) {
		
		try {
			
			var endereco = new EnderecoModel(dados.endereco());
			
			enderecoRepository.save(endereco);
			
			var regimeTributacaoFederal = regimeTributacaoFederalRepository.getReferenceById(dados.regimeTributacaoFederal().id());
			
			var setorAtividade = setorAtividadeRepository.getReferenceById(dados.setorAtividade().id());
			
			var entidade = new EntidadeModel(dados, regimeTributacaoFederal, setorAtividade, endereco);
			
			repository.save(entidade);
			
			return entidade;
			
		} catch (ValidacaoException e) {
			
			throw new ValidacaoException("Não foi possível cadastrar a entidade.");
			
		}
		
	}
		
	public Page<ListaEntidadeRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeCompletoContaining(paginacao, filtro).map(ListaEntidadeRecord::new);
			} else {			
				return repository.findAllByAtivoTrue(paginacao).map(ListaEntidadeRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível listar as entidades.");
		}
	}
		
	@Transactional
	public DetalheEntidadeRecord atualizar(AtualizaEntidadeRecord dados) {
		
		try {
			
			var regime = regimeTributacaoFederalRepository.getReferenceById(dados.regimeTributacaoFederal().id());
			
			var setor = setorAtividadeRepository.getReferenceById(dados.setorAtividade().id());
			
			EntidadeModel entidade = repository.getReferenceById(dados.id());
			
			entidade.atualizar(dados, regime, setor);
			
			return new DetalheEntidadeRecord(entidade);
			
		} catch (ValidacaoException e) {
			
			throw new ValidacaoException("Não foi possível atualizar a entidade.");
			
		}
		
	}
	
	
	
	
	
	
	
	
	@Transactional
	public void excluir(Long id, Boolean ativo) {
		try {
			repository.getReferenceById(id).ativo(ativo);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível excluir a entidade.");
		}
	}
	
	public DetalheEntidadeRecord detalhar(Long id) {
		try {
			EntidadeModel entidade = repository.getReferenceById(id);
			return new DetalheEntidadeRecord(entidade);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível detalhamento a entidade.");
		}
	}
	
}
