package home.office.spring.domain.estoque.representante.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.estoque.representante.model.RepresentanteModel;

public interface RepresentanteRepository extends JpaRepository<RepresentanteModel, Long> {
	
	Page<RepresentanteModel> findAllByAtivoTrue(Pageable paginacao);
	
}
