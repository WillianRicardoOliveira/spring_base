package com.empresa.erp.domain.plataforma.organizacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.plataforma.organizacao.record.OrganizacaoRecord;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class OrganizacaoPlataformaServiceTest {

    @Mock
    private OrganizacaoRepository repository;

    @Mock
    private UsuarioLogadoService
            usuarioLogadoService;

    @InjectMocks
    private OrganizacaoPlataformaService service;

    @Test
    @DisplayName(
            "Deve listar organizações sem filtro"
    )
    void deveListarOrganizacoesSemFiltro() {
        var paginacao =
                PageRequest.of(0, 10);

        var organizacaoA =
                criarOrganizacao(
                        10L,
                        "Organização A"
                );

        var organizacaoB =
                criarOrganizacao(
                        20L,
                        "Organização B"
                );

        when(repository
                .findByNomeContainingIgnoreCase(
                        paginacao,
                        ""
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(
                                organizacaoA,
                                organizacaoB
                        ),
                        paginacao,
                        2
                )
        );

        var resultado =
                service.listar(
                        paginacao,
                        null
                );

        assertThat(resultado.getContent())
                .hasSize(2);

        assertThat(resultado.getContent())
                .extracting(
                        organizacao ->
                                organizacao.nome()
                )
                .containsExactly(
                        "Organização A",
                        "Organização B"
                );

        verify(repository)
                .findByNomeContainingIgnoreCase(
                        paginacao,
                        ""
                );
    }

    @Test
    @DisplayName(
            "Deve normalizar filtro da listagem"
    )
    void deveNormalizarFiltroDaListagem() {
        var paginacao =
                PageRequest.of(0, 10);

        when(repository
                .findByNomeContainingIgnoreCase(
                        paginacao,
                        "Empresa"
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(),
                        paginacao,
                        0
                )
        );

        var resultado =
                service.listar(
                        paginacao,
                        "  Empresa  "
                );

        assertThat(resultado).isEmpty();

        verify(repository)
                .findByNomeContainingIgnoreCase(
                        paginacao,
                        "Empresa"
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar organização"
    )
    void deveDetalharOrganizacao() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Organização A"
                );

        when(repository.findById(10L))
                .thenReturn(
                        Optional.of(organizacao)
                );

        var resultado =
                service.detalhar(10L);

        assertThat(resultado.id())
                .isEqualTo(10L);

        assertThat(resultado.nome())
                .isEqualTo("Organização A");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);
        
        assertThat(resultado.auditoria())
        		.isNotNull();

        verify(repository).findById(10L);
    }

    @Test
    @DisplayName(
            "Deve rejeitar detalhamento de organização inexistente"
    )
    void deveRejeitarDetalhamentoDeOrganizacaoInexistente() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(99L)
        ).isInstanceOf(
                EntityNotFoundException.class
        );

        verify(repository).findById(99L);
    }

    @Test
    @DisplayName(
            "Deve editar e normalizar nome da organização"
    )
    void deveEditarENormalizarNomeDaOrganizacao() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Nome anterior"
                );

        prepararBuscaParaAtualizacao(
                10L,
                organizacao
        );

        var resultado =
                service.editar(
                        10L,
                        new OrganizacaoRecord(
                                "  Novo   nome  "
                        )
                );

        assertThat(resultado.id())
                .isEqualTo(10L);

        assertThat(resultado.nome())
                .isEqualTo("Novo nome");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(organizacao.getNome())
                .isEqualTo("Novo nome");

        verificarBuscaParaAtualizacao(10L);
    }

    @Test
    @DisplayName(
            "Deve rejeitar edição de organização removida "
                    + "ou inexistente"
    )
    void deveRejeitarEdicaoDeOrganizacaoRemovidaOuInexistente() {
        when(repository
                .buscarPorIdNaoRemovidoParaAtualizacao(
                        99L,
                        StatusEnum.REMOVIDO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.editar(
                        99L,
                        new OrganizacaoRecord(
                                "Organização"
                        )
                )
        ).isInstanceOf(
                EntityNotFoundException.class
        );

        verificarBuscaParaAtualizacao(99L);
    }

    @Test
    @DisplayName(
            "Deve inativar organização ativa"
    )
    void deveInativarOrganizacaoAtiva() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Organização"
                );

        prepararBuscaParaAtualizacao(
                10L,
                organizacao
        );

        var resultado =
                service.inativar(10L);

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.INATIVO);

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.INATIVO);

        verificarBuscaParaAtualizacao(10L);
    }

    @Test
    @DisplayName(
            "Deve manter organização já inativa"
    )
    void deveManterOrganizacaoJaInativa() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Organização"
                );

        organizacao.inativar();

        prepararBuscaParaAtualizacao(
                10L,
                organizacao
        );

        var resultado =
                service.inativar(10L);

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.INATIVO);

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.INATIVO);

        verificarBuscaParaAtualizacao(10L);
    }

    @Test
    @DisplayName(
            "Deve reativar organização inativa"
    )
    void deveReativarOrganizacaoInativa() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Organização"
                );

        organizacao.inativar();

        prepararBuscaParaAtualizacao(
                10L,
                organizacao
        );

        var resultado =
                service.reativar(10L);

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verificarBuscaParaAtualizacao(10L);
    }

    @Test
    @DisplayName(
            "Deve manter organização já ativa"
    )
    void deveManterOrganizacaoJaAtiva() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Organização"
                );

        prepararBuscaParaAtualizacao(
                10L,
                organizacao
        );

        var resultado =
                service.reativar(10L);

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verificarBuscaParaAtualizacao(10L);
    }

    @Test
    @DisplayName(
            "Deve remover organização logicamente"
    )
    void deveRemoverOrganizacaoLogicamente() {
        var organizacao =
                criarOrganizacao(
                        10L,
                        "Organização"
                );

        prepararBuscaParaAtualizacao(
                10L,
                organizacao
        );

        when(usuarioLogadoService.getId())
                .thenReturn(30L);

        service.remover(10L);

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(organizacao.getRemovidoPor())
                .isEqualTo(30L);

        assertThat(organizacao.getRemovidoEm())
                .isNotNull();

        verify(usuarioLogadoService).getId();

        verificarBuscaParaAtualizacao(10L);
    }

    @Test
    @DisplayName(
            "Não deve consultar usuário ao rejeitar remoção"
    )
    void naoDeveConsultarUsuarioAoRejeitarRemocao() {
        when(repository
                .buscarPorIdNaoRemovidoParaAtualizacao(
                        99L,
                        StatusEnum.REMOVIDO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.remover(99L)
        ).isInstanceOf(
                EntityNotFoundException.class
        );

        verifyNoInteractions(
                usuarioLogadoService
        );

        verificarBuscaParaAtualizacao(99L);
    }

    private OrganizacaoModel criarOrganizacao(
            Long id,
            String nome
    ) {
        var organizacao =
                new OrganizacaoModel(nome);

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                id
        );

        return organizacao;
    }

    private void prepararBuscaParaAtualizacao(
            Long id,
            OrganizacaoModel organizacao
    ) {
        when(repository
                .buscarPorIdNaoRemovidoParaAtualizacao(
                        id,
                        StatusEnum.REMOVIDO
                )
        ).thenReturn(
                Optional.of(organizacao)
        );
    }

    private void verificarBuscaParaAtualizacao(
            Long id
    ) {
        verify(repository)
                .buscarPorIdNaoRemovidoParaAtualizacao(
                        id,
                        StatusEnum.REMOVIDO
                );
    }
}