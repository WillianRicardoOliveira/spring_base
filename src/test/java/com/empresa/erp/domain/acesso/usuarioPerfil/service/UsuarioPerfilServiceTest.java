package com.empresa.erp.domain.acesso.usuarioPerfil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.administrador.service.ProtecaoAdministradorOrganizacaoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@ExtendWith(MockitoExtension.class)
class UsuarioPerfilServiceTest {

    private static final Long ID_ORGANIZACAO = 100L;

    @Mock
    private UsuarioPerfilRepository repository;

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @Mock
    private ProtecaoAdministradorOrganizacaoService
            protecaoAdministradorOrganizacaoService;

    @InjectMocks
    private UsuarioPerfilService service;

    @Test
    @DisplayName(
            "Deve vincular perfil ao usuário ativo da organização"
    )
    void deveVincularPerfilAoUsuarioAtivoDaOrganizacao() {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        1L,
                        2L
                );

        OrganizacaoModel organizacao =
                criarOrganizacao();

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

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioOrganizacao)
        );

        when(
                perfilRepository.findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                repository
                        .existsByUsuarioOrganizacaoIdAndPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                3L,
                                2L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(false);

        when(repository.save(any(UsuarioPerfilModel.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        UsuarioPerfilModel resultado =
                service.cadastrar(dados);

        assertThat(resultado.getUsuarioOrganizacao())
                .isSameAs(usuarioOrganizacao);

        assertThat(resultado.getPerfil())
                .isSameAs(perfil);

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository)
                .save(resultado);
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro quando usuário não estiver ativo na organização"
    )
    void deveBloquearCadastroQuandoUsuarioNaoEstiverAtivoNaOrganizacao() {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        1L,
                        2L
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO,
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
                        "Usuario nao encontrado na organizacao."
                );

        verify(perfilRepository, never())
                .findByIdAndOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(UsuarioPerfilModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro quando perfil não pertencer à organização"
    )
    void deveBloquearCadastroQuandoPerfilNaoPertencerAOrganizacao() {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        1L,
                        2L
                );

        OrganizacaoModel organizacao =
                criarOrganizacao();

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

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioOrganizacao)
        );

        when(
                perfilRepository.findByIdAndOrganizacaoIdAndStatus(
                        2L,
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
                        "Perfil nao encontrado."
                );

        verify(repository, never())
                .save(any(UsuarioPerfilModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear vínculo ativo duplicado"
    )
    void deveBloquearVinculoAtivoDuplicado() {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        1L,
                        2L
                );

        OrganizacaoModel organizacao =
                criarOrganizacao();

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

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioOrganizacao)
        );

        when(
                perfilRepository.findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(perfil)
        );

        when(
                repository
                        .existsByUsuarioOrganizacaoIdAndPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                3L,
                                2L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(true);

        assertThatThrownBy(
                () -> service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil ja vinculado ao usuario."
                );

        verify(repository, never())
                .save(any(UsuarioPerfilModel.class));
    }

    @Test
    @DisplayName(
            "Deve listar perfis ativos do usuário na organização"
    )
    void deveListarPerfisAtivosDoUsuarioNaOrganizacao() {
        OrganizacaoModel organizacao =
                criarOrganizacao();

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
                criarUsuarioPerfil(
                        4L,
                        usuarioOrganizacao,
                        perfil
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioOrganizacao)
        );

        when(
                repository
                        .findAllByUsuarioOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                3L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                List.of(usuarioPerfil)
        );

        var resultado =
                service.listarPorUsuario(1L);

        assertThat(resultado)
                .hasSize(1);

        assertThat(resultado.get(0).id())
                .isEqualTo(4L);

        assertThat(resultado.get(0).idPerfil())
                .isEqualTo(2L);

        assertThat(resultado.get(0).perfil())
                .isEqualTo("Financeiro");

        assertThat(resultado.get(0).status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear listagem quando usuário não estiver ativo na organização"
    )
    void deveBloquearListagemQuandoUsuarioNaoEstiverAtivoNaOrganizacao() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                usuarioOrganizacaoRepository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                1L,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.listarPorUsuario(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario nao encontrado na organizacao."
                );

        verify(repository, never())
                .findAllByUsuarioOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar vínculo ativo pertencente à organização"
    )
    void deveDetalharVinculoAtivoPertencenteAOrganizacao() {
        OrganizacaoModel organizacao =
                criarOrganizacao();

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
                criarUsuarioPerfil(
                        4L,
                        usuarioOrganizacao,
                        perfil
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                4L,
                                ID_ORGANIZACAO,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioPerfil)
        );

        var resultado =
                service.detalhar(4L);

        assertThat(resultado.id())
                .isEqualTo(4L);

        assertThat(resultado.idUsuario())
                .isEqualTo(1L);

        assertThat(resultado.usuario())
                .isEqualTo("usuario@teste.com");

        assertThat(resultado.idPerfil())
                .isEqualTo(2L);

        assertThat(resultado.perfil())
                .isEqualTo("Financeiro");

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
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                4L,
                                ID_ORGANIZACAO,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.detalhar(4L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo de perfil nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve remover vínculo pertencente à organização com auditoria"
    )
    void deveRemoverVinculoPertencenteAOrganizacaoComAuditoria() {
        OrganizacaoModel organizacao =
                criarOrganizacao();

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
                criarUsuarioPerfil(
                        4L,
                        usuarioOrganizacao,
                        perfil
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                4L,
                                ID_ORGANIZACAO,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioPerfil)
        );

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(4L);

        verify(protecaoAdministradorOrganizacaoService)
                .validarRemocaoPerfil(
                        usuarioPerfil,
                        ID_ORGANIZACAO
                );

        assertThat(usuarioPerfil.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioPerfil.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioPerfil.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear remoção quando proteção do administrador rejeitar operação"
    )
    void deveBloquearRemocaoQuandoProtecaoDoAdministradorRejeitarOperacao() {
        OrganizacaoModel organizacao =
                criarOrganizacao();

        UsuarioModel usuario =
                criarUsuario(
                        1L,
                        "administrador@teste.com"
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        3L,
                        usuario,
                        organizacao
                );

        PerfilModel perfil =
                criarPerfilAdministrador(
                        2L,
                        organizacao
                );

        UsuarioPerfilModel usuarioPerfil =
                criarUsuarioPerfil(
                        4L,
                        usuarioOrganizacao,
                        perfil
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(
                repository
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                4L,
                                ID_ORGANIZACAO,
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                Optional.of(usuarioPerfil)
        );

        org.mockito.Mockito.doThrow(
                new ValidacaoException(
                        "O ultimo administrador ativo da organizacao "
                                + "nao pode ser removido."
                )
        )
                .when(
                        protecaoAdministradorOrganizacaoService
                )
                .validarRemocaoPerfil(
                        usuarioPerfil,
                        ID_ORGANIZACAO
                );

        assertThatThrownBy(
                () -> service.excluir(4L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "O ultimo administrador ativo da organizacao "
                                + "nao pode ser removido."
                );

        verify(usuarioLogadoService, never())
                .getId();

        assertThat(usuarioPerfil.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
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

    private PerfilModel criarPerfilAdministrador(
            Long id,
            OrganizacaoModel organizacao
    ) {
        PerfilModel perfil =
                PerfilModel.criarAdministradorSistema(
                        organizacao
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