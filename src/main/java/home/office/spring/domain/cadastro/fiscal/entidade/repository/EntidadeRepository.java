package home.office.spring.domain.cadastro.fiscal.entidade.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.cadastro.fiscal.entidade.model.EntidadeModel;

public interface EntidadeRepository extends JpaRepository<EntidadeModel, Long> {

	Page<EntidadeModel> findByNomeCompletoContaining(Pageable paginacao, String filtro);
	
	Page<EntidadeModel> findAllByAtivoTrue(Pageable paginacao);
	
}
