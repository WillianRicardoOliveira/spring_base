package home.office.spring.domain.estoque.compraItem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.estoque.compraItem.model.CompraItemModel;

public interface CompraItemRepository extends JpaRepository<CompraItemModel, Long> {
	
	Page<CompraItemModel> findAllByAtivoTrue(Pageable paginacao);
	
}
