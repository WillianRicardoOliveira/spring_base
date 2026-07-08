package com.empresa.erp.core.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticadoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioPerfilRepository usuarioPerfilRepository;

    @InjectMocks
    private UsuarioAutenticadoService service;

    @Test
    @DisplayName("Deve buscar usuario autenticado com permissoes")
    void deveBuscarUsuarioAutenticadoComPermissoes() {
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com"))
                .thenReturn(usuario);

        when(usuarioPerfilRepository.buscarChavesPermissoesAtivasPorUsuario(eq(1L), any()))
                .thenReturn(Set.of("ACESSO_USUARIO_LISTAR", "ACESSO_USUARIO_EDITAR"));

        var resultado = service.buscarPorEmail("usuario@teste.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("usuario@teste.com");
        assertThat(resultado.getUsername()).isEqualTo("usuario@teste.com");
        assertThat(resultado.getPassword()).isEqualTo("senha-criptografada");
        assertThat(resultado.isEnabled()).isTrue();

        assertThat(resultado.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ACESSO_USUARIO_LISTAR", "ACESSO_USUARIO_EDITAR");

        verify(usuarioRepository).findByEmailIgnoreCase("usuario@teste.com");
        verify(usuarioPerfilRepository).buscarChavesPermissoesAtivasPorUsuario(eq(1L), any());
    }

    @Test
    @DisplayName("Deve buscar usuario autenticado sem permissoes")
    void deveBuscarUsuarioAutenticadoSemPermissoes() {
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com"))
                .thenReturn(usuario);

        when(usuarioPerfilRepository.buscarChavesPermissoesAtivasPorUsuario(eq(1L), any()))
                .thenReturn(Set.of());

        var resultado = service.buscarPorEmail("usuario@teste.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getAuthorities()).isEmpty();

        verify(usuarioRepository).findByEmailIgnoreCase("usuario@teste.com");
        verify(usuarioPerfilRepository).buscarChavesPermissoesAtivasPorUsuario(eq(1L), any());
    }

    @Test
    @DisplayName("Deve retornar null quando usuario nao existir")
    void deveRetornarNullQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com"))
                .thenReturn(null);

        var resultado = service.buscarPorEmail("usuario@teste.com");

        assertThat(resultado).isNull();

        verify(usuarioRepository).findByEmailIgnoreCase("usuario@teste.com");
        verifyNoInteractions(usuarioPerfilRepository);
    }

    @Test
    @DisplayName("Deve retornar null quando usuario nao estiver ativo")
    void deveRetornarNullQuandoUsuarioNaoEstiverAtivo() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        usuario.inativar();

        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com"))
                .thenReturn(usuario);

        var resultado = service.buscarPorEmail("usuario@teste.com");

        assertThat(resultado).isNull();

        verify(usuarioRepository).findByEmailIgnoreCase("usuario@teste.com");
        verifyNoInteractions(usuarioPerfilRepository);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}