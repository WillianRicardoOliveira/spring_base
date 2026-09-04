package com.empresa.erp.domain.acesso.usuarioEstabelecimento.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioEstabelecimentoResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe a partir do model")
    void deveCriarDetalheAPartirDoModel() {
        var usuarioEstabelecimento =
                criarUsuarioEstabelecimento();

        var detalhe =
                new DetalheUsuarioEstabelecimentoRecord(
                        usuarioEstabelecimento
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

        assertThat(detalhe.idEstabelecimento())
                .isEqualTo(5L);

        assertThat(detalhe.estabelecimento())
                .isEqualTo("Filial Curitiba");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(detalhe.auditoria())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve criar item de listagem a partir do model"
    )
    void deveCriarItemDeListagemAPartirDoModel() {
        var usuarioEstabelecimento =
                criarUsuarioEstabelecimento();

        var lista =
                new ListaUsuarioEstabelecimentoRecord(
                        usuarioEstabelecimento
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

        assertThat(lista.idEstabelecimento())
                .isEqualTo(5L);

        assertThat(lista.estabelecimento())
                .isEqualTo("Filial Curitiba");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo")
    void deveRefletirStatusInativo() {
        var usuarioEstabelecimento =
                criarUsuarioEstabelecimento();

        usuarioEstabelecimento.inativar();

        var detalhe =
                new DetalheUsuarioEstabelecimentoRecord(
                        usuarioEstabelecimento
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private UsuarioEstabelecimentoModel
            criarUsuarioEstabelecimento() {
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

        var usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        ReflectionTestUtils.setField(
                usuarioOrganizacao,
                "id",
                10L
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
                        usuarioOrganizacao,
                        empresa,
                        false
                );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        var estabelecimento = new EstabelecimentoModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                estabelecimento,
                "id",
                5L
        );

        var usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        estabelecimento
                );

        ReflectionTestUtils.setField(
                usuarioEstabelecimento,
                "id",
                4L
        );

        return usuarioEstabelecimento;
    }
}