package home.office.spring.domain.financeiro.contaPagar.banco.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.financeiro.contaPagar.banco.model.BancoModel;
import home.office.spring.domain.financeiro.contaPagar.banco.record.AtualizaBancoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.record.BancoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.record.DetalheBancoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.record.ListaBancoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.repository.BancoRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class BancoService {
	
	@Autowired
	private BancoRepository repository;
	
	@Transactional
	public BancoModel cadastrar(BancoRecord dados) {		
		try {
			var banco = new BancoModel(dados);
			repository.save(banco);		
			return banco;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaBancoRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaBancoRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaBancoRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheBancoRecord atualizar(AtualizaBancoRecord dados) {
		try {
			BancoModel banco = repository.getReferenceById(dados.id());
			banco.atualizar(dados);
			return new DetalheBancoRecord(banco);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização.");
		}
	}
	
	@Transactional
	public void excluir(Long id) {
		try {
			repository.getReferenceById(id).inativar();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão.");
		}
	}
	
	public DetalheBancoRecord detalhar(Long id) {
		try {
			BancoModel banco = repository.getReferenceById(id);
			return new DetalheBancoRecord(banco);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
