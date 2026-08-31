package com.empresa.erp.domain.acesso.permissao.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;

class PermissaoResponseRecordsTest {

    @Test
    @DisplayName(
            "Deve criar DetalhePermissaoRecord a partir do model"
    )
    void deveCriarDetalhePermissaoRecordAPartirDoModel() {
        PermissaoModel permissao =
                criarPermissao(
                        1L,
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        StatusEnum.ATIVO
                );

        DetalhePermissaoRecord detalhe =
                new DetalhePermissaoRecord(
                        permissao
                );

        assertThat(detalhe.id())
                .isEqualTo(1L);

        assertThat(detalhe.nome())
                .isEqualTo("Listar usuários");

        assertThat(detalhe.chave())
                .isEqualTo(
                        "ACESSO_USUARIO_LISTAR"
                );

        assertThat(detalhe.descricao())
                .isEqualTo(
                        "Permite listar usuários"
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
        
        assertThat(detalhe.auditoria())
        		.isNotNull();
    }

    @Test
    @DisplayName(
            "Deve criar ListaPermissaoRecord a partir do model"
    )
    void deveCriarListaPermissaoRecordAPartirDoModel() {
        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Excluir usuários",
                        "ACESSO_USUARIO_EXCLUIR",
                        "Permite excluir usuários",
                        StatusEnum.ATIVO
                );

        ListaPermissaoRecord lista =
                new ListaPermissaoRecord(
                        permissao
                );

        assertThat(lista.id())
                .isEqualTo(2L);

        assertThat(lista.nome())
                .isEqualTo("Excluir usuários");

        assertThat(lista.chave())
                .isEqualTo(
                        "ACESSO_USUARIO_EXCLUIR"
                );

        assertThat(lista.descricao())
                .isEqualTo(
                        "Permite excluir usuários"
                );

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve refletir status inativo no record de detalhe"
    )
    void deveRefletirStatusInativoNoRecordDeDetalhe() {
        PermissaoModel permissao =
                criarPermissao(
                        1L,
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        StatusEnum.INATIVO
                );

        DetalhePermissaoRecord detalhe =
                new DetalhePermissaoRecord(
                        permissao
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private PermissaoModel criarPermissao(
            Long id,
            String nome,
            String chave,
            String descricao,
            StatusEnum status
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
                descricao
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
                status
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