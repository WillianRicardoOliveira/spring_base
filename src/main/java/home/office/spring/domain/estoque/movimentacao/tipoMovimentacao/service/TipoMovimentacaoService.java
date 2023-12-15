package home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.model.TipoMovimentacaoModel;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.AtualizaTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.DetalheTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.ListaTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.TipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.repository.TipoMovimentacaoRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class TipoMovimentacaoService {
	
	@Autowired
	private TipoMovimentacaoRepository repository;
		
	@Transactional
	public TipoMovimentacaoModel cadastrar(TipoMovimentacaoRecord dados) {
		try {
			var tipoMovimentacao = new TipoMovimentacaoModel(dados);
			repository.save(tipoMovimentacao);		
			return tipoMovimentacao;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaTipoMovimentacaoRecord> listar(Pageable paginacao) {
		try {
			return repository.findAllByAtivoTrue(paginacao).map(ListaTipoMovimentacaoRecord::new);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheTipoMovimentacaoRecord atualizar(AtualizaTipoMovimentacaoRecord dados) {
		try {
			TipoMovimentacaoModel tipoMovimentacao = repository.getReferenceById(dados.id());
			tipoMovimentacao.atualizar(dados);
			return new DetalheTipoMovimentacaoRecord(tipoMovimentacao);
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
	
	public DetalheTipoMovimentacaoRecord detalhar(Long id) {
		try {
			TipoMovimentacaoModel tipoMovimentacao = repository.getReferenceById(id);
			return new DetalheTipoMovimentacaoRecord(tipoMovimentacao);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
