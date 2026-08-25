package com.empresa.erp.core.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.repository.UsuarioPerfilPlataformaRepository;

@ExtendWith(MockitoExtension.class)
class AutoridadesPlataformaServiceTest {

    private static final Long ID_USUARIO =
            10L;

    @Mock
    private UsuarioPerfilPlataformaRepository
            usuarioPerfilPlataformaRepository;

    @InjectMocks
    private AutoridadesPlataformaService service;

    @Test
    @DisplayName(
            "Deve buscar autoridades ativas da plataforma"
    )
    void deveBuscarAutoridadesAtivasDaPlataforma() {
        when(usuarioPerfilPlataformaRepository
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Set.of(
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        "PLATAFORMA_ORGANIZACAO_CRIAR",
                        "PLATAFORMA_ORGANIZACAO_INATIVAR"
                )
        );

        var autoridades =
                service.buscar(ID_USUARIO);

        assertThat(autoridades)
                .extracting(
                        autoridade ->
                                autoridade.getAuthority()
                )
                .containsExactlyInAnyOrder(
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        "PLATAFORMA_ORGANIZACAO_CRIAR",
                        "PLATAFORMA_ORGANIZACAO_INATIVAR"
                );

        verify(usuarioPerfilPlataformaRepository)
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve retornar lista vazia quando usuário não possui autoridades da plataforma"
    )
    void deveRetornarListaVaziaQuandoUsuarioNaoPossuiAutoridadesDaPlataforma() {
        when(usuarioPerfilPlataformaRepository
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Set.of()
        );

        var autoridades =
                service.buscar(ID_USUARIO);

        assertThat(autoridades)
                .isEmpty();

        verify(usuarioPerfilPlataformaRepository)
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve criar autoridades da plataforma compatíveis com Spring Security"
    )
    void deveCriarAutoridadesDaPlataformaCompativeisComSpringSecurity() {
        when(usuarioPerfilPlataformaRepository
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Set.of(
                        "PLATAFORMA_ORGANIZACAO_LISTAR"
                )
        );

        var autoridades =
                service.buscar(ID_USUARIO);

        assertThat(autoridades)
                .hasSize(1);

        assertThat(
                autoridades.get(0).getAuthority()
        ).isEqualTo(
                "PLATAFORMA_ORGANIZACAO_LISTAR"
        );
    }

    @Test
    @DisplayName(
            "Não deve consultar permissões de escopo organizacional"
    )
    void naoDeveConsultarPermissoesDeEscopoOrganizacional() {
        when(usuarioPerfilPlataformaRepository
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Set.of(
                        "PLATAFORMA_ORGANIZACAO_LISTAR"
                )
        );

        service.buscar(ID_USUARIO);

        verify(usuarioPerfilPlataformaRepository)
                .buscarChavesPermissoesAtivasPorUsuario(
                        ID_USUARIO,
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );
    }
}