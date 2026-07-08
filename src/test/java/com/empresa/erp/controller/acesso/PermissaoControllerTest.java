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

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.DetalhePermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.ListaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.service.PermissaoService;
import com.empresa.erp.domain.old.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

class PermissaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PermissaoService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(PermissaoService.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PermissaoController(service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar permissao e retornar status 201")
    void deveCadastrarPermissaoERetornarStatus201() throws Exception {
        var dados = new PermissaoRecord(
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(service.cadastrar(any(PermissaoRecord.class))).thenReturn(permissao);

        mockMvc.perform(post("/permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/permissao/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Listar perfis"))
                .andExpect(jsonPath("$.chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.descricao").value("Permite listar perfis"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar permissao com nome em branco")
    void deveRetornar400AoCadastrarPermissaoComNomeEmBranco() throws Exception {
        var dados = new PermissaoRecord("", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");

        mockMvc.perform(post("/permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar permissao com chave em branco")
    void deveRetornar400AoCadastrarPermissaoComChaveEmBranco() throws Exception {
        var dados = new PermissaoRecord("Listar perfis", "", "Permite listar perfis");

        mockMvc.perform(post("/permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve listar permissoes")
    void deveListarPermissoes() throws Exception {
        var lista = List.of(new ListaPermissaoRecord(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis",
                StatusEnum.ATIVO
        ));

        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(service.listar(any(Pageable.class), isNull())).thenReturn(pagina);

        mockMvc.perform(get("/permissao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nome").value("Listar perfis"))
                .andExpect(jsonPath("$.content[0].chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.content[0].descricao").value("Permite listar perfis"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"));

        verify(service).listar(any(Pageable.class), isNull());
    }

    @Test
    @DisplayName("Deve listar permissoes com filtro")
    void deveListarPermissoesComFiltro() throws Exception {
        var lista = List.of(new ListaPermissaoRecord(
                2L,
                "Criar perfis",
                "ACESSO_PERFIL_CRIAR",
                "Permite criar perfis",
                StatusEnum.ATIVO
        ));

        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(service.listar(any(Pageable.class), eq("criar"))).thenReturn(pagina);

        mockMvc.perform(get("/permissao").param("filtro", "criar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2L))
                .andExpect(jsonPath("$.content[0].nome").value("Criar perfis"))
                .andExpect(jsonPath("$.content[0].chave").value("ACESSO_PERFIL_CRIAR"))
                .andExpect(jsonPath("$.content[0].descricao").value("Permite criar perfis"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"));

        verify(service).listar(any(Pageable.class), eq("criar"));
    }

    @Test
    @DisplayName("Deve detalhar permissao")
    void deveDetalharPermissao() throws Exception {
        var detalhe = new DetalhePermissaoRecord(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis",
                StatusEnum.ATIVO
        );

        when(service.detalhar(1L)).thenReturn(detalhe);

        mockMvc.perform(get("/permissao/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Listar perfis"))
                .andExpect(jsonPath("$.chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.descricao").value("Permite listar perfis"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve atualizar permissao")
    void deveAtualizarPermissao() throws Exception {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar perfil",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfil"
        );

        var detalhe = new DetalhePermissaoRecord(
                1L,
                "Listar perfil",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfil",
                StatusEnum.ATIVO
        );

        when(service.atualizar(any(AtualizaPermissaoRecord.class))).thenReturn(detalhe);

        mockMvc.perform(put("/permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Listar perfil"))
                .andExpect(jsonPath("$.chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.descricao").value("Permite listar perfil"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar permissao com nome em branco")
    void deveRetornar400AoAtualizarPermissaoComNomeEmBranco() throws Exception {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfil"
        );

        mockMvc.perform(put("/permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar permissao com chave em branco")
    void deveRetornar400AoAtualizarPermissaoComChaveEmBranco() throws Exception {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar perfil",
                "",
                "Permite listar perfil"
        );

        mockMvc.perform(put("/permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve remover permissao e retornar status 204")
    void deveRemoverPermissaoERetornarStatus204() throws Exception {
        mockMvc.perform(delete("/permissao/1"))
                .andExpect(status().isNoContent());

        verify(service).excluir(1L);
    }

    private PermissaoModel criarPermissao(Long id, String nome, String chave, String descricao) {
        var permissao = new PermissaoModel(new PermissaoRecord(nome, chave, descricao));
        ReflectionTestUtils.setField(permissao, "id", id);
        return permissao;
    }
}