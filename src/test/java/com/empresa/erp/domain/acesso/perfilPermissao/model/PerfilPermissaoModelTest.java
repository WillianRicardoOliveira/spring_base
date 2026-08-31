package com.empresa.erp.domain.acesso.perfilPermissao.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class PerfilPermissaoModelTest {

    @Test
    @DisplayName(
            "Deve criar vínculo ativo entre perfil e permissão"
    )
    void deveCriarVinculoAtivoEntrePerfilEPermissao() {
        PerfilModel perfil =
                criarPerfil(
                        1L,
                        "Administrador"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                );

        assertThat(perfilPermissao.getPerfil())
                .isSameAs(perfil);

        assertThat(perfilPermissao.getPermissao())
                .isSameAs(permissao);

        assertThat(perfilPermissao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(perfilPermissao.getRemovidoPor())
                .isNull();

        assertThat(perfilPermissao.getRemovidoEm())
                .isNull();
    }

    @Test
    @DisplayName(
            "Deve remover vínculo entre perfil e permissão registrando auditoria"
    )
    void deveRemoverVinculoEntrePerfilEPermissaoRegistrandoAuditoria() {
        PerfilModel perfil =
                criarPerfil(
                        1L,
                        "Administrador"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                );

        perfilPermissao.remover(10L);

        assertThat(perfilPermissao.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(perfilPermissao.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(perfilPermissao.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve reativar vínculo removido e limpar auditoria de remoção"
    )
    void deveReativarVinculoRemovidoELimparAuditoriaDeRemocao() {
        PerfilModel perfil =
                criarPerfil(
                        1L,
                        "Administrador"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                );

        perfilPermissao.remover(10L);
        perfilPermissao.reativar();

        assertThat(perfilPermissao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(perfilPermissao.getRemovidoPor())
                .isNull();

        assertThat(perfilPermissao.getRemovidoEm())
                .isNull();
    }

    private PerfilModel criarPerfil(
            Long id,
            String nome
    ) {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

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

    private PermissaoModel criarPermissao(
            Long id,
            String nome,
            String chave
    ) {
        PermissaoModel permissao =
                instanciarPermissao();

        ReflectionTestUtils.setField(
                permissao,
                "id",
                id
        );

        ReflectionTestUtils.setField(
                permissao,
                "nome",
                nome
        );

        ReflectionTestUtils.setField(
                permissao,
                "chave",
                chave
        );

        ReflectionTestUtils.setField(
                permissao,
                "descricao",
                "Permite " + nome
        );

        ReflectionTestUtils.setField(
                permissao,
                "sistema",
                true
        );

        ReflectionTestUtils.setField(
                permissao,
                "escopo",
                EscopoPermissaoEnum.ORGANIZACAO
        );

        ReflectionTestUtils.setField(
                permissao,
                "status",
                StatusEnum.ATIVO
        );

        return permissao;
    }

    private PermissaoModel instanciarPermissao() {
        try {
            var construtor =
                    PermissaoModel.class
                            .getDeclaredConstructor();

            construtor.setAccessible(true);

            return construtor.newInstance();
        } catch (
                InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException exception
        ) {
            throw new IllegalStateException(
                    "Não foi possível criar permissão para o teste.",
                    exception
            );
        }
    }
}