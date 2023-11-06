package home.office.spring.domain.estoque.movimentacao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoModel, Long> {
	
	Page<MovimentacaoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
