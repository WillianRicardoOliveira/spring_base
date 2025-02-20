package home.office.spring.domain.estoque.compra.service;
/*
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

import com.empresa.erp.domain.estoque.compra.model.CompraModel;
import com.empresa.erp.domain.estoque.compra.record.AtualizaCompraRecord;
import com.empresa.erp.domain.estoque.compra.record.CompraRecord;
import com.empresa.erp.domain.estoque.compra.record.DetalheCompraRecord;
import com.empresa.erp.domain.estoque.compra.record.ListaCompraRecord;
import com.empresa.erp.domain.estoque.compra.repository.CompraRepository;
import com.empresa.erp.domain.fiscal.fornecedor.model.FornecedorModel;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

	@InjectMocks
	private CompraService service;	
	@Mock
	private CompraRepository repository;
	@Mock
	private CompraModel model;
	@Mock
	private CompraRecord cadastroRecord;
	@Mock
	private ListaCompraRecord listaRecord;
	@Mock
	private AtualizaCompraRecord atualizaRecord;
	@Mock
	private DetalheCompraRecord detalheRecord;
	@Captor
	private ArgumentCaptor<CompraModel> compraCaptor;
		
	@Test
	@DisplayName("Varicar se está cadastrando")
	void cadastrar() {		
		// ARRANGE | GIVEN 
		// ACT | WHEN
		CompraModel resultado = service.cadastrar(cadastroRecord);
        // ASSERT | THEN
		BDDMockito.then(repository).should().save(compraCaptor.capture());
        CompraModel argumentCapturado = compraCaptor.getValue();
        Assertions.assertEquals(resultado, argumentCapturado);
	}
	
}
*/