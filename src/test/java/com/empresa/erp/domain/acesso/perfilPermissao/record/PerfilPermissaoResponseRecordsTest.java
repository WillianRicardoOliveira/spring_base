package com.empresa.erp.domain.acesso.perfilPermissao.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class PerfilPermissaoResponseRecordsTest {

    @Test
    @DisplayName(
            "Deve criar DetalhePerfilPermissaoRecord a partir do modelo"
    )
    void deveCriarDetalhePerfilPermissaoRecordAPartirDoModelo() {
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
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        DetalhePerfilPermissaoRecord detalhe =
                new DetalhePerfilPermissaoRecord(
                        perfilPermissao
                );

        assertThat(detalhe.id())
                .isEqualTo(3L);

        assertThat(detalhe.idPerfil())
                .isEqualTo(1L);

        assertThat(detalhe.perfil())
                .isEqualTo("Administrador");

        assertThat(detalhe.idPermissao())
                .isEqualTo(2L);

        assertThat(detalhe.permissao())
                .isEqualTo("Listar usuários");

        assertThat(detalhe.chave())
                .isEqualTo("ACESSO_USUARIO_LISTAR");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve criar ListaPerfilPermissaoRecord a partir do modelo"
    )
    void deveCriarListaPerfilPermissaoRecordAPartirDoModelo() {
        PerfilModel perfil =
                criarPerfil(
                        1L,
                        "Administrador"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Excluir usuários",
                        "ACESSO_USUARIO_EXCLUIR"
                );

        PerfilPermissaoModel perfilPermissao =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        ListaPerfilPermissaoRecord lista =
                new ListaPerfilPermissaoRecord(
                        perfilPermissao
                );

        assertThat(lista.id())
                .isEqualTo(3L);

        assertThat(lista.idPermissao())
                .isEqualTo(2L);

        assertThat(lista.permissao())
                .isEqualTo("Excluir usuários");

        assertThat(lista.chave())
                .isEqualTo("ACESSO_USUARIO_EXCLUIR");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve refletir status removido no record de detalhe"
    )
    void deveRefletirStatusRemovidoNoRecordDeDetalhe() {
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
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        perfilPermissao.remover(10L);

        DetalhePerfilPermissaoRecord detalhe =
                new DetalhePerfilPermissaoRecord(
                        perfilPermissao
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.REMOVIDO);
    }

    @Test
    @DisplayName(
            "Deve refletir status removido no record de lista"
    )
    void deveRefletirStatusRemovidoNoRecordDeLista() {
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
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        perfilPermissao.remover(10L);

        ListaPerfilPermissaoRecord lista =
                new ListaPerfilPermissaoRecord(
                        perfilPermissao
                );

        assertThat(lista.status())
                .isEqualTo(StatusEnum.REMOVIDO);
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

    private PerfilPermissaoModel criarPerfilPermissao(
            Long id,
            PerfilModel perfil,
            PermissaoModel permissao
    ) {
        PerfilPermissaoModel perfilPermissao =
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                );

        ReflectionTestUtils.setField(
                perfilPermissao,
                "id",
                id
        );

        return perfilPermissao;
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