package com.empresa.erp.controller.acesso;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.AtualizaSenhaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.DetalheUsuarioRecord;
import com.empresa.erp.domain.usuario.record.ListaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(UsuarioService.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UsuarioController(service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar usuario e retornar status 201")
    void deveCadastrarUsuarioERetornarStatus201() throws Exception {
        var dados = new UsuarioRecord("usuario@teste.com", "123456");
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(service.cadastrar(any(UsuarioRecord.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuario/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("usuario@teste.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar usuario com email em branco")
    void deveRetornar400AoCadastrarUsuarioComEmailEmBranco() throws Exception {
        var dados = new UsuarioRecord("", "123456");

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar usuario com email invalido")
    void deveRetornar400AoCadastrarUsuarioComEmailInvalido() throws Exception {
        var dados = new UsuarioRecord("email-invalido", "123456");

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar usuario com senha em branco")
    void deveRetornar400AoCadastrarUsuarioComSenhaEmBranco() throws Exception {
        var dados = new UsuarioRecord("usuario@teste.com", "");

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve listar usuarios")
    void deveListarUsuarios() throws Exception {
        var lista = List.of(new ListaUsuarioRecord(1L, "usuario@teste.com", StatusEnum.ATIVO));
        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(service.listar(any(Pageable.class), isNull())).thenReturn(pagina);

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].email").value("usuario@teste.com"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"));

        verify(service).listar(any(Pageable.class), isNull());
    }

    @Test
    @DisplayName("Deve listar usuarios com filtro")
    void deveListarUsuariosComFiltro() throws Exception {
        var lista = List.of(new ListaUsuarioRecord(2L, "financeiro@teste.com", StatusEnum.ATIVO));
        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(service.listar(any(Pageable.class), eq("fin"))).thenReturn(pagina);

        mockMvc.perform(get("/usuario").param("filtro", "fin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2L))
                .andExpect(jsonPath("$.content[0].email").value("financeiro@teste.com"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"));

        verify(service).listar(any(Pageable.class), eq("fin"));
    }

    @Test
    @DisplayName("Deve detalhar usuario")
    void deveDetalharUsuario() throws Exception {
        var detalhe = new DetalheUsuarioRecord(1L, "usuario@teste.com", StatusEnum.ATIVO);

        when(service.detalhar(1L)).thenReturn(detalhe);

        mockMvc.perform(get("/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("usuario@teste.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve atualizar usuario")
    void deveAtualizarUsuario() throws Exception {
        var dados = new AtualizaUsuarioRecord(1L, "novo@teste.com");
        var detalhe = new DetalheUsuarioRecord(1L, "novo@teste.com", StatusEnum.ATIVO);

        when(service.atualizar(any(AtualizaUsuarioRecord.class))).thenReturn(detalhe);

        mockMvc.perform(put("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("novo@teste.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar usuario com email invalido")
    void deveRetornar400AoAtualizarUsuarioComEmailInvalido() throws Exception {
        var dados = new AtualizaUsuarioRecord(1L, "email-invalido");

        mockMvc.perform(put("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve alterar senha do usuario")
    void deveAlterarSenhaDoUsuario() throws Exception {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "nova-senha");
        var detalhe = new DetalheUsuarioRecord(1L, "usuario@teste.com", StatusEnum.ATIVO);

        when(service.atualizarSenha(any(AtualizaSenhaUsuarioRecord.class))).thenReturn(detalhe);

        mockMvc.perform(put("/usuario/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("usuario@teste.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao alterar senha com senha em branco")
    void deveRetornar400AoAlterarSenhaComSenhaEmBranco() throws Exception {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "");

        mockMvc.perform(put("/usuario/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve remover usuario e retornar status 204")
    void deveRemoverUsuarioERetornarStatus204() throws Exception {
        mockMvc.perform(delete("/usuario/1"))
                .andExpect(status().isNoContent());

        verify(service).excluir(1L);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}