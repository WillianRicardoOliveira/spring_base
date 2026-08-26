package com.empresa.erp.domain.acesso.permissao.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PermissaoRepositoryTest {

    @Autowired
    private PermissaoRepository repository;

    @Test
    @DisplayName(
            "Deve listar somente permissões ativas do escopo informado"
    )
    void deveListarSomentePermissoesAtivasDoEscopoInformado() {
        var permissaoOrganizacaoAtiva =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var permissaoOrganizacaoInativa =
                salvarPermissao(
                        "Excluir usuários",
                        "ACESSO_USUARIO_EXCLUIR",
                        "Permite excluir usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.INATIVO
                );

        var permissaoPlataformaAtiva =
                salvarPermissao(
                        "Listar organizações",
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        "Permite listar organizações",
                        true,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );

        var resultado =
                repository.findAllByEscopoAndStatus(
                        PageRequest.of(0, 10),
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(PermissaoModel::getId)
                .containsExactly(
                        permissaoOrganizacaoAtiva.getId()
                )
                .doesNotContain(
                        permissaoOrganizacaoInativa.getId(),
                        permissaoPlataformaAtiva.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar permissões por nome ignorando caixa, escopo e status"
    )
    void deveFiltrarPermissoesPorNomeIgnorandoCaixaEscopoEStatus() {
        var permissaoEsperada =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        salvarPermissao(
                "Editar perfis",
                "ACESSO_PERFIL_EDITAR",
                "Permite editar perfis",
                true,
                EscopoPermissaoEnum.ORGANIZACAO,
                StatusEnum.ATIVO
        );

        salvarPermissao(
                "Listar organizações",
                "PLATAFORMA_ORGANIZACAO_LISTAR",
                "Permite listar organizações",
                true,
                EscopoPermissaoEnum.PLATAFORMA,
                StatusEnum.ATIVO
        );

        salvarPermissao(
                "Listar acessos removidos",
                "ACESSO_REMOVIDO_LISTAR",
                "Permite listar acessos removidos",
                true,
                EscopoPermissaoEnum.ORGANIZACAO,
                StatusEnum.REMOVIDO
        );

        var resultado =
                repository
                        .findByNomeContainingIgnoreCaseAndEscopoAndStatus(
                                PageRequest.of(0, 10),
                                "LISTAR",
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent())
                .extracting(PermissaoModel::getId)
                .containsExactly(
                        permissaoEsperada.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve listar permissões do sistema por escopo e status ordenadas por id"
    )
    void deveListarPermissoesDoSistemaPorEscopoEStatusOrdenadasPorId() {
        var primeiraPermissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var segundaPermissao =
                salvarPermissao(
                        "Editar usuários",
                        "ACESSO_USUARIO_EDITAR",
                        "Permite editar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var permissaoNaoControladaPeloSistema =
                salvarPermissao(
                        "Consultar relatório personalizado",
                        "RELATORIO_PERSONALIZADO_CONSULTAR",
                        "Permite consultar relatório personalizado",
                        false,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var permissaoDaPlataforma =
                salvarPermissao(
                        "Listar organizações",
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        "Permite listar organizações",
                        true,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );

        var permissaoInativa =
                salvarPermissao(
                        "Excluir usuários",
                        "ACESSO_USUARIO_EXCLUIR",
                        "Permite excluir usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.INATIVO
                );

        var resultado =
                repository
                        .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .extracting(PermissaoModel::getId)
                .containsExactly(
                        primeiraPermissao.getId(),
                        segundaPermissao.getId()
                )
                .doesNotContain(
                        permissaoNaoControladaPeloSistema.getId(),
                        permissaoDaPlataforma.getId(),
                        permissaoInativa.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve buscar permissão por id, escopo e status"
    )
    void deveBuscarPermissaoPorIdEscopoEStatus() {
        var permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var resultado =
                repository.findByIdAndEscopoAndStatus(
                        permissao.getId(),
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(permissao.getId());

        assertThat(resultado.get().getNome())
                .isEqualTo("Listar usuários");

        assertThat(resultado.get().getEscopo())
                .isEqualTo(EscopoPermissaoEnum.ORGANIZACAO);

        assertThat(resultado.get().getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Não deve buscar permissão quando o escopo for diferente"
    )
    void naoDeveBuscarPermissaoQuandoEscopoForDiferente() {
        var permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var resultado =
                repository.findByIdAndEscopoAndStatus(
                        permissao.getId(),
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve buscar permissão quando o status for diferente"
    )
    void naoDeveBuscarPermissaoQuandoStatusForDiferente() {
        var permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        "Permite listar usuários",
                        true,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var resultado =
                repository.findByIdAndEscopoAndStatus(
                        permissao.getId(),
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.REMOVIDO
                );

        assertThat(resultado)
                .isEmpty();
    }

    private PermissaoModel salvarPermissao(
            String nome,
            String chave,
            String descricao,
            Boolean sistema,
            EscopoPermissaoEnum escopo,
            StatusEnum status
    ) {
        PermissaoModel permissao =
                instanciarPermissao();

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
                sistema
        );

        ReflectionTestUtils.setField(
                permissao,
                "escopo",
                escopo
        );

        ReflectionTestUtils.setField(
                permissao,
                "status",
                status
        );

        return repository.save(permissao);
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