package com.empresa.erp.domain.financeiro.contaPagar.record;
/*
import java.math.BigDecimal;

import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.DetalheCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.DetalheSubCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.model.ContaPagarModel;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.DetalheStatusPagamentoRecord;
import com.empresa.erp.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;

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

}*/