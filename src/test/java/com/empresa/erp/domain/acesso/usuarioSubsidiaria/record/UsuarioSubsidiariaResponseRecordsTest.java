package com.empresa.erp.domain.acesso.usuarioSubsidiaria.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioSubsidiariaResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe a partir do model")
    void deveCriarDetalheAPartirDoModel() {
        var usuarioSubsidiaria =
                criarUsuarioSubsidiaria();

        var detalhe =
                new DetalheUsuarioSubsidiariaRecord(
                        usuarioSubsidiaria
                );

        assertThat(detalhe.id())
                .isEqualTo(4L);

        assertThat(detalhe.idUsuarioEmpresa())
                .isEqualTo(3L);

        assertThat(detalhe.idUsuario())
                .isEqualTo(1L);

        assertThat(detalhe.usuario())
                .isEqualTo("usuario@teste.com");

        assertThat(detalhe.idEmpresa())
                .isEqualTo(2L);

        assertThat(detalhe.empresa())
                .isEqualTo("Empresa Exemplo");

        assertThat(detalhe.idSubsidiaria())
                .isEqualTo(5L);

        assertThat(detalhe.subsidiaria())
                .isEqualTo("Filial Curitiba");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve criar item de listagem a partir do model"
    )
    void deveCriarItemDeListagemAPartirDoModel() {
        var usuarioSubsidiaria =
                criarUsuarioSubsidiaria();

        var lista =
                new ListaUsuarioSubsidiariaRecord(
                        usuarioSubsidiaria
                );

        assertThat(lista.id())
                .isEqualTo(4L);

        assertThat(lista.idUsuarioEmpresa())
                .isEqualTo(3L);

        assertThat(lista.idUsuario())
                .isEqualTo(1L);

        assertThat(lista.usuario())
                .isEqualTo("usuario@teste.com");

        assertThat(lista.idEmpresa())
                .isEqualTo(2L);

        assertThat(lista.empresa())
                .isEqualTo("Empresa Exemplo");

        assertThat(lista.idSubsidiaria())
                .isEqualTo(5L);

        assertThat(lista.subsidiaria())
                .isEqualTo("Filial Curitiba");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo")
    void deveRefletirStatusInativo() {
        var usuarioSubsidiaria =
                criarUsuarioSubsidiaria();

        usuarioSubsidiaria.inativar();

        var detalhe =
                new DetalheUsuarioSubsidiariaRecord(
                        usuarioSubsidiaria
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private UsuarioSubsidiariaModel
            criarUsuarioSubsidiaria() {
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

        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                1L
        );

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                2L
        );

        var usuarioEmpresa =
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        false
                );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        var subsidiaria = new SubsidiariaModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                subsidiaria,
                "id",
                5L
        );

        var usuarioSubsidiaria =
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                );

        ReflectionTestUtils.setField(
                usuarioSubsidiaria,
                "id",
                4L
        );

        return usuarioSubsidiaria;
    }
}