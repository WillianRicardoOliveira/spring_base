package com.empresa.erp.domain.acesso.permissao.service;

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
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;

@ExtendWith(MockitoExtension.class)
class PermissaoServiceTest {

    @Mock
    private PermissaoRepository repository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @InjectMocks
    private PermissaoService service;

    @Test
    @DisplayName("Deve cadastrar permissao quando chave ainda nao existe")
    void deveCadastrarPermissaoQuandoChaveNaoExiste() {
        var dados = new PermissaoRecord(
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(repository.existsByChaveIgnoreCaseAndStatus("ACESSO_PERFIL_LISTAR", StatusEnum.ATIVO))
                .thenReturn(false);

        PermissaoModel permissao = service.cadastrar(dados);

        assertThat(permissao.getNome()).isEqualTo("Listar perfis");
        assertThat(permissao.getChave()).isEqualTo("ACESSO_PERFIL_LISTAR");
        assertThat(permissao.getDescricao()).isEqualTo("Permite listar perfis");
        assertThat(permissao.getStatus()).isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(permissao);
    }

    @Test
    @DisplayName("Deve bloquear cadastro de permissao duplicada ativa")
    void deveBloquearCadastroDePermissaoDuplicadaAtiva() {
        var dados = new PermissaoRecord(
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(repository.existsByChaveIgnoreCaseAndStatus("ACESSO_PERFIL_LISTAR", StatusEnum.ATIVO))
                .thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Permissao ja cadastrada.");
    }

    @Test
    @DisplayName("Deve listar permissoes ativas sem filtro")
    void deveListarPermissoesAtivasSemFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(repository.findAllByStatus(paginacao, StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(permissao)));

        var resultado = service.listar(paginacao, null);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(1L);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Listar perfis");
        assertThat(resultado.getContent().get(0).chave()).isEqualTo("ACESSO_PERFIL_LISTAR");
        assertThat(resultado.getContent().get(0).status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve listar permissoes ativas com filtro")
    void deveListarPermissoesAtivasComFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var permissao = criarPermissao(
                2L,
                "Criar perfis",
                "ACESSO_PERFIL_CRIAR",
                "Permite criar perfis"
        );

        when(repository.findByNomeContainingIgnoreCaseAndStatus(paginacao, "criar", StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(permissao)));

        var resultado = service.listar(paginacao, "criar");

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(2L);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Criar perfis");
        assertThat(resultado.getContent().get(0).chave()).isEqualTo("ACESSO_PERFIL_CRIAR");
    }

    @Test
    @DisplayName("Deve detalhar permissao")
    void deveDetalharPermissao() {
        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(repository.getReferenceById(1L)).thenReturn(permissao);

        var resultado = service.detalhar(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Listar perfis");
        assertThat(resultado.chave()).isEqualTo("ACESSO_PERFIL_LISTAR");
        assertThat(resultado.descricao()).isEqualTo("Permite listar perfis");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar permissao quando chave nao esta duplicada")
    void deveAtualizarPermissaoQuandoChaveNaoEstaDuplicada() {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar perfil",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfil"
        );

        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR_ANTIGA",
                "Permite listar perfis"
        );

        when(repository.existsByChaveIgnoreCaseAndStatusAndIdNot("ACESSO_PERFIL_LISTAR", StatusEnum.ATIVO, 1L))
                .thenReturn(false);

        when(repository.getReferenceById(1L)).thenReturn(permissao);

        var resultado = service.atualizar(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Listar perfil");
        assertThat(resultado.chave()).isEqualTo("ACESSO_PERFIL_LISTAR");
        assertThat(resultado.descricao()).isEqualTo("Permite listar perfil");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve bloquear atualizacao quando chave pertence a outra permissao ativa")
    void deveBloquearAtualizacaoQuandoChavePertenceAOutraPermissaoAtiva() {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(repository.existsByChaveIgnoreCaseAndStatusAndIdNot("ACESSO_PERFIL_LISTAR", StatusEnum.ATIVO, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Permissao ja cadastrada.");
    }

    @Test
    @DisplayName("Deve remover permissao com auditoria")
    void deveRemoverPermissaoComAuditoria() {
        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis"
        );

        when(usuarioLogadoService.getId()).thenReturn(10L);
        when(repository.getReferenceById(1L)).thenReturn(permissao);

        service.excluir(1L);

        assertThat(permissao.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(permissao.getRemovidoEm()).isNotNull();
        assertThat(permissao.getRemovidoPor()).isEqualTo(10L);
    }

    private PermissaoModel criarPermissao(Long id, String nome, String chave, String descricao) {
        var permissao = new PermissaoModel(new PermissaoRecord(nome, chave, descricao));
        ReflectionTestUtils.setField(permissao, "id", id);
        return permissao;
    }
}