package home.office.spring.domain.endereco.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.endereco.model.EnderecoModel;

public interface EnderecoRepository extends JpaRepository<EnderecoModel, Long> {
	
}
