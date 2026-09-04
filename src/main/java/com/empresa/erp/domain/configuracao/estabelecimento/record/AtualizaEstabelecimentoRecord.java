package com.empresa.erp.domain.configuracao.estabelecimento.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizaEstabelecimentoRecord(

        @NotNull(message = "{estabelecimento.id.obrigatorio}")
        Long id,

        @NotBlank(message = "{estabelecimento.nome.obrigatorio}")
        @Size(max = 100, message = "{estabelecimento.nome.tamanho}")
        String nome

) {
}