package home.office.spring.domain.financeiro.contaPagar.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.record.DetalheCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.DetalheSubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.model.ContaPagarModel;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.record.DetalheStatusPagamentoRecord;

	public record DetalheContaPagarRecord(	
			Long id,
			DetalheFornecedorRecord fornecedor,
			DetalheCategoriaContaRecord categoriaConta,
			DetalheSubCategoriaContaRecord subCategoriaConta,
			String descricao,
			BigDecimal valor,
			Integer parcelas,
			DetalheStatusPagamentoRecord statusPagamento,
			DetalheFormaPagamentoRecord formaPagamento
			) {
		
	public DetalheContaPagarRecord(ContaPagarModel dados) {
		
			this(	
					dados.getId(),
					new DetalheFornecedorRecord(dados.getFornecedor()),
					new DetalheCategoriaContaRecord(dados.getSubCategoriaConta().getCategoriaConta()),
					new DetalheSubCategoriaContaRecord(dados.getSubCategoriaConta()),
					dados.getDescricao(),
					dados.getValor(),
					dados.getParcelas(),
					new DetalheStatusPagamentoRecord(dados.getStatusPagamento()),
					new DetalheFormaPagamentoRecord(dados.getFormaPagamento())
			);
				
	}

}