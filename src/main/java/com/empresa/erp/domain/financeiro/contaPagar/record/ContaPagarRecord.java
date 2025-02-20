package com.empresa.erp.domain.financeiro.contaPagar.record;

import java.math.BigDecimal;

import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.DetalheSubCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.DetalheStatusPagamentoRecord;

import jakarta.validation.constraints.NotNull;

public record ContaPagarRecord(		
		//@NotNull(message = "{fornecedor.obrigatorio}")
		//DetalheFornecedorRecord fornecedor,
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
