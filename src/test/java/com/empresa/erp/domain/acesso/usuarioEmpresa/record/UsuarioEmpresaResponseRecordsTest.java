package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioEmpresaResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe a partir do model")
    void deveCriarDetalheAPartirDoModel() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var detalhe = new DetalheUsuarioEmpresaRecord(
                usuarioEmpresa
        );

        assertThat(detalhe.id()).isEqualTo(3L);
        assertThat(detalhe.idUsuario()).isEqualTo(1L);
        assertThat(detalhe.usuario())
                .isEqualTo("usuario@teste.com");
        assertThat(detalhe.idEmpresa()).isEqualTo(2L);
        assertThat(detalhe.empresa())
                .isEqualTo("Empresa Exemplo");
        assertThat(detalhe.todasSubsidiarias())
                .isTrue();
        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar item de listagem a partir do model")
    void deveCriarItemDeListagemAPartirDoModel() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var lista = new ListaUsuarioEmpresaRecord(
                usuarioEmpresa
        );

        assertThat(lista.id()).isEqualTo(3L);
        assertThat(lista.idUsuario()).isEqualTo(1L);
        assertThat(lista.usuario())
                .isEqualTo("usuario@teste.com");
        assertThat(lista.idEmpresa()).isEqualTo(2L);
        assertThat(lista.empresa())
                .isEqualTo("Empresa Exemplo");
        assertThat(lista.todasSubsidiarias())
                .isTrue();
        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo")
    void deveRefletirStatusInativo() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        usuarioEmpresa.inativar();

        var detalhe = new DetalheUsuarioEmpresaRecord(
                usuarioEmpresa
        );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private UsuarioEmpresaModel criarUsuarioEmpresa() {
        var usuario = new UsuarioModel(
                new UsuarioRecord(
                        "usuario@teste.com",
                        "123456"
                ),
                "senha-criptografada"
        );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                1L
        );

        var empresa = new EmpresaModel(
                new EmpresaRecord("Empresa Exemplo")
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                2L
        );

        var usuarioEmpresa = new UsuarioEmpresaModel(
                usuario,
                empresa,
                true
        );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        return usuarioEmpresa;
    }
}