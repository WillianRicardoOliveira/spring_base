package com.empresa.erp.controller.acesso;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
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

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.DetalhePerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.ListaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.service.PerfilService;
import com.empresa.erp.domain.old.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

class PerfilControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PerfilService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(PerfilService.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PerfilController(service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar perfil e retornar status 201")
    void deveCadastrarPerfilERetornarStatus201() throws Exception {
        var dados = new PerfilRecord("Financeiro", "Perfil financeiro");
        var perfil = criarPerfil(1L, "Financeiro", "Perfil financeiro");

        when(service.cadastrar(any(PerfilRecord.class))).thenReturn(perfil);

        mockMvc.perform(post("/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/perfil/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Financeiro"))
                .andExpect(jsonPath("$.descricao").value("Perfil financeiro"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar perfil com nome em branco")
    void deveRetornar400AoCadastrarPerfilComNomeEmBranco() throws Exception {
        var dados = new PerfilRecord("", "Perfil financeiro");

        mockMvc.perform(post("/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar perfis")
    void deveListarPerfis() throws Exception {
        var lista = List.of(new ListaPerfilRecord(1L, "Administrador", "Perfil administrador", StatusEnum.ATIVO));
        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());
        
        when(service.listar(any(Pageable.class), isNull())).thenReturn(pagina);

        mockMvc.perform(get("/perfil"))
                .andExpect(status().isOk());

        verify(service).listar(any(Pageable.class), isNull());
    }

    @Test
    @DisplayName("Deve listar perfis com filtro")
    void deveListarPerfisComFiltro() throws Exception {
        var lista = List.of(new ListaPerfilRecord(2L, "Financeiro", "Perfil financeiro", StatusEnum.ATIVO));
        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(service.listar(any(Pageable.class), eq("fin"))).thenReturn(pagina);

        mockMvc.perform(get("/perfil").param("filtro", "fin"))
                .andExpect(status().isOk());

        verify(service).listar(any(Pageable.class), eq("fin"));
    }

    @Test
    @DisplayName("Deve detalhar perfil")
    void deveDetalharPerfil() throws Exception {
        var detalhe = new DetalhePerfilRecord(1L, "Administrador", "Perfil administrador", StatusEnum.ATIVO);

        when(service.detalhar(1L)).thenReturn(detalhe);

        mockMvc.perform(get("/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Administrador"))
                .andExpect(jsonPath("$.descricao").value("Perfil administrador"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve atualizar perfil")
    void deveAtualizarPerfil() throws Exception {
        var dados = new AtualizaPerfilRecord(1L, "Administrador Master", "Perfil atualizado");
        var detalhe = new DetalhePerfilRecord(1L, "Administrador Master", "Perfil atualizado", StatusEnum.ATIVO);

        when(service.atualizar(any(AtualizaPerfilRecord.class))).thenReturn(detalhe);

        mockMvc.perform(put("/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Administrador Master"))
                .andExpect(jsonPath("$.descricao").value("Perfil atualizado"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar perfil com nome em branco")
    void deveRetornar400AoAtualizarPerfilComNomeEmBranco() throws Exception {
        var dados = new AtualizaPerfilRecord(1L, "", "Perfil atualizado");

        mockMvc.perform(put("/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve remover perfil e retornar status 204")
    void deveRemoverPerfilERetornarStatus204() throws Exception {
        mockMvc.perform(delete("/perfil/1"))
                .andExpect(status().isNoContent());

        verify(service).excluir(1L);
    }

    private PerfilModel criarPerfil(Long id, String nome, String descricao) {
        var perfil = new PerfilModel(new PerfilRecord(nome, descricao));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }
}