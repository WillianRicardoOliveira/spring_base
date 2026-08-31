package com.empresa.erp.core.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticadoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioAutenticadoService service;

    @Test
    @DisplayName("Deve buscar usuario autenticado ativo")
    void deveBuscarUsuarioAutenticadoAtivo() {
        var usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com",
                        StatusEnum.ATIVO
                );

        when(usuarioRepository.findByEmailIgnoreCase(
                "usuario@teste.com"
        )).thenReturn(usuario);

        var resultado =
                service.buscarPorEmail(
                        "usuario@teste.com"
                );

        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.getUsuario())
                .isEqualTo(usuario);

        assertThat(resultado.getId())
                .isEqualTo(1L);

        assertThat(resultado.getEmail())
                .isEqualTo("usuario@teste.com");

        assertThat(resultado.getUsername())
                .isEqualTo("usuario@teste.com");

        assertThat(resultado.getPassword())
                .isEqualTo("senha-criptografada");

        assertThat(resultado.isEnabled())
                .isTrue();

        assertThat(resultado.getAuthorities())
                .isEmpty();

        verify(usuarioRepository)
                .findByEmailIgnoreCase(
                        "usuario@teste.com"
                );
    }

    @Test
    @DisplayName("Deve retornar null quando usuario nao existir")
    void deveRetornarNullQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByEmailIgnoreCase(
                "usuario@teste.com"
        )).thenReturn(null);

        var resultado =
                service.buscarPorEmail(
                        "usuario@teste.com"
                );

        assertThat(resultado)
                .isNull();

        verify(usuarioRepository)
                .findByEmailIgnoreCase(
                        "usuario@teste.com"
                );
    }

    @Test
    @DisplayName("Deve retornar null quando usuario nao estiver ativo")
    void deveRetornarNullQuandoUsuarioNaoEstiverAtivo() {
        var usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com",
                        StatusEnum.INATIVO
                );

        when(usuarioRepository.findByEmailIgnoreCase(
                "usuario@teste.com"
        )).thenReturn(usuario);

        var resultado =
                service.buscarPorEmail(
                        "usuario@teste.com"
                );

        assertThat(resultado)
                .isNull();

        verify(usuarioRepository)
                .findByEmailIgnoreCase(
                        "usuario@teste.com"
                );
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email,
            StatusEnum status
    ) {
        return new UsuarioModel(
                id,
                email,
                "senha-criptografada",
                status
        );
    }
}