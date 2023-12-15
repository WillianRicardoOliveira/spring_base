package home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.model.TipoMovimentacaoModel;

public interface TipoMovimentacaoRepository extends JpaRepository<TipoMovimentacaoModel, Long> {
	
	Page<TipoMovimentacaoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
