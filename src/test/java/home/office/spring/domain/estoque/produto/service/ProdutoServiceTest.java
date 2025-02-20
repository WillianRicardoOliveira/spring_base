package home.office.spring.domain.estoque.produto.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;
import com.empresa.erp.domain.estoque.produto.record.AtualizaProdutoRecord;
import com.empresa.erp.domain.estoque.produto.record.DetalheProdutoRecord;
import com.empresa.erp.domain.estoque.produto.record.ListaProdutoRecord;
import com.empresa.erp.domain.estoque.produto.record.ProdutoRecord;
import com.empresa.erp.domain.estoque.produto.repository.ProdutoRepository;
import com.empresa.erp.domain.estoque.produto.service.ProdutoService;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

	@InjectMocks
	private ProdutoService service;	
	@Mock
	private ProdutoRepository repository;
	@Mock
	private ProdutoModel model;
	@Mock
	private ProdutoRecord cadastroRecord;
	@Mock
	private ListaProdutoRecord listaRecord;
	@Mock
	private AtualizaProdutoRecord atualizaRecord;
	@Mock
	private DetalheProdutoRecord detalheRecord;
	@Captor
	private ArgumentCaptor<ProdutoModel> produtoCaptor;
		
	@Test
	@DisplayName("Varicar se está cadastrando")
	void cadastrar() {		
		// ARRANGE | GIVEN 
		// ACT | WHEN
		ProdutoModel resultado = service.cadastrar(cadastroRecord);
        // ASSERT | THEN
		BDDMockito.then(repository).should().save(produtoCaptor.capture());
        ProdutoModel argumentCapturado = produtoCaptor.getValue();
        Assertions.assertEquals(resultado, argumentCapturado);
	}
}
