package com.empresa.erp.domain.acesso.perfilPermissao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

@ExtendWith(MockitoExtension.class)
class PerfilPermissaoServiceTest {

    private static final Long ID_ORGANIZACAO = 100L;

    @Mock
    private PerfilPermissaoRepository repository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @InjectMocks
    private PerfilPermissaoService service;

    @Test
    @DisplayName(
            "Deve vincular permissão ao perfil da organização"
    )
    void deveVincularPermissaoAoPerfilDaOrganizacao() {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository
                        .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                permissaoRepository.findByIdAndEscopoAndStatus(
                        2L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(permissao)
        );

        when(
                repository
                        .findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                                1L,
                                2L,
                                ID_ORGANIZACAO
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(repository.save(any(PerfilPermissaoModel.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        PerfilPermissaoModel resultado =
                service.cadastrar(dados);

        assertThat(resultado.getPerfil())
                .isSameAs(perfil);

        assertThat(resultado.getPermissao())
                .isSameAs(permissao);

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository)
                .save(resultado);
    }

    @Test
    @DisplayName(
            "Deve bloquear vínculo duplicado ativo"
    )
    void deveBloquearVinculoDuplicadoAtivo() {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        PerfilPermissaoModel vinculoExistente =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository
                        .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                permissaoRepository.findByIdAndEscopoAndStatus(
                        2L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(permissao)
        );

        when(
                repository
                        .findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                                1L,
                                2L,
                                ID_ORGANIZACAO
                        )
        ).thenReturn(
                Optional.of(vinculoExistente)
        );

        assertThatThrownBy(
                () -> service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Permissao ja vinculada ao perfil."
                );

        verify(repository, never())
                .save(any(PerfilPermissaoModel.class));
    }

    @Test
    @DisplayName(
            "Deve reativar vínculo removido"
    )
    void deveReativarVinculoRemovido() {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        PerfilPermissaoModel vinculoRemovido =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        vinculoRemovido.remover(10L);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository
                        .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                permissaoRepository.findByIdAndEscopoAndStatus(
                        2L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(permissao)
        );

        when(
                repository
                        .findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                                1L,
                                2L,
                                ID_ORGANIZACAO
                        )
        ).thenReturn(
                Optional.of(vinculoRemovido)
        );

        when(repository.save(vinculoRemovido))
                .thenReturn(vinculoRemovido);

        PerfilPermissaoModel resultado =
                service.cadastrar(dados);

        assertThat(resultado)
                .isSameAs(vinculoRemovido);

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(resultado.getRemovidoPor())
                .isNull();

        assertThat(resultado.getRemovidoEm())
                .isNull();

        verify(repository)
                .save(vinculoRemovido);
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro quando perfil não pertence à organização"
    )
    void deveBloquearCadastroQuandoPerfilNaoPertenceAOrganizacao() {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository
                        .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil nao encontrado ou removido."
                );

        verify(permissaoRepository, never())
                .findByIdAndEscopoAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(PerfilPermissaoModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear alteração de perfil crítico do sistema"
    )
    void deveBloquearAlteracaoDePerfilCriticoDoSistema() {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        PerfilModel perfilAdministrador =
                criarPerfilAdministrador(1L);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository
                        .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfilAdministrador)
        );

        assertThatThrownBy(
                () -> service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Permissoes de perfil critico do sistema "
                                + "nao podem ser alteradas."
                );

        verify(permissaoRepository, never())
                .findByIdAndEscopoAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(PerfilPermissaoModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro quando permissão não existe no escopo da organização"
    )
    void deveBloquearCadastroQuandoPermissaoNaoExisteNoEscopoDaOrganizacao() {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository
                        .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                permissaoRepository.findByIdAndEscopoAndStatus(
                        2L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Permissao nao encontrada."
                );

        verify(repository, never())
                .save(any(PerfilPermissaoModel.class));
    }

