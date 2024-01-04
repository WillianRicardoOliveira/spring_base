package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.AtualizaSubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.DetalheSubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.ListaSubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.SubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.repository.SubCategoriaContaRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class SubCategoriaContaService {
	
	@Autowired
	private SubCategoriaContaRepository repository;
	
	@Transactional
	public SubCategoriaContaModel cadastrar(SubCategoriaContaRecord dados) {		
		try {
			var SubCategoriaConta = new SubCategoriaContaModel(dados);
			repository.save(SubCategoriaConta);		
			return SubCategoriaConta;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaSubCategoriaContaRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaSubCategoriaContaRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaSubCategoriaContaRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheSubCategoriaContaRecord atualizar(AtualizaSubCategoriaContaRecord dados) {
		try {
			SubCategoriaContaModel subCategoriaConta = repository.getReferenceById(dados.id());
			subCategoriaConta.atualizar(dados);
			return new DetalheSubCategoriaContaRecord(subCategoriaConta);
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
	
	public DetalheSubCategoriaContaRecord detalhar(Long id) {
		try {
			SubCategoriaContaModel subCategoriaConta = repository.getReferenceById(id);
			return new DetalheSubCategoriaContaRecord(subCategoriaConta);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
