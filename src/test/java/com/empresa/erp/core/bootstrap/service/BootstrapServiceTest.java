package com.empresa.erp.core.bootstrap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.core.bootstrap.config.BootstrapProperties;
import com.empresa.erp.core.bootstrap.validation.BootstrapPropertiesValidator;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.provisionamento.service.ProvisionamentoOrganizacaoService;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.plataforma.acesso.perfil.model.PerfilPlataformaModel;
import com.empresa.erp.domain.plataforma.acesso.perfil.model.TipoPerfilPlataformaSistemaEnum;
import com.empresa.erp.domain.plataforma.acesso.perfil.repository.PerfilPlataformaRepository;
import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.model.PerfilPlataformaPermissaoModel;
import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.repository.PerfilPlataformaPermissaoRepository;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.model.UsuarioPerfilPlataformaModel;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.repository.UsuarioPerfilPlataformaRepository;
import com.empresa.erp.domain.usuario.criacao.service.CriacaoUsuarioService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class BootstrapServiceTest {

    @Mock
    private BootstrapPropertiesValidator
            propertiesValidator;

    @Mock
    private ProvisionamentoOrganizacaoService
            provisionamentoOrganizacaoService;

    @Mock
    private CriacaoUsuarioService
            criacaoUsuarioService;

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

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

    @Mock
    private PerfilPlataformaRepository
            perfilPlataformaRepository;

    @Mock
    private PerfilPlataformaPermissaoRepository
            perfilPlataformaPermissaoRepository;

    @Mock
    private UsuarioPerfilPlataformaRepository
            usuarioPerfilPlataformaRepository;

    private BootstrapProperties properties;

    private BootstrapService service;

    @BeforeEach
    void setUp() {
        properties =
                criarProperties(
                        false,
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        criarService();
    }

    @Test
    @DisplayName(
            "Deve ignorar bootstrap desabilitado"
    )
    void deveIgnorarBootstrapDesabilitado() {
        boolean resultado =
                service.provisionar();

        assertThat(resultado)
                .isFalse();

        verifyNoInteractions(
                propertiesValidator,
                provisionamentoOrganizacaoService,
                criacaoUsuarioService,
                organizacaoRepository,
                usuarioRepository,
                usuarioOrganizacaoRepository,
                perfilRepository,
                permissaoRepository,
                perfilPermissaoRepository,
                usuarioPerfilRepository,
                perfilPlataformaRepository,
                perfilPlataformaPermissaoRepository,
                usuarioPerfilPlataformaRepository
        );
    }

    @Test
    @DisplayName(
            "Deve ignorar bootstrap quando instalação já está provisionada"
    )
    void deveIgnorarBootstrapQuandoInstalacaoJaEstaProvisionada() {
        habilitarBootstrap();

        configurarInstalacaoCompleta();

        boolean resultado =
                service.provisionar();

        assertThat(resultado)
                .isFalse();

        verifyNoInteractions(
                propertiesValidator,
                provisionamentoOrganizacaoService,
                criacaoUsuarioService,
                permissaoRepository
        );

        verify(perfilPlataformaRepository, never())
                .save(any());

        verify(perfilPlataformaPermissaoRepository, never())
                .saveAll(any());

        verify(usuarioPerfilPlataformaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Não deve executar bootstrap em instalação parcialmente provisionada"
    )
    void naoDeveExecutarBootstrapEmInstalacaoParcialmenteProvisionada() {
        habilitarBootstrap();

        configurarInstalacaoParcial();

        assertThatThrownBy(
                () -> service.provisionar()
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Bootstrap nao pode continuar: "
                                + "estado inicial do banco inconsistente"
                );

        verifyNoInteractions(
                propertiesValidator,
                provisionamentoOrganizacaoService,
                criacaoUsuarioService,
                permissaoRepository
        );

        verify(perfilPlataformaRepository, never())
                .save(any());

        verify(perfilPlataformaPermissaoRepository, never())
                .saveAll(any());

        verify(usuarioPerfilPlataformaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Não deve executar bootstrap sem permissões da plataforma"
    )
    void naoDeveExecutarBootstrapSemPermissoesDaPlataforma() {
        habilitarBootstrap();

        configurarInstalacaoVazia();

        when(permissaoRepository
                .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(List.of());

        assertThatThrownBy(
                () -> service.provisionar()
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Bootstrap nao pode continuar: "
                                + "nenhuma permissao ativa do sistema "
                                + "encontrada para o escopo plataforma"
                );

        verify(propertiesValidator)
                .validar(properties);

        verifyNoInteractions(
                provisionamentoOrganizacaoService,
                criacaoUsuarioService
        );

        verify(perfilPlataformaRepository, never())
                .save(any());

        verify(perfilPlataformaPermissaoRepository, never())
                .saveAll(any());

        verify(usuarioPerfilPlataformaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Deve provisionar instalação vazia com administradores distintos"
    )
    void deveProvisionarInstalacaoVaziaComAdministradoresDistintos() {
        habilitarBootstrap();

        configurarInstalacaoVazia();

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
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                List.of(
                        permissaoA,
                        permissaoB
                )
        );

        var administradorOrganizacao =
                criarUsuario(
                        10L,
                        "admin.organizacao@teste.com"
                );

        var administradorPlataforma =
                criarUsuario(
                        20L,
                        "admin.plataforma@teste.com"
                );

        when(criacaoUsuarioService.criar(
                "admin.organizacao@teste.com",
                "Organizacao@2026"
        )).thenReturn(
                administradorOrganizacao
        );

        when(criacaoUsuarioService.criar(
                "admin.plataforma@teste.com",
                "Plataforma@2026"
        )).thenReturn(
                administradorPlataforma
        );

        when(perfilPlataformaRepository.save(
                any(PerfilPlataformaModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        boolean resultado =
                service.provisionar();

        assertThat(resultado)
                .isTrue();

        verify(propertiesValidator)
                .validar(properties);

        verify(criacaoUsuarioService)
                .criar(
                        "admin.organizacao@teste.com",
                        "Organizacao@2026"
                );

        verify(provisionamentoOrganizacaoService)
                .provisionar(
                        "Organização Principal",
                        administradorOrganizacao
                );

        verify(criacaoUsuarioService)
                .criar(
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        var perfilCaptor =
                ArgumentCaptor.forClass(
                        PerfilPlataformaModel.class
                );

        verify(perfilPlataformaRepository)
                .save(
                        perfilCaptor.capture()
                );

        var perfilAdministrador =
                perfilCaptor.getValue();

        assertThat(
                perfilAdministrador.getNome()
        ).isEqualTo(
                "Administrador da plataforma"
        );

        assertThat(
                perfilAdministrador.getDescricao()
        ).isEqualTo(
                "Perfil administrativo reservado da plataforma"
        );

        assertThat(
                perfilAdministrador.getTipoSistema()
        ).isEqualTo(
                TipoPerfilPlataformaSistemaEnum.ADMINISTRADOR
        );

        assertThat(
                perfilAdministrador.getStatus()
        ).isEqualTo(
                StatusEnum.ATIVO
        );

        assertThat(
                perfilAdministrador.isSistema()
        ).isTrue();

        assertThat(
                perfilAdministrador
                        .isAdministradorSistema()
        ).isTrue();

        @SuppressWarnings("unchecked")
        var permissoesCaptor =
                (ArgumentCaptor<
                        Iterable<
                                PerfilPlataformaPermissaoModel
                        >
                >) (ArgumentCaptor<?>)
                        ArgumentCaptor.forClass(
                                Iterable.class
                        );

        verify(perfilPlataformaPermissaoRepository)
                .saveAll(
                        permissoesCaptor.capture()
                );

        assertThat(
                permissoesCaptor.getValue()
        )
                .hasSize(2)
                .allSatisfy(vinculo -> {
                    assertThat(
                            vinculo.getPerfil()
                    ).isSameAs(
                            perfilAdministrador
                    );

                    assertThat(
                            vinculo.getStatus()
                    ).isEqualTo(
                            StatusEnum.ATIVO
                    );
                })
                .extracting(
                        PerfilPlataformaPermissaoModel::getPermissao
                )
                .containsExactly(
                        permissaoA,
                        permissaoB
                );

        var usuarioPerfilCaptor =
                ArgumentCaptor.forClass(
                        UsuarioPerfilPlataformaModel.class
                );

        verify(usuarioPerfilPlataformaRepository)
                .save(
                        usuarioPerfilCaptor.capture()
                );

        var usuarioPerfil =
                usuarioPerfilCaptor.getValue();

        assertThat(usuarioPerfil.getUsuario())
                .isSameAs(
                        administradorPlataforma
                );

        assertThat(usuarioPerfil.getPerfil())
                .isSameAs(
                        perfilAdministrador
                );

        assertThat(usuarioPerfil.getStatus())
                .isEqualTo(
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve reutilizar identidade quando administradores possuem o mesmo e-mail"
    )
    void deveReutilizarIdentidadeQuandoAdministradoresPossuemOMesmoEmail() {
        properties =
                criarProperties(
                        true,
                        "ADMIN@TESTE.COM",
                        "Identidade@2026",
                        "admin@teste.com",
                        "Identidade@2026"
                );

        criarService();

        configurarInstalacaoVazia();

        var permissao =
                org.mockito.Mockito.mock(
                        PermissaoModel.class
                );

        when(permissaoRepository
                .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                List.of(permissao)
        );

        var administradorCompartilhado =
                criarUsuario(
                        10L,
                        "admin@teste.com"
                );

        when(criacaoUsuarioService.criar(
                "ADMIN@TESTE.COM",
                "Identidade@2026"
        )).thenReturn(
                administradorCompartilhado
        );

        when(perfilPlataformaRepository.save(
                any(PerfilPlataformaModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        assertThatCode(
                () -> service.provisionar()
        ).doesNotThrowAnyException();

        verify(criacaoUsuarioService)
                .criar(
                        "ADMIN@TESTE.COM",
                        "Identidade@2026"
                );

        verify(criacaoUsuarioService, never())
                .criar(
                        "admin@teste.com",
                        "Identidade@2026"
                );

        verify(provisionamentoOrganizacaoService)
                .provisionar(
                        "Organização Principal",
                        administradorCompartilhado
                );

        var usuarioPerfilCaptor =
                ArgumentCaptor.forClass(
                        UsuarioPerfilPlataformaModel.class
                );

        verify(usuarioPerfilPlataformaRepository)
                .save(
                        usuarioPerfilCaptor.capture()
                );

        assertThat(
                usuarioPerfilCaptor
                        .getValue()
                        .getUsuario()
        ).isSameAs(
                administradorCompartilhado
        );
    }

    private void habilitarBootstrap() {
        properties =
                criarProperties(
                        true,
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        criarService();
    }

    private void configurarInstalacaoVazia() {
        when(organizacaoRepository.count())
                .thenReturn(0L);

        when(usuarioRepository.count())
                .thenReturn(0L);

        when(usuarioOrganizacaoRepository.count())
                .thenReturn(0L);

        when(perfilRepository.count())
                .thenReturn(0L);

        when(perfilPermissaoRepository.count())
                .thenReturn(0L);

        when(usuarioPerfilRepository.count())
                .thenReturn(0L);

        when(perfilPlataformaRepository.count())
                .thenReturn(0L);

        when(perfilPlataformaPermissaoRepository.count())
                .thenReturn(0L);

        when(usuarioPerfilPlataformaRepository.count())
                .thenReturn(0L);
    }

    private void configurarInstalacaoCompleta() {
        when(organizacaoRepository.count())
                .thenReturn(1L);

        when(usuarioRepository.count())
                .thenReturn(1L);

        when(usuarioOrganizacaoRepository.count())
                .thenReturn(1L);

        when(perfilRepository.count())
                .thenReturn(1L);

        when(perfilPermissaoRepository.count())
                .thenReturn(1L);

        when(usuarioPerfilRepository.count())
                .thenReturn(1L);

        when(perfilPlataformaRepository.count())
                .thenReturn(1L);

        when(perfilPlataformaPermissaoRepository.count())
                .thenReturn(1L);

        when(usuarioPerfilPlataformaRepository.count())
                .thenReturn(1L);
    }

    private void configurarInstalacaoParcial() {
        when(organizacaoRepository.count())
                .thenReturn(1L);

        when(usuarioRepository.count())
                .thenReturn(0L);

        when(usuarioOrganizacaoRepository.count())
                .thenReturn(0L);

        when(perfilRepository.count())
                .thenReturn(0L);

        when(perfilPermissaoRepository.count())
                .thenReturn(0L);

        when(usuarioPerfilRepository.count())
                .thenReturn(0L);

        when(perfilPlataformaRepository.count())
                .thenReturn(0L);

        when(perfilPlataformaPermissaoRepository.count())
                .thenReturn(0L);

        when(usuarioPerfilPlataformaRepository.count())
                .thenReturn(0L);
    }

    private void criarService() {
        service =
                new BootstrapService(
                        properties,
                        propertiesValidator,
                        provisionamentoOrganizacaoService,
                        criacaoUsuarioService,
                        organizacaoRepository,
                        usuarioRepository,
                        usuarioOrganizacaoRepository,
                        perfilRepository,
                        permissaoRepository,
                        perfilPermissaoRepository,
                        usuarioPerfilRepository,
                        perfilPlataformaRepository,
                        perfilPlataformaPermissaoRepository,
                        usuarioPerfilPlataformaRepository
                );
    }

    private BootstrapProperties criarProperties(
            boolean enabled,
            String emailAdministradorOrganizacao,
            String senhaAdministradorOrganizacao,
            String emailAdministradorPlataforma,
            String senhaAdministradorPlataforma
    ) {
        return new BootstrapProperties(
                enabled,
                "Organização Principal",
                emailAdministradorOrganizacao,
                senhaAdministradorOrganizacao,
                emailAdministradorPlataforma,
                senhaAdministradorPlataforma
        );
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email
    ) {
        return new UsuarioModel(
                id,
                email,
                "senha-criptografada",
                StatusEnum.ATIVO
        );
    }
}