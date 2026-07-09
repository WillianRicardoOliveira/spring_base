package com.empresa.erp.domain.acesso.perfil.service;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private PerfilRepository repository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @InjectMocks
    private PerfilService service;

    @Test
    @DisplayName("Deve cadastrar perfil quando nome ainda nao existe")
    void deveCadastrarPerfilQuandoNomeNaoExiste() {
        PerfilRecord dados = new PerfilRecord("Financeiro", "Perfil financeiro");

        when(repository.existsByNomeIgnoreCaseAndStatus("Financeiro", StatusEnum.ATIVO))
                .thenReturn(false);

        PerfilModel perfil = service.cadastrar(dados);

        assertThat(perfil.getNome()).isEqualTo("Financeiro");
        assertThat(perfil.getDescricao()).isEqualTo("Perfil financeiro");
        assertThat(perfil.getStatus()).isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(perfil);
    }

    @Test
    @DisplayName("Deve bloquear cadastro de perfil duplicado ativo")
    void deveBloquearCadastroDePerfilDuplicadoAtivo() {
        PerfilRecord dados = new PerfilRecord("Financeiro", "Perfil financeiro");

        when(repository.existsByNomeIgnoreCaseAndStatus("Financeiro", StatusEnum.ATIVO))
                .thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil ja cadastrado.");
    }

    @Test
    @DisplayName("Deve listar perfis ativos sem filtro")
    void deveListarPerfisAtivosSemFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");

        when(repository.findAllByStatus(paginacao, StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(perfil)));

        var resultado = service.listar(paginacao, null);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(1L);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Administrador");
        assertThat(resultado.getContent().get(0).status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve listar perfis ativos com filtro")
    void deveListarPerfisAtivosComFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var perfil = criarPerfil(2L, "Financeiro", "Perfil financeiro");

        when(repository.findByNomeContainingIgnoreCaseAndStatus(paginacao, "fin", StatusEnum.ATIVO))
                .thenReturn(new PageImpl<>(List.of(perfil)));

        var resultado = service.listar(paginacao, "fin");

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(2L);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Financeiro");
    }

    @Test
    @DisplayName("Deve detalhar perfil")
    void deveDetalharPerfil() {
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");

        when(repository.getReferenceById(1L)).thenReturn(perfil);

        var resultado = service.detalhar(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Administrador");
        assertThat(resultado.descricao()).isEqualTo("Perfil administrador");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar perfil quando nome nao esta duplicado")
    void deveAtualizarPerfilQuandoNomeNaoEstaDuplicado() {
        var dados = new AtualizaPerfilRecord(1L, "Administrador Master", "Perfil atualizado");
        var perfil = criarPerfil(1L, "Administrador", "Perfil administrador");

        when(repository.existsByNomeIgnoreCaseAndStatusAndIdNot("Administrador Master", StatusEnum.ATIVO, 1L))
                .thenReturn(false);

        when(repository.findByIdAndStatus(1L, StatusEnum.ATIVO)).thenReturn(Optional.of(perfil));

        var resultado = service.atualizar(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Administrador Master");
        assertThat(resultado.descricao()).isEqualTo("Perfil atualizado");
        assertThat(resultado.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve bloquear atualizacao quando nome pertence a outro perfil ativo")
    void deveBloquearAtualizacaoQuandoNomePertenceAOutroPerfilAtivo() {
        var dados = new AtualizaPerfilRecord(1L, "Financeiro", "Perfil financeiro");

        when(repository.existsByNomeIgnoreCaseAndStatusAndIdNot("Financeiro", StatusEnum.ATIVO, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil ja cadastrado.");
    }

    @Test
    @DisplayName("Deve bloquear atualizacao de perfil removido")
    void deveBloquearAtualizacaoDePerfilRemovido() {
        var dados = new AtualizaPerfilRecord(1L, "Administrador Master", "Perfil atualizado");

        when(repository.existsByNomeIgnoreCaseAndStatusAndIdNot("Administrador Master", StatusEnum.ATIVO, 1L))
                .thenReturn(false);

        when(repository.findByIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil nao encontrado ou removido.");
    }

    @Test
    @DisplayName("Deve remover perfil com auditoria")
    void deveRemoverPerfilComAuditoria() {
        var perfil = criarPerfil(1L, "Financeiro", "Perfil financeiro");

        when(usuarioLogadoService.getId()).thenReturn(10L);
        when(repository.getReferenceById(1L)).thenReturn(perfil);

        service.excluir(1L);

        assertThat(perfil.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(perfil.getRemovidoEm()).isNotNull();
        assertThat(perfil.getRemovidoPor()).isEqualTo(10L);
    }

    private PerfilModel criarPerfil(Long id, String nome, String descricao) {
        var perfil = new PerfilModel(new PerfilRecord(nome, descricao));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }
}