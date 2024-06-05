package home.office.spring.domain.estoque.compra.service;

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

import home.office.spring.domain.estoque.compra.model.CompraModel;
import home.office.spring.domain.estoque.compra.record.AtualizaCompraRecord;
import home.office.spring.domain.estoque.compra.record.CompraRecord;
import home.office.spring.domain.estoque.compra.record.DetalheCompraRecord;
import home.office.spring.domain.estoque.compra.record.ListaCompraRecord;
import home.office.spring.domain.estoque.compra.repository.CompraRepository;
import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;

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
