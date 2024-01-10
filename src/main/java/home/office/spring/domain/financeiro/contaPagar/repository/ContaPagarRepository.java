package home.office.spring.domain.financeiro.contaPagar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.financeiro.contaPagar.model.ContaPagarModel;

public interface ContaPagarRepository extends JpaRepository<ContaPagarModel, Long> {
	
	Page<ContaPagarModel> findByFornecedorNomeContaining(Pageable paginacao, String filtro);
		
	Page<ContaPagarModel> findAllByAtivoTrue(Pageable paginacao);
	
}
