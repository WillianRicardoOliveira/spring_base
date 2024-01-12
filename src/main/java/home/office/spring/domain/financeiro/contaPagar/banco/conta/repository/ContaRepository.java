package home.office.spring.domain.financeiro.contaPagar.banco.conta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.financeiro.contaPagar.banco.conta.model.ContaModel;

public interface ContaRepository extends JpaRepository<ContaModel, Long> {
	
	Page<ContaModel> findByContaContaining(Pageable paginacao, String filtro);
	
	Page<ContaModel> findAllByAtivoTrue(Pageable paginacao);
	
}
