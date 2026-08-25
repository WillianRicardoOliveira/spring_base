package com.empresa.erp.controller.acesso;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.DetalhePerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.ListaPerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.service.PerfilPermissaoService;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.old.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

class PerfilPermissaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PerfilPermissaoService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(PerfilPermissaoService.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PerfilPermissaoController(service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Deve vincular permissao ao perfil e retornar status 201")
    void deveVincularPermissaoAoPerfilERetornarStatus201() throws Exception {
        var dados = new PerfilPermissaoRecord(1L, 2L);

        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        var permissao = criarPermissao(2L, "Listar perfis", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        when(service.cadastrar(any(PerfilPermissaoRecord.class))).thenReturn(perfilPermissao);

        mockMvc.perform(post("/perfil-permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/perfil-permissao/3"))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.idPerfil").value(1L))
                .andExpect(jsonPath("$.perfil").value("Administrador"))
                .andExpect(jsonPath("$.idPermissao").value(2L))
                .andExpect(jsonPath("$.permissao").value("Listar perfis"))
                .andExpect(jsonPath("$.chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao vincular permissao sem perfil")
    void deveRetornar400AoVincularPermissaoSemPerfil() throws Exception {
        var dados = new PerfilPermissaoRecord(null, 2L);

        mockMvc.perform(post("/perfil-permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve retornar 400 ao vincular permissao sem permissao")
    void deveRetornar400AoVincularPermissaoSemPermissao() throws Exception {
        var dados = new PerfilPermissaoRecord(1L, null);

        mockMvc.perform(post("/perfil-permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve listar permissoes por perfil")
    void deveListarPermissoesPorPerfil() throws Exception {
        var lista = List.of(new ListaPerfilPermissaoRecord(
                3L,
                2L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                StatusEnum.ATIVO
        ));

        var pagina = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(service.listarPorPerfil(any(Pageable.class), any(Long.class))).thenReturn(pagina);

        mockMvc.perform(get("/perfil-permissao/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3L))
                .andExpect(jsonPath("$.content[0].idPermissao").value(2L))
                .andExpect(jsonPath("$.content[0].permissao").value("Listar perfis"))
                .andExpect(jsonPath("$.content[0].chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"));

        verify(service).listarPorPerfil(any(Pageable.class), any(Long.class));
    }

    @Test
    @DisplayName("Deve detalhar vinculo entre perfil e permissao")
    void deveDetalharVinculoEntrePerfilEPermissao() throws Exception {
        var detalhe = new DetalhePerfilPermissaoRecord(
                3L,
                1L,
                "Administrador",
                2L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                StatusEnum.ATIVO
        );

        when(service.detalhar(3L)).thenReturn(detalhe);

        mockMvc.perform(get("/perfil-permissao/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.idPerfil").value(1L))
                .andExpect(jsonPath("$.perfil").value("Administrador"))
                .andExpect(jsonPath("$.idPermissao").value(2L))
                .andExpect(jsonPath("$.permissao").value("Listar perfis"))
                .andExpect(jsonPath("$.chave").value("ACESSO_PERFIL_LISTAR"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve remover vinculo entre perfil e permissao e retornar status 204")
    void deveRemoverVinculoEntrePerfilEPermissaoERetornarStatus204() throws Exception {
        mockMvc.perform(delete("/perfil-permissao/3"))
                .andExpect(status().isNoContent());

        verify(service).excluir(3L);
    }

    private PerfilModel criarPerfil(Long id, String nome, String descricao) {
        var perfil = new PerfilModel(new PerfilRecord(nome, descricao));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }

    private PermissaoModel criarPermissao(Long id, String nome, String chave, String descricao) {
        var permissao = new PermissaoModel(new PermissaoRecord(nome, chave, descricao));
        ReflectionTestUtils.setField(permissao, "id", id);
        return permissao;
    }

    private PerfilPermissaoModel criarPerfilPermissao(Long id, PerfilModel perfil, PermissaoModel permissao) {
        var perfilPermissao = new PerfilPermissaoModel(perfil, permissao);
        ReflectionTestUtils.setField(perfilPermissao, "id", id);
        return perfilPermissao;
    }
}