package com.empresa.erp.modulos.fiscal.endereco.record;

import com.empresa.erp.modulos.fiscal.endereco.model.EnderecoModel;

public record DetalheEnderecoRecord(
		Long id,
		String cep,
		String localidade,
	    String uf,
		String bairro,
		String logradouro,
		String numero,
		String complemento
) {
	public DetalheEnderecoRecord(EnderecoModel dados) {
		this(
				dados.getId(),
				dados.getCep(),
				dados.getLocalidade(),
				dados.getUf(),
				dados.getBairro(),
				dados.getLogradouro(),
				dados.getNumero(),
				dados.getComplemento()
		);
	}
}