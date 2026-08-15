package com.empresa.erp.domain.configuracao.empresa.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRecord(

        @NotBlank(message = "{empresa.nome.obrigatorio}")
        @Size(max = 100, message = "{empresa.nome.tamanho}")
        String nome

) {
}