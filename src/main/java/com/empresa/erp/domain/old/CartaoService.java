package com.empresa.erp.domain.old;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;

@Service
public class CartaoService {
	
	@Autowired
	private CartaoRepository repository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@Transactional
	public CartaoModel cadastrar(CartaoRecord dados) {		
		try {
			ContaModel conta = contaRepository.getReferenceById(dados.conta());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());
			var cartao = new CartaoModel(dados, conta, formaPagamento);
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
			ContaModel conta = contaRepository.getReferenceById(dados.conta());	
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());
			CartaoModel cartao = repository.getReferenceById(dados.id());
			cartao.atualizar(dados, conta, formaPagamento);
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
