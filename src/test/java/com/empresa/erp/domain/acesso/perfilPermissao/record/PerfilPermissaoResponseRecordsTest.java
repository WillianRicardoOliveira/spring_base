package com.empresa.erp.domain.acesso.perfilPermissao.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.old.StatusEnum;

class PerfilPermissaoResponseRecordsTest {

    @Test
    @DisplayName("Deve criar DetalhePerfilPermissaoRecord a partir do model")
    void deveCriarDetalhePerfilPermissaoRecordAPartirDoModel() {
        var perfil = criarPerfil(1L, "Administrador");
        var permissao = criarPermissao(2L, "Listar usuarios", "ACESSO_USUARIO_LISTAR");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        var detalhe = new DetalhePerfilPermissaoRecord(perfilPermissao);

        assertThat(detalhe.id()).isEqualTo(3L);
        assertThat(detalhe.idPerfil()).isEqualTo(1L);
        assertThat(detalhe.perfil()).isEqualTo("Administrador");
        assertThat(detalhe.idPermissao()).isEqualTo(2L);
        assertThat(detalhe.permissao()).isEqualTo("Listar usuarios");
        assertThat(detalhe.chave()).isEqualTo("ACESSO_USUARIO_LISTAR");
        assertThat(detalhe.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar ListaPerfilPermissaoRecord a partir do model")
    void deveCriarListaPerfilPermissaoRecordAPartirDoModel() {
        var perfil = criarPerfil(1L, "Administrador");
        var permissao = criarPermissao(2L, "Editar usuarios", "ACESSO_USUARIO_EDITAR");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        var lista = new ListaPerfilPermissaoRecord(perfilPermissao);

        assertThat(lista.id()).isEqualTo(3L);
        assertThat(lista.idPermissao()).isEqualTo(2L);
        assertThat(lista.permissao()).isEqualTo("Editar usuarios");
        assertThat(lista.chave()).isEqualTo("ACESSO_USUARIO_EDITAR");
        assertThat(lista.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status removido no record de detalhe")
    void deveRefletirStatusRemovidoNoRecordDeDetalhe() {
        var perfil = criarPerfil(1L, "Administrador");
        var permissao = criarPermissao(2L, "Listar usuarios", "ACESSO_USUARIO_LISTAR");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        perfilPermissao.remover(10L);

        var detalhe = new DetalhePerfilPermissaoRecord(perfilPermissao);

        assertThat(detalhe.status()).isEqualTo(StatusEnum.REMOVIDO);
    }

    private PerfilModel criarPerfil(Long id, String nome) {
        var perfil = new PerfilModel(new PerfilRecord(nome, "Perfil " + nome));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }

    private PermissaoModel criarPermissao(Long id, String nome, String chave) {
        var permissao = new PermissaoModel(new PermissaoRecord(nome, chave, "Permite " + nome));
        ReflectionTestUtils.setField(permissao, "id", id);
        return permissao;
    }

    private PerfilPermissaoModel criarPerfilPermissao(Long id, PerfilModel perfil, PermissaoModel permissao) {
        var perfilPermissao = new PerfilPermissaoModel(perfil, permissao);
        ReflectionTestUtils.setField(perfilPermissao, "id", id);
        return perfilPermissao;
    }
}