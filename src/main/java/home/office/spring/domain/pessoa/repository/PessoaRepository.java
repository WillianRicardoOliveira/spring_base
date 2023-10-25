package home.office.spring.domain.pessoa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import home.office.spring.domain.pessoa.model.PessoaModel;

public interface PessoaRepository extends JpaRepository<PessoaModel, Long> {
	
	Page<PessoaModel> findAllByAtivoTrue(Pageable paginacao);
		
	@Query("""
			select
			 	pessoa
			from
			 	PessoaModel as pessoa
			join
			 	pessoa.usuario as usuario
			where
				usuario.id = :id
			""")
	
	public PessoaModel buscaPessoaUsuario(Long id);

}