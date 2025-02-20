package home.office.spring.domain.estoque.compraItem.service;
/*
import java.math.BigDecimal;

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
import com.empresa.erp.domain.estoque.compra.repository.CompraRepository;
import com.empresa.erp.domain.estoque.compraItem.model.CompraItemModel;
import com.empresa.erp.domain.estoque.compraItem.record.AtualizaCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.CompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.DetalheCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.ListaCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.repository.CompraItemRepository;
import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;
import com.empresa.erp.domain.estoque.produto.repository.ProdutoRepository;
import com.empresa.erp.domain.fiscal.fornecedor.model.FornecedorModel;
import com.empresa.erp.domain.fiscal.fornecedor.repository.FornecedorRepository;

@ExtendWith(MockitoExtension.class)
class CompraItemServiceTest {

	@InjectMocks
	private CompraItemService service;	
	@Mock
	private CompraItemRepository repository;
	@Mock
	private CompraItemModel model;
	
	private CompraItemRecord cadastroRecord;
	@Mock
	private ListaCompraItemRecord listaRecord;
	@Mock
	private AtualizaCompraItemRecord atualizaRecord;
	@Mock
	private DetalheCompraItemRecord detalheRecord;
	@Captor
	private ArgumentCaptor<CompraItemModel> compraItemCaptor;
	@Mock
	private CompraRepository compraRepository;
	@Mock
	private EntidadeRepository fornecedorRepository;
	@Mock
	private ProdutoRepository produtoRepository;
	@Mock
	private CompraModel compraModel;
	@Mock
	private FornecedorModel fornecedorModel;
	@Mock
	private ProdutoModel produtoModel;
	
	@Test
	@DisplayName("Varicar se está cadastrando")
	void cadastrar() {		
		/
		// ARRANGE | GIVEN 
		this.cadastroRecord = new CompraItemRecord(1l, 1l, 1l, 2, new BigDecimal("2.15"));		
		BDDMockito.given(compraRepository.getReferenceById(cadastroRecord.compra())).willReturn(compraModel);
		BDDMockito.given(fornecedorRepository.getReferenceById(cadastroRecord.fornecedor())).willReturn(fornecedorModel);
		BDDMockito.given(produtoRepository.getReferenceById(cadastroRecord.produto())).willReturn(produtoModel);
		// ACT | WHEN
		CompraItemModel compraItemSalvo = service.cadastrar(cadastroRecord);
		// ASSERT | THEN
		BDDMockito.then(repository).should().save(compraItemCaptor.capture());
        CompraItemModel argumentCapturado = compraItemCaptor.getValue();
        Assertions.assertEquals(compraItemSalvo, argumentCapturado);
        Assertions.assertEquals(new BigDecimal("4.30"), argumentCapturado.getTotal());
        Assertions.assertEquals(argumentCapturado.getQuantidade(), argumentCapturado.getControle());
        Assertions.assertEquals(compraModel, argumentCapturado.getCompra());
        Assertions.assertEquals(fornecedorModel, argumentCapturado.getFornecedor());
        Assertions.assertEquals(produtoModel, argumentCapturado.getProduto());
        /
	}

}
*/