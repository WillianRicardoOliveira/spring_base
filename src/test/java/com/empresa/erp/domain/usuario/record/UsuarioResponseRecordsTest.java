package com.empresa.erp.domain.usuario.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

class UsuarioResponseRecordsTest {

    @Test
    @DisplayName("Deve criar DetalheUsuarioRecord a partir do model")
    void deveCriarDetalheUsuarioRecordAPartirDoModel() {
        var usuario = criarUsuario(1L, "usuario@teste.com");

        var detalhe = new DetalheUsuarioRecord(usuario);

        assertThat(detalhe.id()).isEqualTo(1L);
        assertThat(detalhe.email()).isEqualTo("usuario@teste.com");
        assertThat(detalhe.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar ListaUsuarioRecord a partir do model")
    void deveCriarListaUsuarioRecordAPartirDoModel() {
        var usuario = criarUsuario(2L, "financeiro@teste.com");

        var lista = new ListaUsuarioRecord(usuario);

        assertThat(lista.id()).isEqualTo(2L);
        assertThat(lista.email()).isEqualTo("financeiro@teste.com");
        assertThat(lista.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo no record de detalhe")
    void deveRefletirStatusInativoNoRecordDeDetalhe() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        usuario.inativar();

        var detalhe = new DetalheUsuarioRecord(usuario);

        assertThat(detalhe.status()).isEqualTo(StatusEnum.INATIVO);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}