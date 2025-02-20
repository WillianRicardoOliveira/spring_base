package home.office.spring.domain.estoque.fornecedor.service;
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

import com.empresa.erp.domain.fiscal.fornecedor.model.FornecedorModel;
import com.empresa.erp.domain.fiscal.fornecedor.record.AtualizaFornecedorRecord;
import com.empresa.erp.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;
import com.empresa.erp.domain.fiscal.fornecedor.record.FornecedorRecord;
import com.empresa.erp.domain.fiscal.fornecedor.record.ListaFornecedorRecord;
import com.empresa.erp.domain.fiscal.fornecedor.repository.FornecedorRepository;
import com.empresa.erp.domain.fiscal.fornecedor.service.FornecedorService;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {
	
	@InjectMocks
	private EntidadeService service;	
	@Mock
	private EntidadeRepository repository;
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
*/