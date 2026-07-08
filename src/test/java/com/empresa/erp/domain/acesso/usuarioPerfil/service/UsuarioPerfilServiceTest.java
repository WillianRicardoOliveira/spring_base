package com.empresa.erp.domain.acesso.usuarioPerfil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioPerfilServiceTest {

    @Mock
    private UsuarioPerfilRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @InjectMocks
    private UsuarioPerfilService service;

    @Test
    @DisplayName("Deve vincular perfil ao usuario quando vinculo ainda nao existe")
    void deveVincularPerfilAoUsuarioQuandoVinculoAindaNaoExiste() {
        var dados = new UsuarioPerfilRecord(1L, 2L);
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador", "Perfil administrador");

        when(usuarioRepository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.of(usuario));

        when(perfilRepository.findByIdAndStatus(2L, StatusEnum.ATIVO))
                .thenReturn(Optional.of(perfil));

        when(repository.existsByUsuarioAndPerfilAndStatus(usuario, perfil, StatusEnum.ATIVO))
                .thenReturn(false);

        UsuarioPerfilModel usuarioPerfil = service.cadastrar(dados);

        assertThat(usuarioPerfil.getUsuario()).isEqualTo(usuario);
        assertThat(usuarioPerfil.getPerfil()).isEqualTo(perfil);
        assertThat(usuarioPerfil.getStatus()).isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(usuarioPerfil);
    }

    @Test
    @DisplayName("Deve bloquear cadastro quando usuario nao existe ou nao esta ativo")
    void deveBloquearCadastroQuandoUsuarioNaoExisteOuNaoEstaAtivo() {
        var dados = new UsuarioPerfilRecord(1L, 2L);

        when(usuarioRepository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario nao encontrado.");
    }

    @Test
    @DisplayName("Deve bloquear cadastro quando perfil nao existe ou nao esta ativo")
    void deveBloquearCadastroQuandoPerfilNaoExisteOuNaoEstaAtivo() {
        var dados = new UsuarioPerfilRecord(1L, 2L);
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(usuarioRepository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.of(usuario));

        when(perfilRepository.findByIdAndStatus(2L, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil nao encontrado.");
    }

    @Test
    @DisplayName("Deve bloquear vinculo duplicado ativo entre usuario e perfil")
    void deveBloquearVinculoDuplicadoAtivoEntreUsuarioEPerfil() {
        var dados = new UsuarioPerfilRecord(1L, 2L);
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador", "Perfil administrador");

        when(usuarioRepository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.of(usuario));

        when(perfilRepository.findByIdAndStatus(2L, StatusEnum.ATIVO))
                .thenReturn(Optional.of(perfil));

        when(repository.existsByUsuarioAndPerfilAndStatus(usuario, perfil, StatusEnum.ATIVO))
                .thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil ja vinculado ao usuario.");
    }

    @Test
    @DisplayName("Deve listar perfis vinculados ao usuario")
    void deveListarPerfisVinculadosAoUsuario() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador", "Perfil administrador");
        var usuarioPerfil = criarUsuarioPerfil(3L, usuario, perfil);

        when(repository.findAllByUsuarioIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(List.of(usuarioPerfil));

        var resultado = service.listarPorUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(3L);
        assertThat(resultado.get(0).idPerfil()).isEqualTo(2L);
        assertThat(resultado.get(0).perfil()).isEqualTo("Administrador");
        assertThat(resultado.get(0).status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve detalhar vinculo entre usuario e perfil")
    void deveDetalharVinculoEntreUsuarioEPerfil() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador", "Perfil administrador");
        var usuarioPerfil = criarUsuarioPerfil(3L, usuario, perfil);

        when(repository.getReferenceById(3L)).thenReturn(usuarioPerfil);

        var resultado = service.detalhar(3L);

        assertThat(resultado.id()).isEqualTo(3L);
        assertThat(resultado.idUsuario()).isEqualTo(1L);
        assertThat(resultado.usuario()).isEqualTo("usuario@teste.com");
        assertThat(resultado.idPerfil()).isEqualTo(2L);
        assertThat(resultado.perfil()).isEqualTo("Administrador");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo entre usuario e perfil com auditoria")
    void deveRemoverVinculoEntreUsuarioEPerfilComAuditoria() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador", "Perfil administrador");
        var usuarioPerfil = criarUsuarioPerfil(3L, usuario, perfil);

        when(usuarioLogadoService.getId()).thenReturn(10L);
        when(repository.getReferenceById(3L)).thenReturn(usuarioPerfil);

        service.excluir(3L);

        assertThat(usuarioPerfil.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(usuarioPerfil.getRemovidoEm()).isNotNull();
        assertThat(usuarioPerfil.getRemovidoPor()).isEqualTo(10L);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private PerfilModel criarPerfil(Long id, String nome, String descricao) {
        var perfil = new PerfilModel(new PerfilRecord(nome, descricao));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }

    private UsuarioPerfilModel criarUsuarioPerfil(Long id, UsuarioModel usuario, PerfilModel perfil) {
        var usuarioPerfil = new UsuarioPerfilModel(usuario, perfil);
        ReflectionTestUtils.setField(usuarioPerfil, "id", id);
        return usuarioPerfil;
    }
}