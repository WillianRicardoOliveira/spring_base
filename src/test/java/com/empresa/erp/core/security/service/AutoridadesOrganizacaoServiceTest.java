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
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;

@ExtendWith(MockitoExtension.class)
class AutoridadesOrganizacaoServiceTest {

    private static final Long ID_USUARIO = 10L;

    private static final Long ID_ORGANIZACAO = 20L;

    @Mock
    private UsuarioPerfilRepository usuarioPerfilRepository;

    @InjectMocks
    private AutoridadesOrganizacaoService service;

    @Test
    @DisplayName(
            "Deve buscar autoridades ativas da organizacao selecionada"
    )
    void deveBuscarAutoridadesAtivasDaOrganizacaoSelecionada() {
        when(usuarioPerfilRepository
                .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Set.of(
                        "EMPRESA_LISTAR",
                        "EMPRESA_CRIAR",
                        "CONFIGURACAO_ESTABELECIMENTO_LISTAR"
                )
        );

        var autoridades =
                service.buscar(
                        ID_USUARIO,
                        ID_ORGANIZACAO
                );

        assertThat(autoridades)
                .extracting(
                        autoridade ->
                                autoridade.getAuthority()
                )
                .containsExactlyInAnyOrder(
                        "EMPRESA_LISTAR",
                        "EMPRESA_CRIAR",
                        "CONFIGURACAO_ESTABELECIMENTO_LISTAR"
                );

        verify(usuarioPerfilRepository)
                .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve retornar lista vazia quando usuario nao possui autoridades"
    )
    void deveRetornarListaVaziaQuandoUsuarioNaoPossuiAutoridades() {
        when(usuarioPerfilRepository
                .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Set.of());

        var autoridades =
                service.buscar(
                        ID_USUARIO,
                        ID_ORGANIZACAO
                );

        assertThat(autoridades)
                .isEmpty();

        verify(usuarioPerfilRepository)
                .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve criar autoridades compativeis com Spring Security"
    )
    void deveCriarAutoridadesCompativeisComSpringSecurity() {
        when(usuarioPerfilRepository
                .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Set.of(
                        "USUARIO_LISTAR"
                )
        );

        var autoridades =
                service.buscar(
                        ID_USUARIO,
                        ID_ORGANIZACAO
                );

        assertThat(autoridades)
                .hasSize(1);

        assertThat(
                autoridades.get(0).getAuthority()
        ).isEqualTo(
                "USUARIO_LISTAR"
        );
    }
}