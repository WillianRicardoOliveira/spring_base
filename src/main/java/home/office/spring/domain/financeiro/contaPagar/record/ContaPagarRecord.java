package home.office.spring.domain.financeiro.contaPagar.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.DetalheSubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.record.DetalheStatusPagamentoRecord;
import jakarta.validation.constraints.NotNull;

public record ContaPagarRecord(		
		@NotNull(message = "{fornecedor.obrigatorio}")
		DetalheFornecedorRecord fornecedor,
		@NotNull(message = "{subcategoriaconta.obrigatorio}")
		DetalheSubCategoriaContaRecord subCategoriaConta,
		String descricao,
		@NotNull(message = "{valor.obrigatorio}")
		BigDecimal valor,
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer parcelas,
		@NotNull(message = "{statuspagamento.obrigatorio}")
		DetalheStatusPagamentoRecord statusPagamento,
		@NotNull(message = "{metodopagamento.obrigatorio}")
		DetalheFormaPagamentoRecord formaPagamento
) {}
