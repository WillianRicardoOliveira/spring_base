package com.empresa.erp.domain.acesso.perfil.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class PerfilModelTest {

    @Test
    @DisplayName(
            "Deve criar perfil comum ativo vinculado à organização"
    )
    void deveCriarPerfilComumAtivoVinculadoAOrganizacao() {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        PerfilRecord dados =
                new PerfilRecord(
                        "Financeiro",
                        "Perfil do setor financeiro"
                );

        PerfilModel perfil =
                new PerfilModel(
                        organizacao,
                        dados
                );

        assertThat(perfil.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(perfil.getNome())
                .isEqualTo("Financeiro");

        assertThat(perfil.getDescricao())
                .isEqualTo(
                        "Perfil do setor financeiro"
                );

        assertThat(perfil.getTipoSistema())
                .isNull();

        assertThat(perfil.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(perfil.isSistema())
                .isFalse();

        assertThat(perfil.isAdministradorSistema())
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve criar perfil administrador do sistema"
    )
    void deveCriarPerfilAdministradorDoSistema() {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        PerfilModel perfil =
                PerfilModel
                        .criarAdministradorSistema(
                                organizacao
                        );

        assertThat(perfil.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(perfil.getNome())
                .isEqualTo("Administrador");

        assertThat(perfil.getDescricao())
                .isEqualTo(
                        "Perfil com acesso total a organizacao"
                );

        assertThat(perfil.getTipoSistema())
                .isEqualTo(
                        TipoPerfilSistemaEnum.ADMINISTRADOR
                );

        assertThat(perfil.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(perfil.isSistema())
                .isTrue();

        assertThat(perfil.isAdministradorSistema())
                .isTrue();
    }

    @Test
    @DisplayName(
            "Deve atualizar perfil"
    )
    void deveAtualizarPerfil() {
        PerfilModel perfil =
                criarPerfil();

        perfil.atualizar(
                new AtualizaPerfilRecord(
                        1L,
                        "Financeiro Master",
                        "Perfil atualizado"
                )
        );

        assertThat(perfil.getNome())
                .isEqualTo(
                        "Financeiro Master"
                );

        assertThat(perfil.getDescricao())
                .isEqualTo(
                        "Perfil atualizado"
                );
    }

    @Test
    @DisplayName(
            "Deve inativar perfil"
    )
    void deveInativarPerfil() {
        PerfilModel perfil =
                criarPerfil();

        perfil.inativar();

        assertThat(perfil.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName(
            "Deve remover perfil registrando auditoria"
    )
    void deveRemoverPerfilRegistrandoAuditoria() {
        PerfilModel perfil =
                criarPerfil();

        perfil.remover(10L);

        assertThat(perfil.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(perfil.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(perfil.getRemovidoEm())
                .isNotNull();
    }

    private PerfilModel criarPerfil() {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        return new PerfilModel(
                organizacao,
                new PerfilRecord(
                        "Financeiro",
                        "Perfil do setor financeiro"
                )
        );
    }
}