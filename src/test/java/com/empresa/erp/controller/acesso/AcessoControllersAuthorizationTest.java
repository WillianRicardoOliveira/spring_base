package com.empresa.erp.controller.acesso;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.usuario.record.AtualizaSenhaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class AcessoControllersAuthorizationTest {

    @Test
    @DisplayName("Deve validar permissoes do PerfilController")
    void deveValidarPermissoesDoPerfilController() throws Exception {
        assertThat(preAuthorize(PerfilController.class, "cadastrar", PerfilRecord.class, UriComponentsBuilder.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_CRIAR')");

        assertThat(preAuthorize(PerfilController.class, "listar", Pageable.class, String.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_LISTAR')");

        assertThat(preAuthorize(PerfilController.class, "atualizar", AtualizaPerfilRecord.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_EDITAR')");

        assertThat(preAuthorize(PerfilController.class, "excluir", Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_EXCLUIR')");

        assertThat(preAuthorize(PerfilController.class, "detalhar", Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_DETALHAR')");
    }

    @Test
    @DisplayName("Deve validar permissoes do PermissaoController")
    void deveValidarPermissoesDoPermissaoController() throws Exception {
        assertThat(preAuthorize(PermissaoController.class, "cadastrar", PermissaoRecord.class, UriComponentsBuilder.class))
                .isEqualTo("hasAuthority('ACESSO_PERMISSAO_CRIAR')");

        assertThat(preAuthorize(PermissaoController.class, "listar", Pageable.class, String.class))
                .isEqualTo("hasAuthority('ACESSO_PERMISSAO_LISTAR')");

        assertThat(preAuthorize(PermissaoController.class, "atualizar", AtualizaPermissaoRecord.class))
                .isEqualTo("hasAuthority('ACESSO_PERMISSAO_EDITAR')");

        assertThat(preAuthorize(PermissaoController.class, "excluir", Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERMISSAO_EXCLUIR')");

        assertThat(preAuthorize(PermissaoController.class, "detalhar", Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERMISSAO_DETALHAR')");
    }

    @Test
    @DisplayName("Deve validar permissoes do PerfilPermissaoController")
    void deveValidarPermissoesDoPerfilPermissaoController() throws Exception {
        assertThat(preAuthorize(PerfilPermissaoController.class, "cadastrar", PerfilPermissaoRecord.class, UriComponentsBuilder.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_PERMISSAO_CRIAR')");

        assertThat(preAuthorize(PerfilPermissaoController.class, "listarPorPerfil", Pageable.class, Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_PERMISSAO_LISTAR')");

        assertThat(preAuthorize(PerfilPermissaoController.class, "excluir", Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_PERMISSAO_EXCLUIR')");

        assertThat(preAuthorize(PerfilPermissaoController.class, "detalhar", Long.class))
                .isEqualTo("hasAuthority('ACESSO_PERFIL_PERMISSAO_DETALHAR')");
    }

    @Test
    @DisplayName("Deve validar permissoes do UsuarioPerfilController")
    void deveValidarPermissoesDoUsuarioPerfilController() throws Exception {
        assertThat(preAuthorize(UsuarioPerfilController.class, "cadastrar", UsuarioPerfilRecord.class, UriComponentsBuilder.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_PERFIL_CRIAR')");

        assertThat(preAuthorize(UsuarioPerfilController.class, "listarPorUsuario", Long.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_PERFIL_LISTAR')");

        assertThat(preAuthorize(UsuarioPerfilController.class, "excluir", Long.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_PERFIL_EXCLUIR')");

        assertThat(preAuthorize(UsuarioPerfilController.class, "detalhar", Long.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_PERFIL_DETALHAR')");
    }

    @Test
    @DisplayName("Deve validar permissoes do UsuarioController")
    void deveValidarPermissoesDoUsuarioController() throws Exception {
        assertThat(preAuthorize(UsuarioController.class, "cadastrar", UsuarioRecord.class, UriComponentsBuilder.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_CRIAR')");

        assertThat(preAuthorize(UsuarioController.class, "listar", Pageable.class, String.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_LISTAR')");

        assertThat(preAuthorize(UsuarioController.class, "atualizar", AtualizaUsuarioRecord.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_EDITAR')");

        assertThat(preAuthorize(UsuarioController.class, "excluir", Long.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_EXCLUIR')");

        assertThat(preAuthorize(UsuarioController.class, "detalhar", Long.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_DETALHAR')");

        assertThat(preAuthorize(UsuarioController.class, "alterarSenha", AtualizaSenhaUsuarioRecord.class))
                .isEqualTo("hasAuthority('ACESSO_USUARIO_SENHA_EDITAR')");
    }

    private String preAuthorize(Class<?> controller, String metodo, Class<?>... parametros) throws Exception {
        return controller.getDeclaredMethod(metodo, parametros)
                .getAnnotation(PreAuthorize.class)
                .value();
    }
}