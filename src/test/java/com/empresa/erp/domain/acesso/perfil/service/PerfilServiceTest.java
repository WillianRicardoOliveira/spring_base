package com.empresa.erp.domain.acesso.perfil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private PerfilRepository repository;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @InjectMocks
    private PerfilService service;

    private OrganizacaoModel organizacao;

    @BeforeEach
    void setUp() {
        organizacao = criarOrganizacao(
                ID_ORGANIZACAO,
                "Organizacao Teste"
        );
    }

    @Test
    @DisplayName(
            "Deve cadastrar perfil na organizacao ativa"
    )
    void deveCadastrarPerfilNaOrganizacaoAtiva() {
        PerfilRecord dados = new PerfilRecord(
                "  Financeiro  ",
                "  Perfil   financeiro  "
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        ID_ORGANIZACAO,
                        "Financeiro",
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(organizacaoRepository.getReferenceById(
                ID_ORGANIZACAO
        )).thenReturn(organizacao);

        when(repository.save(any(PerfilModel.class)))
                .thenAnswer(invocacao ->
                        invocacao.getArgument(0)
                );

        PerfilModel perfil =
                service.cadastrar(dados);

        assertThat(perfil.getOrganizacao())
                .isEqualTo(organizacao);

        assertThat(perfil.getNome())
                .isEqualTo("Financeiro");

        assertThat(perfil.getDescricao())
                .isEqualTo("Perfil financeiro");

        assertThat(perfil.getSistema())
                .isFalse();

        assertThat(perfil.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(perfil);
    }

    @Test
    @DisplayName(
            "Deve bloquear perfil duplicado na mesma organizacao"
    )
    void deveBloquearPerfilDuplicadoNaMesmaOrganizacao() {
        PerfilRecord dados = new PerfilRecord(
                "Financeiro",
                "Perfil financeiro"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        ID_ORGANIZACAO,
                        "Financeiro",
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil ja cadastrado.");

        verify(organizacaoRepository, never())
                .getReferenceById(any());

        verify(repository, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Deve listar perfis ativos da organizacao sem filtro"
    )
    void deveListarPerfisAtivosDaOrganizacaoSemFiltro() {
        var paginacao =
                PageRequest.of(0, 10);

        var perfil = criarPerfil(
                1L,
                "Administrador",
                "Perfil administrador"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findAllByOrganizacaoIdAndStatus(
                paginacao,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(
                new PageImpl<>(List.of(perfil))
        );

        var resultado =
                service.listar(paginacao, null);

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(1L);

        assertThat(resultado.getContent().get(0).nome())
                .isEqualTo("Administrador");

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve listar perfis ativos da organizacao com filtro"
    )
    void deveListarPerfisAtivosDaOrganizacaoComFiltro() {
        var paginacao =
                PageRequest.of(0, 10);

        var perfil = criarPerfil(
                2L,
                "Financeiro",
                "Perfil financeiro"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository
                .findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "fin",
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of(perfil))
        );

        var resultado =
                service.listar(paginacao, " fin ");

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(2L);

        assertThat(resultado.getContent().get(0).nome())
                .isEqualTo("Financeiro");
    }

    @Test
    @DisplayName(
            "Deve detalhar perfil ativo da organizacao"
    )
    void deveDetalharPerfilAtivoDaOrganizacao() {
        var perfil = criarPerfil(
                1L,
                "Administrador",
                "Perfil administrador"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                1L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(perfil));

        var resultado =
                service.detalhar(1L);

        assertThat(resultado.id())
                .isEqualTo(1L);

        assertThat(resultado.nome())
                .isEqualTo("Administrador");

        assertThat(resultado.descricao())
                .isEqualTo("Perfil administrador");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve impedir detalhar perfil fora da organizacao"
    )
    void deveImpedirDetalharPerfilForaDaOrganizacao() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                99L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(99L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve atualizar perfil da organizacao"
    )
    void deveAtualizarPerfilDaOrganizacao() {
        var dados = new AtualizaPerfilRecord(
                2L,
                "  Financeiro   Master  ",
                "  Perfil   atualizado  "
        );

        var perfil = criarPerfil(
                2L,
                "Financeiro",
                "Perfil financeiro"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                2L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(perfil));

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        ID_ORGANIZACAO,
                        "Financeiro Master",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(false);

        var resultado =
                service.atualizar(dados);

        assertThat(resultado.id())
                .isEqualTo(2L);

        assertThat(resultado.nome())
                .isEqualTo("Financeiro Master");

        assertThat(resultado.descricao())
                .isEqualTo("Perfil atualizado");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear nome duplicado ao atualizar perfil"
    )
    void deveBloquearNomeDuplicadoAoAtualizarPerfil() {
        var dados = new AtualizaPerfilRecord(
                2L,
                "Financeiro",
                "Perfil financeiro"
        );

        var perfil = criarPerfil(
                2L,
                "Compras",
                "Perfil de compras"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                2L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(perfil));

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        ID_ORGANIZACAO,
                        "Financeiro",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.atualizar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Perfil ja cadastrado.");
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao de perfil fora da organizacao"
    )
    void deveBloquearAtualizacaoDePerfilForaDaOrganizacao() {
        var dados = new AtualizaPerfilRecord(
                99L,
                "Financeiro",
                "Perfil financeiro"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                99L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.atualizar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao de perfil critico"
    )
    void deveBloquearAtualizacaoDePerfilCritico() {
        var dados = new AtualizaPerfilRecord(
                2L,
                "Administrador Master",
                "Perfil atualizado"
        );

        var perfil = criarPerfil(
                2L,
                "Administrador",
                "Perfil administrador"
        );

        ReflectionTestUtils.setField(
                perfil,
                "sistema",
                true
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                2L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(perfil));

        assertThatThrownBy(() ->
                service.atualizar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil critico do sistema nao pode ser alterado."
                );

        verify(repository, never())
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName(
            "Deve remover perfil da organizacao com auditoria"
    )
    void deveRemoverPerfilDaOrganizacaoComAuditoria() {
        var perfil = criarPerfil(
                2L,
                "Financeiro",
                "Perfil financeiro"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                2L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(perfil));

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(2L);

        assertThat(perfil.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(perfil.getRemovidoEm())
                .isNotNull();

        assertThat(perfil.getRemovidoPor())
                .isEqualTo(10L);
    }

    @Test
    @DisplayName(
            "Deve bloquear remocao de perfil critico"
    )
    void deveBloquearRemocaoDePerfilCritico() {
        var perfil = criarPerfil(
                2L,
                "Administrador",
                "Perfil administrador"
        );

        ReflectionTestUtils.setField(
                perfil,
                "sistema",
                true
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(repository.findByIdAndOrganizacaoIdAndStatus(
                2L,
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(perfil));

        assertThatThrownBy(() ->
                service.excluir(2L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Perfil critico do sistema nao pode ser alterado."
                );

        verify(usuarioLogadoService, never())
                .getId();
    }

    private PerfilModel criarPerfil(
            Long id,
            String nome,
            String descricao
    ) {
        var perfil = new PerfilModel(
                organizacao,
                new PerfilRecord(nome, descricao)
        );

        ReflectionTestUtils.setField(
                perfil,
                "id",
                id
        );

        return perfil;
    }

    private OrganizacaoModel criarOrganizacao(
            Long id,
            String nome
    ) {
        var organizacaoCriada =
                new OrganizacaoModel(nome);

        ReflectionTestUtils.setField(
                organizacaoCriada,
                "id",
                id
        );

        return organizacaoCriada;
    }
}