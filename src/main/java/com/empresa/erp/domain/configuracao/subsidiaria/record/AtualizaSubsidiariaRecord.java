package com.empresa.erp.domain.configuracao.subsidiaria.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizaSubsidiariaRecord(

        @NotNull(message = "{subsidiaria.id.obrigatorio}")
        Long id,

        @NotBlank(message = "{subsidiaria.nome.obrigatorio}")
        @Size(max = 100, message = "{subsidiaria.nome.tamanho}")
        String nome

) {
}