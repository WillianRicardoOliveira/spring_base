package home.office.spring.domain.endereco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.endereco.model.EstadoModel;

public interface EstadoRepository extends JpaRepository<EstadoModel, Long> {
	
}
