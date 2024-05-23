package home.office.spring.domain.estoque.setorAtividade.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.estoque.setorAtividade.model.SetorAtividadeModel;

public interface SetorAtividadeRepository extends JpaRepository<SetorAtividadeModel, Long> {

	Page<SetorAtividadeModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<SetorAtividadeModel> findAllByAtivoTrue(Pageable paginacao);
	
}
