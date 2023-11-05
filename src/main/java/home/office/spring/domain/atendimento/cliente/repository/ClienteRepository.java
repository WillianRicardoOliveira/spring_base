package home.office.spring.domain.atendimento.cliente.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.atendimento.cliente.model.ClienteModel;

public interface ClienteRepository extends JpaRepository<ClienteModel, Long> {
	
	Page<ClienteModel> findAllByAtivoTrue(Pageable paginacao);
	
}
