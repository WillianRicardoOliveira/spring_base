package home.office.spring.domain.estoque.fornecedor.service;

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

import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;
import home.office.spring.domain.fiscal.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.repository.FornecedorRepository;
import home.office.spring.domain.fiscal.fornecedor.service.FornecedorService;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {
	
	@InjectMocks
	private FornecedorService service;	
	@Mock
	private FornecedorRepository repository;
	@Mock
	private FornecedorModel model;
	@Mock
	private FornecedorRecord cadastroRecord;
	@Mock
	private ListaFornecedorRecord listaRecord;
	@Mock
	private AtualizaFornecedorRecord atualizaRecord;
	@Mock
	private DetalheFornecedorRecord detalheRecord;
	@Captor
	private ArgumentCaptor<FornecedorModel> fornecedorCaptor;
		
	@Test
	@DisplayName("Varicar se está cadastrando")
	void cadastrar() {		
		// ARRANGE | GIVEN 
		// ACT | WHEN
		FornecedorModel resultado = service.cadastrar(cadastroRecord);
        // ASSERT | THEN
		BDDMockito.then(repository).should().save(fornecedorCaptor.capture());
        FornecedorModel argumentCapturado = fornecedorCaptor.getValue();
        Assertions.assertEquals(resultado, argumentCapturado);
	}
		
}
