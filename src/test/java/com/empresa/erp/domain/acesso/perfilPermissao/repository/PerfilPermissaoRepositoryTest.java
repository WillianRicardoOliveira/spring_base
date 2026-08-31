package com.empresa.erp.domain.acesso.perfilPermissao.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PerfilPermissaoRepositoryTest {

    @Autowired
    private PerfilPermissaoRepository repository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Test
    @DisplayName(
            "Deve listar somente vínculos ativos com permissões ativas do escopo da organização"
    )
    void deveListarSomenteVinculosAtivosComPermissoesAtivasDoEscopoDaOrganizacao() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissaoAtiva =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PermissaoModel permissaoComVinculoRemovido =
                salvarPermissao(
                        "Editar usuários",
                        "ACESSO_USUARIO_EDITAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PermissaoModel permissaoInativa =
                salvarPermissao(
                        "Excluir usuários",
                        "ACESSO_USUARIO_EXCLUIR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.INATIVO
                );

        PermissaoModel permissaoPlataforma =
                salvarPermissao(
                        "Listar organizações",
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );

        PerfilPermissaoModel vinculoEsperado =
                salvarVinculo(
                        perfil,
                        permissaoAtiva
                );

        PerfilPermissaoModel vinculoRemovido =
                salvarVinculo(
                        perfil,
                        permissaoComVinculoRemovido
                );

        vinculoRemovido.remover(10L);
        repository.save(vinculoRemovido);

        PerfilPermissaoModel vinculoComPermissaoInativa =
                salvarVinculo(
                        perfil,
                        permissaoInativa
                );

        PerfilPermissaoModel vinculoComPermissaoPlataforma =
                salvarVinculo(
                        perfil,
                        permissaoPlataforma
                );

        var resultado =
                repository
                        .findAllByPerfilIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                PageRequest.of(0, 10),
                                perfil.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent())
                .extracting(PerfilPermissaoModel::getId)
                .containsExactly(
                        vinculoEsperado.getId()
                )
                .doesNotContain(
                        vinculoRemovido.getId(),
                        vinculoComPermissaoInativa.getId(),
                        vinculoComPermissaoPlataforma.getId()
                );
    }

    @Test
    @DisplayName(
            "Não deve listar vínculos de perfil pertencente a outra organização"
    )
    void naoDeveListarVinculosDePerfilPertencenteAOutraOrganizacao() {
        OrganizacaoModel primeiraOrganizacao =
                salvarOrganizacao(
                        "Primeira Organização"
                );

        OrganizacaoModel segundaOrganizacao =
                salvarOrganizacao(
                        "Segunda Organização"
                );

        PerfilModel perfil =
                salvarPerfil(
                        primeiraOrganizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        salvarVinculo(
                perfil,
                permissao
        );

        var resultado =
                repository
                        .findAllByPerfilIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                PageRequest.of(0, 10),
                                perfil.getId(),
                                segundaOrganizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve buscar vínculo por perfil, permissão e organização"
    )
    void deveBuscarVinculoPorPerfilPermissaoEOrganizacao() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PerfilPermissaoModel vinculo =
                salvarVinculo(
                        perfil,
                        permissao
                );

        var resultado =
                repository
                        .findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                                perfil.getId(),
                                permissao.getId(),
                                organizacao.getId()
                        );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(vinculo.getId());

        assertThat(resultado.get().getPerfil().getId())
                .isEqualTo(perfil.getId());

        assertThat(resultado.get().getPermissao().getId())
                .isEqualTo(permissao.getId());
    }

    @Test
    @DisplayName(
            "Não deve buscar vínculo por organização diferente"
    )
    void naoDeveBuscarVinculoPorOrganizacaoDiferente() {
        OrganizacaoModel primeiraOrganizacao =
                salvarOrganizacao(
                        "Primeira Organização"
                );

        OrganizacaoModel segundaOrganizacao =
                salvarOrganizacao(
                        "Segunda Organização"
                );

        PerfilModel perfil =
                salvarPerfil(
                        primeiraOrganizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        salvarVinculo(
                perfil,
                permissao
        );

        var resultado =
                repository
                        .findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                                perfil.getId(),
                                permissao.getId(),
                                segundaOrganizacao.getId()
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve verificar vínculo ativo do perfil na organização"
    )
    void deveVerificarVinculoAtivoDoPerfilNaOrganizacao() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        salvarVinculo(
                perfil,
                permissao
        );

        boolean existe =
                repository
                        .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                perfil.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        boolean existeRemovido =
                repository
                        .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                perfil.getId(),
                                organizacao.getId(),
                                StatusEnum.REMOVIDO
                        );

        assertThat(existe)
                .isTrue();

        assertThat(existeRemovido)
                .isFalse();
    }

    @Test
    @DisplayName(
            "Não deve identificar vínculo ativo em outra organização"
    )
    void naoDeveIdentificarVinculoAtivoEmOutraOrganizacao() {
        OrganizacaoModel primeiraOrganizacao =
                salvarOrganizacao(
                        "Primeira Organização"
                );

        OrganizacaoModel segundaOrganizacao =
                salvarOrganizacao(
                        "Segunda Organização"
                );

        PerfilModel perfil =
                salvarPerfil(
                        primeiraOrganizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        salvarVinculo(
                perfil,
                permissao
        );

        boolean existe =
                repository
                        .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                perfil.getId(),
                                segundaOrganizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(existe)
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve buscar vínculo por id com organização, escopo e status válidos"
    )
    void deveBuscarVinculoPorIdComOrganizacaoEscopoEStatusValidos() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PerfilPermissaoModel vinculo =
                salvarVinculo(
                        perfil,
                        permissao
                );

        var resultado =
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                vinculo.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(vinculo.getId());
    }

    @Test
    @DisplayName(
            "Não deve buscar vínculo por id quando a organização for diferente"
    )
    void naoDeveBuscarVinculoPorIdQuandoOrganizacaoForDiferente() {
        OrganizacaoModel primeiraOrganizacao =
                salvarOrganizacao(
                        "Primeira Organização"
                );

        OrganizacaoModel segundaOrganizacao =
                salvarOrganizacao(
                        "Segunda Organização"
                );

        PerfilModel perfil =
                salvarPerfil(
                        primeiraOrganizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PerfilPermissaoModel vinculo =
                salvarVinculo(
                        perfil,
                        permissao
                );

        var resultado =
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                vinculo.getId(),
                                segundaOrganizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve buscar vínculo por id quando o escopo for diferente"
    )
    void naoDeveBuscarVinculoPorIdQuandoEscopoForDiferente() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PerfilPermissaoModel vinculo =
                salvarVinculo(
                        perfil,
                        permissao
                );

        var resultado =
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                vinculo.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve buscar vínculo por id quando a permissão estiver inativa"
    )
    void naoDeveBuscarVinculoPorIdQuandoPermissaoEstiverInativa() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.INATIVO
                );

        PerfilPermissaoModel vinculo =
                salvarVinculo(
                        perfil,
                        permissao
                );

        var resultado =
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                vinculo.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve buscar vínculo por id quando o vínculo estiver removido"
    )
    void naoDeveBuscarVinculoPorIdQuandoVinculoEstiverRemovido() {
        OrganizacaoModel organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        PerfilModel perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        PermissaoModel permissao =
                salvarPermissao(
                        "Listar usuários",
                        "ACESSO_USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        PerfilPermissaoModel vinculo =
                salvarVinculo(
                        perfil,
                        permissao
                );

        vinculo.remover(10L);
        repository.save(vinculo);

        var resultado =
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                vinculo.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    private OrganizacaoModel salvarOrganizacao(
            String nome
    ) {
        return organizacaoRepository.save(
                new OrganizacaoModel(nome)
        );
    }

    private PerfilModel salvarPerfil(
            OrganizacaoModel organizacao,
            String nome
    ) {
        return perfilRepository.save(
                new PerfilModel(
                        organizacao,
                        new PerfilRecord(
                                nome,
                                "Perfil " + nome
                        )
                )
        );
    }

    private PermissaoModel salvarPermissao(
            String nome,
            String chave,
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
                escopo
        );

        ReflectionTestUtils.setField(
                permissao,
                "status",
                status
        );

        return permissaoRepository.save(
                permissao
        );
    }

    private PerfilPermissaoModel salvarVinculo(
            PerfilModel perfil,
            PermissaoModel permissao
    ) {
        return repository.save(
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                )
        );
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