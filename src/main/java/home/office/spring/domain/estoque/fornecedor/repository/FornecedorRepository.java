package home.office.spring.domain.estoque.fornecedor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;

public interface FornecedorRepository extends JpaRepository<FornecedorModel, Long> {
	
	Page<FornecedorModel> findAllByAtivoTrue(Pageable paginacao);
	
}
