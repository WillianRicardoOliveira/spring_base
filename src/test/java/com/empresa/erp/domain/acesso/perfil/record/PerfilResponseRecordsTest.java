package com.empresa.erp.domain.acesso.perfil.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.old.StatusEnum;

class PerfilResponseRecordsTest {

    @Test
    @DisplayName("Deve criar DetalhePerfilRecord a partir do model")
    void deveCriarDetalhePerfilRecordAPartirDoModel() {
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");

        var detalhe = new DetalhePerfilRecord(perfil);

        assertThat(detalhe.id()).isEqualTo(1L);
        assertThat(detalhe.nome()).isEqualTo("Administrador");
        assertThat(detalhe.descricao()).isEqualTo("Perfil administrador");
        assertThat(detalhe.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar ListaPerfilRecord a partir do model")
    void deveCriarListaPerfilRecordAPartirDoModel() {
        var perfil = criarPerfil(2L, "Financeiro", "Perfil financeiro");

        var lista = new ListaPerfilRecord(perfil);

        assertThat(lista.id()).isEqualTo(2L);
        assertThat(lista.nome()).isEqualTo("Financeiro");
        assertThat(lista.descricao()).isEqualTo("Perfil financeiro");
        assertThat(lista.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo no record de detalhe")
    void deveRefletirStatusInativoNoRecordDeDetalhe() {
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        perfil.inativar();

        var detalhe = new DetalhePerfilRecord(perfil);

        assertThat(detalhe.status()).isEqualTo(StatusEnum.INATIVO);
    }

    private PerfilModel criarPerfil(Long id, String nome, String descricao) {
        var perfil = new PerfilModel(new PerfilRecord(nome, descricao));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }
}