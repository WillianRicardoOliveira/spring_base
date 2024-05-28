package home.office.spring.domain.fiscal.regimeTributacaoFederal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.AtualizaRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.ListaRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.RegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.repository.RegimeTributacaoFederalRepository;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class RegimeTributacaoFederalService {
	
	@Autowired
	private RegimeTributacaoFederalRepository repository;
	
	@Transactional
	public RegimeTributacaoFederalModel cadastrar(RegimeTributacaoFederalRecord dados) {		
		try {
			var regimeTributacaoFederal = new RegimeTributacaoFederalModel(dados);
			repository.save(regimeTributacaoFederal);		
			return regimeTributacaoFederal;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do Regime de Tributação Federal.");
		}
	}
	
	public Page<ListaRegimeTributacaoFederalRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaRegimeTributacaoFederalRecord::new);
			} else {			
				return repository.findAllByAtivoTrue(paginacao).map(ListaRegimeTributacaoFederalRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos Regimes de Tributação Federal.");
		}
	}
	
	@Transactional
	public DetalheRegimeTributacaoFederalRecord atualizar(AtualizaRegimeTributacaoFederalRecord dados) {
		try {
			RegimeTributacaoFederalModel regimeTributacaoFederal = repository.getReferenceById(dados.id());
			regimeTributacaoFederal.atualizar(dados);
			return new DetalheRegimeTributacaoFederalRecord(regimeTributacaoFederal);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do Regime de Tributação Federal.");
		}
	}
	
	@Transactional
	public void excluir(Long id) {
		try {
			repository.getReferenceById(id).inativar();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do Regime de Tributação Federal.");
		}
	}
	
	public DetalheRegimeTributacaoFederalRecord detalhar(Long id) {
		try {
			RegimeTributacaoFederalModel regimeTributacaoFederal = repository.getReferenceById(id);
			return new DetalheRegimeTributacaoFederalRecord(regimeTributacaoFederal);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do Regime de Tributação Federal.");
		}
	}
	
}
