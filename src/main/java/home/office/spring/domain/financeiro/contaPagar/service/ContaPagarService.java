package home.office.spring.domain.financeiro.contaPagar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.repository.SubCategoriaContaRepository;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.repository.FormaPagamentoRepository;
import home.office.spring.domain.financeiro.contaPagar.model.ContaPagarModel;
import home.office.spring.domain.financeiro.contaPagar.record.AtualizaContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.record.ContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.record.DetalheContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.record.ListaContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.repository.ContaPagarRepository;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.repository.StatusPagamentoRepository;
import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;
import home.office.spring.domain.fiscal.fornecedor.repository.FornecedorRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class ContaPagarService {
	
	@Autowired
	private ContaPagarRepository repository;
	
	@Autowired
	private FornecedorRepository fornecedorRepository;
	
	@Autowired
	private SubCategoriaContaRepository subCategoriaContaRepository;
	
	@Autowired
	private StatusPagamentoRepository statusPagamentoRepository;
	
	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	
	@Transactional
	public ContaPagarModel cadastrar(ContaPagarRecord dados) {		
		try {			
			FornecedorModel fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor().id());
			SubCategoriaContaModel subCategoriaConta = subCategoriaContaRepository.getReferenceById(dados.subCategoriaConta().id());			
			StatusPagamentoModel statusPagamento = statusPagamentoRepository.getReferenceById(dados.statusPagamento().id());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());			
			var contaPagar = new ContaPagarModel(dados, fornecedor, subCategoriaConta, statusPagamento, formaPagamento);
			repository.save(contaPagar);		
			return contaPagar;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaContaPagarRecord> listar(Pageable paginacao, String filtro) {
		try {
			//if(filtro != null) {
			//	return repository.findByFornecedorNomeContaining(paginacao, filtro).map(ListaContaPagarRecord::new);
			//} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaContaPagarRecord::new);
			//}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheContaPagarRecord atualizar(AtualizaContaPagarRecord dados) {
		try {
			FornecedorModel fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor().id());
			SubCategoriaContaModel subCategoriaConta = subCategoriaContaRepository.getReferenceById(dados.subCategoriaConta().id());
			StatusPagamentoModel statusPagamento = statusPagamentoRepository.getReferenceById(dados.statusPagamento().id());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());
			ContaPagarModel contaPagar = repository.getReferenceById(dados.id());
			contaPagar.atualizar(dados, fornecedor, subCategoriaConta, statusPagamento, formaPagamento);
			return new DetalheContaPagarRecord(contaPagar);
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
	
	public DetalheContaPagarRecord detalhar(Long id) {
		try {
			ContaPagarModel contaPagar = repository.getReferenceById(id);
			return new DetalheContaPagarRecord(contaPagar);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
