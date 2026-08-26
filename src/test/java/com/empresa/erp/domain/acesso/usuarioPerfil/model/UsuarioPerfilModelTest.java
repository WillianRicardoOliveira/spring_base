package com.empresa.erp.domain.acesso.usuarioPerfil.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioPerfilModelTest {

    @Test
    @DisplayName(
            "Deve criar vínculo ativo entre usuário da organização e perfil"
    )
    void deveCriarVinculoAtivoEntreUsuarioDaOrganizacaoEPerfil() {
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
                        3L,
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
                new UsuarioPerfilModel(
                        usuarioOrganizacao,
                        perfil
                );

        assertThat(usuarioPerfil.getUsuarioOrganizacao())
                .isSameAs(usuarioOrganizacao);

        assertThat(usuarioPerfil.getUsuarioOrganizacao().getUsuario())
                .isSameAs(usuario);

        assertThat(usuarioPerfil.getUsuarioOrganizacao().getOrganizacao())
                .isSameAs(organizacao);

        assertThat(usuarioPerfil.getPerfil())
                .isSameAs(perfil);

        assertThat(usuarioPerfil.getPerfil().getOrganizacao())
                .isSameAs(organizacao);

        assertThat(usuarioPerfil.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(usuarioPerfil.getRemovidoPor())
                .isNull();

        assertThat(usuarioPerfil.getRemovidoEm())
                .isNull();
    }

    @Test
    @DisplayName(
            "Deve remover vínculo entre usuário da organização e perfil registrando auditoria"
    )
    void deveRemoverVinculoEntreUsuarioDaOrganizacaoEPerfilRegistrandoAuditoria() {
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
                        3L,
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
                new UsuarioPerfilModel(
                        usuarioOrganizacao,
                        perfil
                );

        usuarioPerfil.remover(10L);

        assertThat(usuarioPerfil.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioPerfil.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioPerfil.getRemovidoEm())
                .isNotNull();
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
}