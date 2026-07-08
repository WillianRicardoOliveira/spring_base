package com.empresa.erp.domain.acesso.perfilPermissao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;

@ExtendWith(MockitoExtension.class)
class PerfilPermissaoServiceTest {

    @Mock
    private PerfilPermissaoRepository repository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @InjectMocks
    private PerfilPermissaoService service;

    @Test
    @DisplayName("Deve vincular permissao ao perfil quando vinculo ainda nao existe")
    void deveVincularPermissaoAoPerfilQuandoVinculoAindaNaoExiste() {
        var dados = new PerfilPermissaoRecord(1L, 2L);
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        var permissao = criarPermissao(2L, "Listar perfis", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");

        when(perfilRepository.getReferenceById(1L)).thenReturn(perfil);
        when(permissaoRepository.getReferenceById(2L)).thenReturn(permissao);
        when(repository.existsByPerfilAndPermissaoAndStatus(perfil, permissao, StatusEnum.ATIVO))
                .thenReturn(false);

        PerfilPermissaoModel perfilPermissao = service.cadastrar(dados);

        assertThat(perfilPermissao.getPerfil()).isEqualTo(perfil);
        assertThat(perfilPermissao.getPermissao()).isEqualTo(permissao);
        assertThat(perfilPermissao.getStatus()).isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(perfilPermissao);
    }

    @Test
    @DisplayName("Deve bloquear vinculo duplicado ativo entre perfil e permissao")
    void deveBloquearVinculoDuplicadoAtivoEntrePerfilEPermissao() {
        var dados = new PerfilPermissaoRecord(1L, 2L);
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        var permissao = criarPermissao(2L, "Listar perfis", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");

        when(perfilRepository.getReferenceById(1L)).thenReturn(perfil);
        when(permissaoRepository.getReferenceById(2L)).thenReturn(permissao);
        when(repository.existsByPerfilAndPermissaoAndStatus(perfil, permissao, StatusEnum.ATIVO))
                .thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Permissao ja vinculada ao perfil.");
    }

    @Test
    @DisplayName("Deve listar permissoes vinculadas ao perfil")
    void deveListarPermissoesVinculadasAoPerfil() {
        var paginacao = PageRequest.of(0, 10);
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        var permissao = criarPermissao(2L, "Listar perfis", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        when(repository.findAllByPerfilIdAndStatus(paginacao, 1L, StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(perfilPermissao)));

        var resultado = service.listarPorPerfil(paginacao, 1L);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(3L);
        assertThat(resultado.getContent().get(0).idPermissao()).isEqualTo(2L);
        assertThat(resultado.getContent().get(0).permissao()).isEqualTo("Listar perfis");
        assertThat(resultado.getContent().get(0).chave()).isEqualTo("ACESSO_PERFIL_LISTAR");
        assertThat(resultado.getContent().get(0).status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve detalhar vinculo entre perfil e permissao")
    void deveDetalharVinculoEntrePerfilEPermissao() {
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        var permissao = criarPermissao(2L, "Listar perfis", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        when(repository.getReferenceById(3L)).thenReturn(perfilPermissao);

        var resultado = service.detalhar(3L);

        assertThat(resultado.id()).isEqualTo(3L);
        assertThat(resultado.idPerfil()).isEqualTo(1L);
        assertThat(resultado.perfil()).isEqualTo("Administrador");
        assertThat(resultado.idPermissao()).isEqualTo(2L);
        assertThat(resultado.permissao()).isEqualTo("Listar perfis");
        assertThat(resultado.chave()).isEqualTo("ACESSO_PERFIL_LISTAR");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo entre perfil e permissao com auditoria")
    void deveRemoverVinculoEntrePerfilEPermissaoComAuditoria() {
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");
        var permissao = criarPermissao(2L, "Listar perfis", "ACESSO_PERFIL_LISTAR", "Permite listar perfis");
        var perfilPermissao = criarPerfilPermissao(3L, perfil, permissao);

        when(usuarioLogadoService.getId()).thenReturn(10L);
        when(repository.getReferenceById(3L)).thenReturn(perfilPermissao);

        service.excluir(3L);

        assertThat(perfilPermissao.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(perfilPermissao.getRemovidoEm()).isNotNull();
        assertThat(perfilPermissao.getRemovidoPor()).isEqualTo(10L);
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