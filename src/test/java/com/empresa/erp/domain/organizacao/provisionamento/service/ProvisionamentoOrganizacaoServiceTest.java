package com.empresa.erp.domain.organizacao.provisionamento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.model.TipoPerfilSistemaEnum;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

@ExtendWith(MockitoExtension.class)
class ProvisionamentoOrganizacaoServiceTest {

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private PerfilPermissaoRepository
            perfilPermissaoRepository;

    @Mock
    private UsuarioPerfilRepository
            usuarioPerfilRepository;

    @Test
    @DisplayName(
            "Deve provisionar organização com administrador e permissões"
    )
    void deveProvisionarOrganizacaoComAdministradorEPermissoes() {
        var administrador =
                criarUsuario(
                        10L,
                        StatusEnum.ATIVO
                );

        var permissaoA =
                org.mockito.Mockito.mock(
                        PermissaoModel.class
                );

        var permissaoB =
                org.mockito.Mockito.mock(
                        PermissaoModel.class
                );

        when(permissaoRepository
                .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                List.of(
                        permissaoA,
                        permissaoB
                )
        );

        when(organizacaoRepository.save(
                any(OrganizacaoModel.class)
        )).thenAnswer(invocacao ->
                invocacao.getArgument(0)
        );

        when(usuarioOrganizacaoRepository.save(
                any(UsuarioOrganizacaoModel.class)
        )).thenAnswer(invocacao ->
                invocacao.getArgument(0)
        );

        when(perfilRepository.save(
                any(PerfilModel.class)
        )).thenAnswer(invocacao ->
                invocacao.getArgument(0)
        );

        var service =
                criarService();

        var resultado =
                service.provisionar(
                        "  Organização   Principal  ",
                        administrador
                );

        assertThat(resultado.getNome())
                .isEqualTo(
                        "Organização Principal"
                );

        assertThat(resultado.getStatus())
                .isEqualTo(
                        StatusEnum.ATIVO
                );

        var organizacaoCaptor =
                ArgumentCaptor.forClass(
                        OrganizacaoModel.class
                );

        verify(organizacaoRepository)
                .save(
                        organizacaoCaptor.capture()
                );

        var organizacao =
                organizacaoCaptor.getValue();

        assertThat(organizacao)
                .isSameAs(resultado);

        var vinculoCaptor =
                ArgumentCaptor.forClass(
                        UsuarioOrganizacaoModel.class
                );

        verify(usuarioOrganizacaoRepository)
                .save(
                        vinculoCaptor.capture()
                );

        var vinculoOrganizacao =
                vinculoCaptor.getValue();

        assertThat(
                vinculoOrganizacao.getUsuario()
        ).isSameAs(administrador);

        assertThat(
                vinculoOrganizacao.getOrganizacao()
        ).isSameAs(organizacao);

        assertThat(
                vinculoOrganizacao.getStatus()
        ).isEqualTo(StatusEnum.ATIVO);

        var perfilCaptor =
                ArgumentCaptor.forClass(
                        PerfilModel.class
                );

        verify(perfilRepository)
                .save(
                        perfilCaptor.capture()
                );

        var perfilAdministrador =
                perfilCaptor.getValue();

        assertThat(
                perfilAdministrador.getOrganizacao()
        ).isSameAs(organizacao);

        assertThat(
                perfilAdministrador.getNome()
        ).isEqualTo("Administrador");

        assertThat(
                perfilAdministrador.getDescricao()
        ).isEqualTo(
                "Perfil com acesso total a organizacao"
        );

        assertThat(
                perfilAdministrador.getTipoSistema()
        ).isEqualTo(
                TipoPerfilSistemaEnum.ADMINISTRADOR
        );

        assertThat(
                perfilAdministrador.getStatus()
        ).isEqualTo(StatusEnum.ATIVO);

        assertThat(
                perfilAdministrador.isSistema()
        ).isTrue();

        assertThat(
                perfilAdministrador
                        .isAdministradorSistema()
        ).isTrue();

        @SuppressWarnings("unchecked")
        var permissoesCaptor =
                (ArgumentCaptor<List<PerfilPermissaoModel>>)
                        (ArgumentCaptor<?>)
                                ArgumentCaptor.forClass(
                                        List.class
                                );

        verify(perfilPermissaoRepository)
                .saveAll(
                        permissoesCaptor.capture()
                );

        var vinculosPermissao =
                permissoesCaptor.getValue();

        assertThat(vinculosPermissao)
                .hasSize(2);

        assertThat(vinculosPermissao)
                .allSatisfy(vinculo -> {
                    assertThat(vinculo.getPerfil())
                            .isSameAs(
                                    perfilAdministrador
                            );

                    assertThat(vinculo.getStatus())
                            .isEqualTo(
                                    StatusEnum.ATIVO
                            );
                });

        assertThat(vinculosPermissao)
                .extracting(
                        PerfilPermissaoModel::getPermissao
                )
                .containsExactly(
                        permissaoA,
                        permissaoB
                );

        var usuarioPerfilCaptor =
                ArgumentCaptor.forClass(
                        UsuarioPerfilModel.class
                );

        verify(usuarioPerfilRepository)
                .save(
                        usuarioPerfilCaptor.capture()
                );

        var usuarioPerfil =
                usuarioPerfilCaptor.getValue();

        assertThat(
                usuarioPerfil.getUsuarioOrganizacao()
        ).isSameAs(vinculoOrganizacao);

        assertThat(usuarioPerfil.getPerfil())
                .isSameAs(perfilAdministrador);

        assertThat(usuarioPerfil.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(permissaoRepository)
                .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve provisionar sem permissões organizacionais"
    )
    void naoDeveProvisionarSemPermissoesOrganizacionais() {
        var administrador =
                criarUsuario(
                        10L,
                        StatusEnum.ATIVO
                );

        when(permissaoRepository
                .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(List.of());

        var service =
                criarService();

        assertThatThrownBy(
                () -> service.provisionar(
                        "Organização Principal",
                        administrador
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Provisionamento da organizacao "
                                + "nao pode continuar: "
                                + "nenhuma permissao ativa "
                                + "do sistema foi encontrada."
                );

        verifyNoInteractions(
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                perfilRepository,
                perfilPermissaoRepository,
                usuarioPerfilRepository
        );
    }

    @Test
    @DisplayName(
            "Não deve provisionar organização sem nome"
    )
    void naoDeveProvisionarOrganizacaoSemNome() {
        var administrador =
                criarUsuario(
                        10L,
                        StatusEnum.ATIVO
                );

        var service =
                criarService();

        assertThatThrownBy(
                () -> service.provisionar(
                        "   ",
                        administrador
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Nome da organizacao obrigatorio."
                );

        verificarNenhumaPersistencia();
    }

    @Test
    @DisplayName(
            "Não deve provisionar sem administrador"
    )
    void naoDeveProvisionarSemAdministrador() {
        var service =
                criarService();

        assertThatThrownBy(
                () -> service.provisionar(
                        "Organização Principal",
                        null
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Administrador da organizacao invalido."
                );

        verificarNenhumaPersistencia();
    }

    @Test
    @DisplayName(
            "Não deve provisionar com administrador sem ID"
    )
    void naoDeveProvisionarComAdministradorSemId() {
        var administrador =
                criarUsuario(
                        null,
                        StatusEnum.ATIVO
                );

        var service =
                criarService();

        assertThatThrownBy(
                () -> service.provisionar(
                        "Organização Principal",
                        administrador
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Administrador da organizacao invalido."
                );

        verificarNenhumaPersistencia();
    }

    @Test
    @DisplayName(
            "Não deve provisionar com administrador inativo"
    )
    void naoDeveProvisionarComAdministradorInativo() {
        var administrador =
                criarUsuario(
                        10L,
                        StatusEnum.INATIVO
                );

        var service =
                criarService();

        assertThatThrownBy(
                () -> service.provisionar(
                        "Organização Principal",
                        administrador
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Administrador da organizacao invalido."
                );

        verificarNenhumaPersistencia();
    }

    @Test
    @DisplayName(
            "Não deve provisionar com administrador removido"
    )
    void naoDeveProvisionarComAdministradorRemovido() {
        var administrador =
                criarUsuario(
                        10L,
                        StatusEnum.REMOVIDO
                );

        var service =
                criarService();

        assertThatThrownBy(
                () -> service.provisionar(
                        "Organização Principal",
                        administrador
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Administrador da organizacao invalido."
                );

        verificarNenhumaPersistencia();
    }

    private ProvisionamentoOrganizacaoService
            criarService() {
        return new ProvisionamentoOrganizacaoService(
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                perfilRepository,
                permissaoRepository,
                perfilPermissaoRepository,
                usuarioPerfilRepository
        );
    }

    private UsuarioModel criarUsuario(
            Long id,
            StatusEnum status
    ) {
        return new UsuarioModel(
                id,
                "admin@teste.com",
                "senha-criptografada",
                status
        );
    }

    private void verificarNenhumaPersistencia() {
        verifyNoInteractions(
                permissaoRepository,
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                perfilRepository,
                perfilPermissaoRepository,
                usuarioPerfilRepository
        );
    }
}