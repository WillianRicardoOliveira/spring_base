package com.empresa.erp.domain.acesso.administrador.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.model.TipoPerfilSistemaEnum;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

@ExtendWith(MockitoExtension.class)
class ProtecaoAdministradorOrganizacaoServiceTest {

    private static final Long ID_ORGANIZACAO =
            10L;

    private static final Long ID_USUARIO_ORGANIZACAO =
            20L;

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private UsuarioPerfilRepository
            usuarioPerfilRepository;

    private ProtecaoAdministradorOrganizacaoService service;

    @BeforeEach
    void setUp() {
        service =
                new ProtecaoAdministradorOrganizacaoService(
                        organizacaoRepository,
                        usuarioPerfilRepository
                );
    }

    @Test
    @DisplayName(
            "Deve permitir inativação de usuário que não é administrador"
    )
    void devePermitirInativacaoDeUsuarioQueNaoEAdministrador() {
        var organizacao =
                criarOrganizacao();

        var vinculo =
                criarVinculo(organizacao);

        when(organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(organizacao)
        );

        when(usuarioPerfilRepository
                .possuiPerfilAdministradorAtivo(
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        assertThatCode(
                () -> service.validarInativacaoUsuario(
                        vinculo,
                        ID_ORGANIZACAO
                )
        ).doesNotThrowAnyException();

        verify(organizacaoRepository)
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        verify(usuarioPerfilRepository)
                .possuiPerfilAdministradorAtivo(
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                );

        verify(usuarioPerfilRepository, never())
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve permitir inativação quando existe outro administrador"
    )
    void devePermitirInativacaoQuandoExisteOutroAdministrador() {
        var organizacao =
                criarOrganizacao();

        var vinculo =
                criarVinculo(organizacao);

        when(organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(organizacao)
        );

        when(usuarioPerfilRepository
                .possuiPerfilAdministradorAtivo(
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        when(usuarioPerfilRepository
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatCode(
                () -> service.validarInativacaoUsuario(
                        vinculo,
                        ID_ORGANIZACAO
                )
        ).doesNotThrowAnyException();

        verify(usuarioPerfilRepository)
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve inativar o último administrador ativo"
    )
    void naoDeveInativarOUltimoAdministradorAtivo() {
        var organizacao =
                criarOrganizacao();

        var vinculo =
                criarVinculo(organizacao);

        when(organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(organizacao)
        );

        when(usuarioPerfilRepository
                .possuiPerfilAdministradorAtivo(
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        when(usuarioPerfilRepository
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        assertThatThrownBy(
                () -> service.validarInativacaoUsuario(
                        vinculo,
                        ID_ORGANIZACAO
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "O ultimo administrador ativo da organizacao "
                                + "nao pode ser removido."
                );
    }

    @Test
    @DisplayName(
            "Não deve validar inativação de organização inexistente ou inativa"
    )
    void naoDeveValidarInativacaoDeOrganizacaoInexistenteOuInativa() {
        var organizacao =
                criarOrganizacao();

        var vinculo =
                criarVinculo(organizacao);

        when(organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.validarInativacaoUsuario(
                        vinculo,
                        ID_ORGANIZACAO
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Organizacao nao encontrada ou inativa."
                );

        verifyNoInteractions(
                usuarioPerfilRepository
        );
    }

    @Test
    @DisplayName(
            "Deve ignorar remoção de perfil que não é administrador"
    )
    void deveIgnorarRemocaoDePerfilQueNaoEAdministrador() {
        var perfil =
                org.mockito.Mockito.mock(
                        PerfilModel.class
                );

        when(perfil.isAdministradorSistema())
                .thenReturn(false);

        var usuarioPerfil =
                new UsuarioPerfilModel(
                        null,
                        perfil
                );

        assertThatCode(
                () -> service.validarRemocaoPerfil(
                        usuarioPerfil,
                        ID_ORGANIZACAO
                )
        ).doesNotThrowAnyException();

        verifyNoInteractions(
                organizacaoRepository,
                usuarioPerfilRepository
        );
    }

    @Test
    @DisplayName(
            "Deve permitir remoção do administrador quando existe outro"
    )
    void devePermitirRemocaoDoAdministradorQuandoExisteOutro() {
        var organizacao =
                criarOrganizacao();

        var vinculo =
                criarVinculo(organizacao);

        var perfilAdministrador =
                PerfilModel
                        .criarAdministradorSistema(
                                organizacao
                        );

        var usuarioPerfil =
                new UsuarioPerfilModel(
                        vinculo,
                        perfilAdministrador
                );

        when(organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(organizacao)
        );

        when(usuarioPerfilRepository
                .possuiPerfilAdministradorAtivo(
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        when(usuarioPerfilRepository
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatCode(
                () -> service.validarRemocaoPerfil(
                        usuarioPerfil,
                        ID_ORGANIZACAO
                )
        ).doesNotThrowAnyException();

        verify(organizacaoRepository)
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        verify(usuarioPerfilRepository)
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve remover o último perfil administrador ativo"
    )
    void naoDeveRemoverOUltimoPerfilAdministradorAtivo() {
        var organizacao =
                criarOrganizacao();

        var vinculo =
                criarVinculo(organizacao);

        var perfilAdministrador =
                PerfilModel
                        .criarAdministradorSistema(
                                organizacao
                        );

        var usuarioPerfil =
                new UsuarioPerfilModel(
                        vinculo,
                        perfilAdministrador
                );

        when(organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(organizacao)
        );

        when(usuarioPerfilRepository
                .possuiPerfilAdministradorAtivo(
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        when(usuarioPerfilRepository
                .existeOutroAdministradorAtivo(
                        ID_ORGANIZACAO,
                        ID_USUARIO_ORGANIZACAO,
                        TipoPerfilSistemaEnum.ADMINISTRADOR,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        assertThatThrownBy(
                () -> service.validarRemocaoPerfil(
                        usuarioPerfil,
                        ID_ORGANIZACAO
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "O ultimo administrador ativo da organizacao "
                                + "nao pode ser removido."
                );
    }

    private OrganizacaoModel criarOrganizacao() {
        var organizacao =
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

    private UsuarioOrganizacaoModel criarVinculo(
            OrganizacaoModel organizacao
    ) {
        var usuario =
                new UsuarioModel(
                        30L,
                        "admin@teste.com",
                        "senha-criptografada",
                        StatusEnum.ATIVO
                );

        var vinculo =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        ReflectionTestUtils.setField(
                vinculo,
                "id",
                ID_USUARIO_ORGANIZACAO
        );

        return vinculo;
    }
}