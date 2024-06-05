package home.office.spring.domain.fiscal.fornecedor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;

public interface FornecedorRepository extends JpaRepository<FornecedorModel, Long> {

	Page<FornecedorModel> findByRazaoSocialContaining(Pageable paginacao, String filtro);
	
	Page<FornecedorModel> findAllByAtivoTrue(Pageable paginacao);
	
}
