package home.office.spring.domain.estoque.movimentacao.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.domain.old.CompraModel;
import com.empresa.erp.domain.old.CompraRepository;
import com.empresa.erp.domain.old.DetalheMovimentacaoRecord;
import com.empresa.erp.domain.old.ListaMovimentacaoRecord;
import com.empresa.erp.domain.old.MovimentacaoModel;
import com.empresa.erp.domain.old.MovimentacaoRecord;
import com.empresa.erp.domain.old.MovimentacaoRepository;
import com.empresa.erp.domain.old.MovimentacaoService;
import com.empresa.erp.domain.old.ProdutoModel;
import com.empresa.erp.domain.old.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceTest {
	
	@InjectMocks
	private MovimentacaoService service;	
	@Mock
	private MovimentacaoRepository repository;
	@Mock
	private MovimentacaoModel model;
	
	private MovimentacaoRecord cadastroRecord;
	@Mock
	private ListaMovimentacaoRecord listaRecord;
	@Mock
	private DetalheMovimentacaoRecord detalheRecord;
	@Captor
	private ArgumentCaptor<MovimentacaoModel> movimentacaoCaptor;
	@Mock
	private CompraModel compraModel;
	@Mock
	private ProdutoModel produtoModel;
	@Mock
	private ProdutoRepository produtoRepository;
	@Mock
	private CompraRepository compraRepository;
	@Captor
	private ArgumentCaptor<ProdutoModel> produtoCaptor;
	
	@Test
	@DisplayName("Varicar se está cadastrando o Inventario")
	void cadastrarInventario() {	
		/*
		// ARRANGE | GIVEN 
		this.cadastroRecord = new MovimentacaoRecord(TipoMovimentacao.INVENTARIO, null, null, 1l, 2);
		BDDMockito.given(produtoRepository.getReferenceById(cadastroRecord.produto())).willReturn(produtoModel);
		// ACT | WHEN
		MovimentacaoModel movimentacaoSalva = service.cadastrar(cadastroRecord);
		// ASSERT | THEN
		BDDMockito.then(produtoRepository).should().save(produtoCaptor.capture());
		BDDMockito.then(repository).should().save(movimentacaoCaptor.capture());		
		
		ProdutoModel produtoCapturado = produtoCaptor.getValue();
		MovimentacaoModel movimentacaoCapturado = movimentacaoCaptor.getValue();
		
        Assertions.assertEquals(produtoCapturado, movimentacaoCapturado.getProduto());
		
        Assertions.assertEquals(movimentacaoSalva, movimentacaoCapturado);
        */
        //Assertions.assertEquals(2, produtoCapturado.getQuantidade());
		
	}
	
	@Test
	@DisplayName("Varicar se está cadastrando o Danos")
	void cadastrarDanos() {}
	
	@Test
	@DisplayName("Varicar se está cadastrando a Devolucao")
	void cadastrarDevolucao() {}
	
	@Test
	@DisplayName("Varicar se está cadastrando a Saida")
	void cadastrarSaida() {}
}
