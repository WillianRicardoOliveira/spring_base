package com.empresa.erp.core.organizacao.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@ExtendWith(MockitoExtension.class)
class ContextoOrganizacaoServiceTest {

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private UsuarioLogadoService
            usuarioLogadoService;

    @InjectMocks
    private ContextoOrganizacaoService service;

    @Test
    @DisplayName("Deve definir contexto para vínculo ativo")
    void deveDefinirContextoParaVinculoAtivo() {
        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        when(organizacaoRepository.existsByIdAndStatus(
                20L,
                StatusEnum.ATIVO
        )).thenReturn(true);

        when(usuarioOrganizacaoRepository
                .existsByUsuarioIdAndOrganizacaoIdAndStatus(
                        10L,
                        20L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        service.definir(20L);

        verify(contextoOrganizacao).definir(20L);
    }

    @Test
    @DisplayName("Não deve aceitar organização nula")
    void naoDeveAceitarOrganizacaoNula() {
        assertThatThrownBy(() -> service.definir(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao invalida.");

        verifyNoInteractions(
                usuarioLogadoService,
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                contextoOrganizacao
        );
    }

    @Test
    @DisplayName("Não deve aceitar organização igual a zero")
    void naoDeveAceitarOrganizacaoIgualAZero() {
        assertThatThrownBy(() -> service.definir(0L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao invalida.");

        verifyNoInteractions(
                usuarioLogadoService,
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                contextoOrganizacao
        );
    }

    @Test
    @DisplayName("Não deve aceitar organização negativa")
    void naoDeveAceitarOrganizacaoNegativa() {
        assertThatThrownBy(() -> service.definir(-1L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao invalida.");

        verifyNoInteractions(
                usuarioLogadoService,
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                contextoOrganizacao
        );
    }

    @Test
    @DisplayName("Não deve definir contexto para organização inativa")
    void naoDeveDefinirContextoParaOrganizacaoInativa() {
        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        when(organizacaoRepository.existsByIdAndStatus(
                20L,
                StatusEnum.ATIVO
        )).thenReturn(false);

        assertThatThrownBy(() -> service.definir(20L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Acesso negado.");

        verify(
                usuarioOrganizacaoRepository,
                never()
        ).existsByUsuarioIdAndOrganizacaoIdAndStatus(
                10L,
                20L,
                StatusEnum.ATIVO
        );

        verify(
                contextoOrganizacao,
                never()
        ).definir(20L);
    }

    @Test
    @DisplayName("Não deve definir contexto sem vínculo ativo")
    void naoDeveDefinirContextoSemVinculoAtivo() {
        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        when(organizacaoRepository.existsByIdAndStatus(
                20L,
                StatusEnum.ATIVO
        )).thenReturn(true);

        when(usuarioOrganizacaoRepository
                .existsByUsuarioIdAndOrganizacaoIdAndStatus(
                        10L,
                        20L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        assertThatThrownBy(() -> service.definir(20L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Acesso negado.");

        verify(
                contextoOrganizacao,
                never()
        ).definir(20L);
    }

    @Test
    @DisplayName("Não deve continuar sem usuário autenticado")
    void naoDeveContinuarSemUsuarioAutenticado() {
        when(usuarioLogadoService.getId())
                .thenThrow(
                        new ValidacaoException(
                                "Usuario nao autenticado."
                        )
                );

        assertThatThrownBy(() -> service.definir(20L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario nao autenticado.");

        verifyNoInteractions(
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                contextoOrganizacao
        );
    }
}