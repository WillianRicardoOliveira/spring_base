package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;

public interface SubCategoriaContaRepository extends JpaRepository<SubCategoriaContaModel, Long> {
	
	Page<SubCategoriaContaModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<SubCategoriaContaModel> findAllByAtivoTrue(Pageable paginacao);
	
}