    @Test
    @DisplayName(
            "Deve listar permissões ativas vinculadas ao perfil da organização"
    )
    void deveListarPermissoesAtivasVinculadasAoPerfilDaOrganizacao() {
        var paginacao =
                PageRequest.of(0, 10);

        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository.findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                repository
                        .findAllByPerfilIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                paginacao,
                                1L,
                                ID_ORGANIZACAO,
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                new PageImpl<>(
                        List.of(perfilPermissao)
                )
        );

        var resultado =
                service.listarPorPerfil(
                        paginacao,
                        1L
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(3L);

        assertThat(resultado.getContent().get(0).idPermissao())
                .isEqualTo(2L);

        assertThat(resultado.getContent().get(0).permissao())
                .isEqualTo("Listar perfis");

        assertThat(resultado.getContent().get(0).chave())
                .isEqualTo("ACESSO_PERFIL_LISTAR");

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear listagem quando perfil não pertence à organização"
    )
    void deveBloquearListagemQuandoPerfilNaoPertenceAOrganizacao() {
        var paginacao =
                PageRequest.of(0, 10);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                perfilRepository.findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.listarPorPerfil(
                        paginacao,
                        1L
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar vínculo ativo pertencente à organização"
    )
    void deveDetalharVinculoAtivoPertencenteAOrganizacao() {
        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                3L,
                                ID_ORGANIZACAO,
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfilPermissao)
        );

        var resultado =
                service.detalhar(3L);

        assertThat(resultado.id())
                .isEqualTo(3L);

        assertThat(resultado.idPerfil())
                .isEqualTo(1L);

        assertThat(resultado.perfil())
                .isEqualTo("Financeiro");

        assertThat(resultado.idPermissao())
                .isEqualTo(2L);

        assertThat(resultado.permissao())
                .isEqualTo("Listar perfis");

        assertThat(resultado.chave())
                .isEqualTo("ACESSO_PERFIL_LISTAR");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear detalhamento de vínculo ausente na organização"
    )
    void deveBloquearDetalhamentoDeVinculoAusenteNaOrganizacao() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                3L,
                                ID_ORGANIZACAO,
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.detalhar(3L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo de permissao nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve remover vínculo pertencente à organização com auditoria"
    )
    void deveRemoverVinculoPertencenteAOrganizacaoComAuditoria() {
        PerfilModel perfil =
                criarPerfilComum(
                        1L,
                        "Financeiro"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                3L,
                                ID_ORGANIZACAO,
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfilPermissao)
        );

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(3L);

        assertThat(perfilPermissao.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(perfilPermissao.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(perfilPermissao.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear remoção de vínculo de perfil crítico"
    )
    void deveBloquearRemocaoDeVinculoDePerfilCritico() {
        PerfilModel perfilAdministrador =
                criarPerfilAdministrador(1L);

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR"
                );

        PerfilPermissaoModel perfilPermissao =
                criarPerfilPermissao(
                        3L,
                        perfilAdministrador,
                        permissao
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                                3L,
                                ID_ORGANIZACAO,
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(perfilPermissao)
        );

        assertThatThrownBy(
                () -> service.excluir(3L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Permissoes de perfil critico do sistema "
                                + "nao podem ser alteradas."
                );

        verify(usuarioLogadoService, never())
                .getId();

        assertThat(perfilPermissao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    private PerfilModel criarPerfilComum(
            Long id,
            String nome
    ) {
        OrganizacaoModel organizacao =
                criarOrganizacao();

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

    private PerfilModel criarPerfilAdministrador(
            Long id
    ) {
        PerfilModel perfil =
                PerfilModel.criarAdministradorSistema(
                        criarOrganizacao()
                );

        ReflectionTestUtils.setField(
                perfil,
                "id",
                id
        );

        return perfil;
    }

    private OrganizacaoModel criarOrganizacao() {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                ID_ORGANIZACAO
        );

        return organizacao;
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