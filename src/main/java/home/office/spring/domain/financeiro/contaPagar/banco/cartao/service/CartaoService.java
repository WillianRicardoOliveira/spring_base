package home.office.spring.domain.financeiro.contaPagar.banco.cartao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.financeiro.contaPagar.banco.cartao.model.CartaoModel;
import home.office.spring.domain.financeiro.contaPagar.banco.cartao.record.AtualizaCartaoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.cartao.record.CartaoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.cartao.record.DetalheCartaoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.cartao.record.ListaCartaoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.cartao.repository.CartaoRepository;
import home.office.spring.domain.financeiro.contaPagar.banco.model.BancoModel;
import home.office.spring.domain.financeiro.contaPagar.banco.repository.BancoRepository;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.repository.FormaPagamentoRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class CartaoService {
	
	@Autowired
	private CartaoRepository repository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@Transactional
	public CartaoModel cadastrar(CartaoRecord dados) {		
		try {
			BancoModel banco = bancoRepository.getReferenceById(dados.banco());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());
			var cartao = new CartaoModel(dados, banco, formaPagamento);
			repository.save(cartao);		
			return cartao;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaCartaoRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNumeroCartaoContaining(paginacao, filtro).map(ListaCartaoRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaCartaoRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheCartaoRecord atualizar(AtualizaCartaoRecord dados) {
		try {
			BancoModel banco = bancoRepository.getReferenceById(dados.banco());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());
			CartaoModel cartao = repository.getReferenceById(dados.id());
			cartao.atualizar(dados, banco, formaPagamento);
			return new DetalheCartaoRecord(cartao);
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
	
	public DetalheCartaoRecord detalhar(Long id) {
		try {
			CartaoModel cartao = repository.getReferenceById(id);
			return new DetalheCartaoRecord(cartao);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
