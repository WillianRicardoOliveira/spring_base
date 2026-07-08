package com.empresa.erp.core.security.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioAutenticadoTest {

    @Test
    @DisplayName("Deve expor dados do usuario autenticado")
    void deveExporDadosDoUsuarioAutenticado() {
        var usuario = criarUsuario(1L, "usuario@teste.com", "senha-criptografada");
        var authorities = List.of(
                new SimpleGrantedAuthority("ACESSO_USUARIO_LISTAR"),
                new SimpleGrantedAuthority("ACESSO_USUARIO_EDITAR")
        );

        var usuarioAutenticado = new UsuarioAutenticado(usuario, authorities);

        assertThat(usuarioAutenticado.getUsuario()).isEqualTo(usuario);
        assertThat(usuarioAutenticado.getId()).isEqualTo(1L);
        assertThat(usuarioAutenticado.getEmail()).isEqualTo("usuario@teste.com");
        assertThat(usuarioAutenticado.getUsername()).isEqualTo("usuario@teste.com");
        assertThat(usuarioAutenticado.getPassword()).isEqualTo("senha-criptografada");

        assertThat(usuarioAutenticado.getAuthorities())
                .extracting("authority")
                .containsExactly("ACESSO_USUARIO_LISTAR", "ACESSO_USUARIO_EDITAR");
    }

    @Test
    @DisplayName("Deve indicar usuario ativo como habilitado")
    void deveIndicarUsuarioAtivoComoHabilitado() {
        var usuario = criarUsuario(1L, "usuario@teste.com", "senha-criptografada");

        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());

        assertThat(usuarioAutenticado.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Deve indicar usuario inativo como nao habilitado")
    void deveIndicarUsuarioInativoComoNaoHabilitado() {
        var usuario = criarUsuario(1L, "usuario@teste.com", "senha-criptografada");
        usuario.inativar();

        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());

        assertThat(usuarioAutenticado.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve manter flags padrao do UserDetails")
    void deveManterFlagsPadraoDoUserDetails() {
        var usuario = criarUsuario(1L, "usuario@teste.com", "senha-criptografada");

        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());

        assertThat(usuarioAutenticado.isAccountNonExpired()).isTrue();
        assertThat(usuarioAutenticado.isAccountNonLocked()).isTrue();
        assertThat(usuarioAutenticado.isCredentialsNonExpired()).isTrue();
    }

    private UsuarioModel criarUsuario(Long id, String email, String senha) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), senha);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}