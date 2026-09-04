package com.empresa.erp.domain.configuracao.inicial.record;

import com.empresa.erp.domain.configuracao.inicial.model.ProximaEtapaConfiguracaoEnum;

public record EstadoConfiguracaoInicialRecord(
        boolean empresaCadastrada,
        ProximaEtapaConfiguracaoEnum proximaEtapa
) {
}