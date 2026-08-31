package com.empresa.erp.domain.acesso.permissao.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PermissaoModelTest {

    @Test
    @DisplayName(
            "Não deve permitir criação pública direta de permissão"
    )
    void naoDevePermitirCriacaoPublicaDiretaDePermissao() {
        var construtores =
                PermissaoModel.class
                        .getDeclaredConstructors();

        assertThat(construtores)
                .isNotEmpty()
                .allMatch(
                        construtor ->
                                !Modifier.isPublic(
                                        construtor
                                                .getModifiers()
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve possuir construtor protegido sem argumentos para JPA"
    )
    void devePossuirConstrutorProtegidoSemArgumentosParaJpa()
            throws NoSuchMethodException {
        var construtor =
                PermissaoModel.class
                        .getDeclaredConstructor();

        assertThat(
                Modifier.isProtected(
                        construtor.getModifiers()
                )
        ).isTrue();
    }

    @Test
    @DisplayName(
            "Não deve expor operações de alteração manual"
    )
    void naoDeveExporOperacoesDeAlteracaoManual() {
        var nomesDosMetodosPublicos =
                Arrays.stream(
                        PermissaoModel.class
                                .getMethods()
                )
                        .map(
                                metodo ->
                                        metodo.getName()
                        )
                        .toList();

        assertThat(nomesDosMetodosPublicos)
                .doesNotContain(
                        "atualizar",
                        "inativar",
                        "reativar",
                        "remover"
                );
    }
}