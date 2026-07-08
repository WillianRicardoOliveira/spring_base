package com.empresa.erp.domain.acesso.permissao.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.old.StatusEnum;

class PermissaoResponseRecordsTest {

    @Test
    @DisplayName("Deve criar DetalhePermissaoRecord a partir do model")
    void deveCriarDetalhePermissaoRecordAPartirDoModel() {
        var permissao = criarPermissao(
                1L,
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        var detalhe = new DetalhePermissaoRecord(permissao);

        assertThat(detalhe.id()).isEqualTo(1L);
        assertThat(detalhe.nome()).isEqualTo("Listar usuarios");
        assertThat(detalhe.chave()).isEqualTo("ACESSO_USUARIO_LISTAR");
        assertThat(detalhe.descricao()).isEqualTo("Permite listar usuarios");
        assertThat(detalhe.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar ListaPermissaoRecord a partir do model")
    void deveCriarListaPermissaoRecordAPartirDoModel() {
        var permissao = criarPermissao(
                2L,
                "Editar usuarios",
                "ACESSO_USUARIO_EDITAR",
                "Permite editar usuarios"
        );

        var lista = new ListaPermissaoRecord(permissao);

        assertThat(lista.id()).isEqualTo(2L);
        assertThat(lista.nome()).isEqualTo("Editar usuarios");
        assertThat(lista.chave()).isEqualTo("ACESSO_USUARIO_EDITAR");
        assertThat(lista.descricao()).isEqualTo("Permite editar usuarios");
        assertThat(lista.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo no record de detalhe")
    void deveRefletirStatusInativoNoRecordDeDetalhe() {
        var permissao = criarPermissao(
                1L,
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );
        permissao.inativar();

        var detalhe = new DetalhePermissaoRecord(permissao);

        assertThat(detalhe.status()).isEqualTo(StatusEnum.INATIVO);
    }

    private PermissaoModel criarPermissao(Long id, String nome, String chave, String descricao) {
        var permissao = new PermissaoModel(new PermissaoRecord(nome, chave, descricao));
        ReflectionTestUtils.setField(permissao, "id", id);
        return permissao;
    }
}