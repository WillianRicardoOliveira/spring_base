package home.office.spring.domain.pessoa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.pessoa.model.PessoaModel;

public interface PessoaRepository extends JpaRepository<PessoaModel, Long> {
	
	Page<PessoaModel> findAllByAtivoTrue(Pageable paginacao);
	
}