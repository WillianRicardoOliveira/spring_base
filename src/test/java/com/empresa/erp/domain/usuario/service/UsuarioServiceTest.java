package com.empresa.erp.domain.usuario.service;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.AtualizaSenhaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private UsuarioService service;

    @Test
    @DisplayName("Deve cadastrar usuario quando email ainda nao existe")
    void deveCadastrarUsuarioQuandoEmailNaoExiste() {
        var dados = new UsuarioRecord("Usuario@Teste.com", "123456");

        when(repository.existsByEmailIgnoreCase("Usuario@Teste.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("senha-criptografada");

        UsuarioModel usuario = service.cadastrar(dados);

        assertThat(usuario.getEmail()).isEqualTo("usuario@teste.com");
        assertThat(usuario.getSenha()).isEqualTo("senha-criptografada");
        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(usuario);
    }

    @Test
    @DisplayName("Deve bloquear cadastro de usuario duplicado")
    void deveBloquearCadastroDeUsuarioDuplicado() {
        var dados = new UsuarioRecord("usuario@teste.com", "123456");

        when(repository.existsByEmailIgnoreCase("usuario@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario ja cadastrado.");
    }

    @Test
    @DisplayName("Deve listar usuarios ativos sem filtro")
    void deveListarUsuariosAtivosSemFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(repository.findAllByStatus(paginacao, StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(usuario)));

        var resultado = service.listar(paginacao, null);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(1L);
        assertThat(resultado.getContent().get(0).email()).isEqualTo("usuario@teste.com");
        assertThat(resultado.getContent().get(0).status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve listar usuarios ativos com filtro")
    void deveListarUsuariosAtivosComFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var usuario = criarUsuario(1L, "financeiro@teste.com");

        when(repository.findByEmailContainingIgnoreCaseAndStatus(paginacao, "fin", StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(usuario)));

        var resultado = service.listar(paginacao, "fin");

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(1L);
        assertThat(resultado.getContent().get(0).email()).isEqualTo("financeiro@teste.com");
    }

    @Test
    @DisplayName("Deve detalhar usuario")
    void deveDetalharUsuario() {
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(repository.getReferenceById(1L)).thenReturn(usuario);

        var resultado = service.detalhar(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.email()).isEqualTo("usuario@teste.com");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar usuario quando email nao esta duplicado")
    void deveAtualizarUsuarioQuandoEmailNaoEstaDuplicado() {
        var dados = new AtualizaUsuarioRecord(1L, "Novo@Teste.com");
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(repository.existsByEmailIgnoreCaseAndIdNot("Novo@Teste.com", 1L)).thenReturn(false);
        when(repository.findByIdAndStatus(1L, StatusEnum.ATIVO)).thenReturn(Optional.of(usuario));

        var resultado = service.atualizar(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.email()).isEqualTo("novo@teste.com");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve bloquear atualizacao quando email pertence a outro usuario")
    void deveBloquearAtualizacaoQuandoEmailPertenceAOutroUsuario() {
        var dados = new AtualizaUsuarioRecord(1L, "usuario@teste.com");

        when(repository.existsByEmailIgnoreCaseAndIdNot("usuario@teste.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario ja cadastrado.");
    }

    @Test
    @DisplayName("Deve bloquear atualizacao de usuario removido")
    void deveBloquearAtualizacaoDeUsuarioRemovido() {
        var dados = new AtualizaUsuarioRecord(1L, "novo@teste.com");

        when(repository.existsByEmailIgnoreCaseAndIdNot("novo@teste.com", 1L))
                .thenReturn(false);

        when(repository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario nao encontrado ou removido.");
    }

    @Test
    @DisplayName("Deve atualizar senha do usuario")
    void deveAtualizarSenhaDoUsuario() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "nova-senha");
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(repository.findByIdAndStatus(1L, StatusEnum.ATIVO)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nova-senha")).thenReturn("nova-senha-criptografada");

        var resultado = service.atualizarSenha(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.email()).isEqualTo("usuario@teste.com");
        assertThat(usuario.getSenha()).isEqualTo("nova-senha-criptografada");
    }

    @Test
    @DisplayName("Deve bloquear alteracao de senha de usuario removido")
    void deveBloquearAlteracaoDeSenhaDeUsuarioRemovido() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "nova-senha");

        when(repository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarSenha(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario nao encontrado ou removido.");
    }

    @Test
    @DisplayName("Deve remover usuario com auditoria")
    void deveRemoverUsuarioComAuditoria() {
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(usuarioLogadoService.getId()).thenReturn(10L);
        when(repository.getReferenceById(1L)).thenReturn(usuario);

        service.excluir(1L);

        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(usuario.getRemovidoEm()).isNotNull();
        assertThat(usuario.getRemovidoPor()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve carregar usuario autenticado pelo email")
    void deveCarregarUsuarioAutenticadoPeloEmail() {
        var usuarioAutenticado = org.mockito.Mockito.mock(UsuarioAutenticado.class);

        when(usuarioAutenticadoService.buscarPorEmail("usuario@teste.com"))
                .thenReturn(usuarioAutenticado);

        var resultado = service.loadUserByUsername("usuario@teste.com");

        assertThat(resultado).isEqualTo(usuarioAutenticado);
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario autenticado nao for encontrado")
    void deveLancarExcecaoQuandoUsuarioAutenticadoNaoForEncontrado() {
        when(usuarioAutenticadoService.buscarPorEmail("usuario@teste.com"))
                .thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("usuario@teste.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuario nao encontrado");
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}