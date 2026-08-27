package com.empresa.erp.domain.acesso.usuarioPerfil.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioPerfilResponseRecordsTest {

    @Test
    @DisplayName(
            "Deve criar DetalheUsuarioPerfilRecord a partir do modelo"
    )
    void deveCriarDetalheUsuarioPerfilRecordAPartirDoModelo() {
        OrganizacaoModel organizacao =
                criarOrganizacao(
                        100L,
                        "Organização Principal"
                );

        UsuarioModel usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com"
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        4L,
                        usuario,
                        organizacao
                );

        PerfilModel perfil =
                criarPerfil(
                        2L,
                        organizacao,
                        "Administrador"
                );

        UsuarioPerfilModel usuarioPerfil =
                criarUsuarioPerfil(
                        3L,
                        usuarioOrganizacao,
                        perfil
                );

        DetalheUsuarioPerfilRecord detalhe =
                new DetalheUsuarioPerfilRecord(
                        usuarioPerfil
                );

        assertThat(detalhe.id())
                .isEqualTo(3L);

        assertThat(detalhe.idUsuario())
                .isEqualTo(1L);

        assertThat(detalhe.usuario())
                .isEqualTo("usuario@teste.com");

        assertThat(detalhe.idPerfil())
                .isEqualTo(2L);

        assertThat(detalhe.perfil())
                .isEqualTo("Administrador");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve criar ListaUsuarioPerfilRecord a partir do modelo"
    )
    void deveCriarListaUsuarioPerfilRecordAPartirDoModelo() {
        OrganizacaoModel organizacao =
                criarOrganizacao(
                        100L,
                        "Organização Principal"
                );

        UsuarioModel usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com"
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        4L,
                        usuario,
                        organizacao
                );

        PerfilModel perfil =
                criarPerfil(
                        2L,
                        organizacao,
                        "Financeiro"
                );

        UsuarioPerfilModel usuarioPerfil =
                criarUsuarioPerfil(
                        3L,
                        usuarioOrganizacao,
                        perfil
                );

        ListaUsuarioPerfilRecord lista =
                new ListaUsuarioPerfilRecord(
                        usuarioPerfil
                );

        assertThat(lista.id())
                .isEqualTo(3L);

        assertThat(lista.idPerfil())
                .isEqualTo(2L);

        assertThat(lista.perfil())
                .isEqualTo("Financeiro");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve refletir status removido no record de detalhe"
    )
    void deveRefletirStatusRemovidoNoRecordDeDetalhe() {
        OrganizacaoModel organizacao =
                criarOrganizacao(
                        100L,
                        "Organização Principal"
                );

        UsuarioModel usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com"
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        4L,
                        usuario,
                        organizacao
                );

        PerfilModel perfil =
                criarPerfil(
                        2L,
                        organizacao,
                        "Administrador"
                );

        UsuarioPerfilModel usuarioPerfil =
                criarUsuarioPerfil(
                        3L,
                        usuarioOrganizacao,
                        perfil
                );

        usuarioPerfil.remover(10L);

        DetalheUsuarioPerfilRecord detalhe =
                new DetalheUsuarioPerfilRecord(
                        usuarioPerfil
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.REMOVIDO);
    }

    @Test
    @DisplayName(
            "Deve refletir status removido no record de lista"
    )
    void deveRefletirStatusRemovidoNoRecordDeLista() {
        OrganizacaoModel organizacao =
                criarOrganizacao(
                        100L,
                        "Organização Principal"
                );

        UsuarioModel usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com"
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        4L,
                        usuario,
                        organizacao
                );

        PerfilModel perfil =
                criarPerfil(
                        2L,
                        organizacao,
                        "Financeiro"
                );

        UsuarioPerfilModel usuarioPerfil =
                criarUsuarioPerfil(
                        3L,
                        usuarioOrganizacao,
                        perfil
                );

        usuarioPerfil.remover(10L);

        ListaUsuarioPerfilRecord lista =
                new ListaUsuarioPerfilRecord(
                        usuarioPerfil
                );

        assertThat(lista.status())
                .isEqualTo(StatusEnum.REMOVIDO);
    }

    private OrganizacaoModel criarOrganizacao(
            Long id,
            String nome
    ) {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(nome);

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                id
        );

        return organizacao;
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email
    ) {
        UsuarioModel usuario =
                new UsuarioModel(
                        new UsuarioRecord(
                                email,
                                "123456"
                        ),
                        "senha-criptografada"
                );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                id
        );

        return usuario;
    }

    private UsuarioOrganizacaoModel criarUsuarioOrganizacao(
            Long id,
            UsuarioModel usuario,
            OrganizacaoModel organizacao
    ) {
        UsuarioOrganizacaoModel usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        ReflectionTestUtils.setField(
                usuarioOrganizacao,
                "id",
                id
        );

        return usuarioOrganizacao;
    }

    private PerfilModel criarPerfil(
            Long id,
            OrganizacaoModel organizacao,
            String nome
    ) {
        PerfilModel perfil =
                new PerfilModel(
                        organizacao,
                        new PerfilRecord(
                                nome,
                                "Perfil " + nome
                        )
                );

        ReflectionTestUtils.setField(
                perfil,
                "id",
                id
        );

        return perfil;
    }

    private UsuarioPerfilModel criarUsuarioPerfil(
            Long id,
            UsuarioOrganizacaoModel usuarioOrganizacao,
            PerfilModel perfil
    ) {
        UsuarioPerfilModel usuarioPerfil =
                new UsuarioPerfilModel(
                        usuarioOrganizacao,
                        perfil
                );

        ReflectionTestUtils.setField(
                usuarioPerfil,
                "id",
                id
        );

        return usuarioPerfil;
    }
}