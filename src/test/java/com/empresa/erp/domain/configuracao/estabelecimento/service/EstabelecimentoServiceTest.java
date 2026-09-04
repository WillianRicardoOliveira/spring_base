package com.empresa.erp.domain.configuracao.estabelecimento.service;

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
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.repository.UsuarioEstabelecimentoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.record.AtualizaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.EstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.repository.EstabelecimentoRepository;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

@ExtendWith(MockitoExtension.class)
class EstabelecimentoServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private EstabelecimentoRepository repository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @InjectMocks
    private EstabelecimentoService service;

    @BeforeEach
    void setUp() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve cadastrar estabelecimento na organizacao atual"
    )
    void deveCadastrarEstabelecimentoNaOrganizacaoAtual() {
        var empresa = criarEmpresa(1L);

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                        empresa,
                        "Filial Curitiba",
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(repository.save(
                any(EstabelecimentoModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        var resultado = service.cadastrar(
                new EstabelecimentoRecord(
                        1L,
                        "  Filial   Curitiba  "
                )
        );

        assertThat(resultado.getEmpresa())
                .isSameAs(empresa);

        assertThat(resultado.getNome())
                .isEqualTo("Filial Curitiba");

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro para empresa fora da organizacao"
    )
    void deveBloquearCadastroParaEmpresaForaDaOrganizacao() {
        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new EstabelecimentoRecord(
                                1L,
                                "Filial Curitiba"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa nao encontrada ou removida."
                );

        verify(repository, never())
                .save(any(EstabelecimentoModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear nome duplicado na mesma empresa"
    )
    void deveBloquearNomeDuplicadoNaMesmaEmpresa() {
        var empresa = criarEmpresa(1L);

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                        empresa,
                        "Filial Curitiba",
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(
                        new EstabelecimentoRecord(
                                1L,
                                "Filial Curitiba"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Estabelecimento ja cadastrado para esta empresa."
                );

        verify(repository, never())
                .save(any(EstabelecimentoModel.class));
    }

    @Test
    @DisplayName(
            "Deve listar estabelecimentos da organizacao sem filtros"
    )
    void deveListarEstabelecimentosDaOrganizacaoSemFiltros() {
        var paginacao = PageRequest.of(0, 10);
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(estabelecimento)
                )
        );

        var resultado = service.listar(
                paginacao,
                null,
                null
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        verify(repository)
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve considerar filtro em branco como ausente"
    )
    void deveConsiderarFiltroEmBrancoComoAusente() {
        var paginacao = PageRequest.of(0, 10);

        when(repository
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                null,
                "   "
        );

        verify(repository)
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar por empresa e organizacao"
    )
    void deveListarPorEmpresaEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);

        when(repository
                .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                1L,
                null
        );

        verify(repository)
                .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar por nome e organizacao"
    )
    void deveListarPorNomeEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);

        when(repository
                .findByEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "Curitiba",
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                null,
                "  Curitiba  "
        );

        verify(repository)
                .findByEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "Curitiba",
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar por empresa nome e organizacao"
    )
    void deveListarPorEmpresaNomeEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);

        when(repository
                .findByEmpresaIdAndEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        1L,
                        ID_ORGANIZACAO,
                        "Curitiba",
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                1L,
                "  Curitiba  "
        );

        verify(repository)
                .findByEmpresaIdAndEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        1L,
                        ID_ORGANIZACAO,
                        "Curitiba",
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar estabelecimento da organizacao atual"
    )
    void deveDetalharEstabelecimentoDaOrganizacaoAtual() {
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        var resultado = service.detalhar(2L);

        assertThat(resultado.id())
                .isEqualTo(2L);

        assertThat(resultado.idEmpresa())
                .isEqualTo(1L);

        assertThat(resultado.nome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Deve bloquear detalhe de outra organizacao"
    )
    void deveBloquearDetalheDeOutraOrganizacao() {
        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(2L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Estabelecimento nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve atualizar estabelecimento da organizacao"
    )
    void deveAtualizarEstabelecimentoDaOrganizacao() {
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        estabelecimento.getEmpresa(),
                        "Filial Atualizada",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaEstabelecimentoRecord(
                        2L,
                        "  Filial   Atualizada  "
                )
        );

        assertThat(resultado.nome())
                .isEqualTo("Filial Atualizada");
    }

    @Test
    @DisplayName(
            "Deve permitir atualizacao mantendo o mesmo nome"
    )
    void devePermitirAtualizacaoMantendoMesmoNome() {
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        estabelecimento.getEmpresa(),
                        "Filial Curitiba",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaEstabelecimentoRecord(
                        2L,
                        "Filial Curitiba"
                )
        );

        assertThat(resultado.nome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao duplicada"
    )
    void deveBloquearAtualizacaoDuplicada() {
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        estabelecimento.getEmpresa(),
                        "Matriz",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaEstabelecimentoRecord(
                                2L,
                                "Matriz"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Estabelecimento ja cadastrado para esta empresa."
                );

        assertThat(estabelecimento.getNome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao fora da organizacao"
    )
    void deveBloquearAtualizacaoForaDaOrganizacao() {
        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaEstabelecimentoRecord(
                                2L,
                                "Filial Atualizada"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Estabelecimento nao encontrado ou removido."
                );

        verify(repository, never())
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName(
            "Deve remover estabelecimento da organizacao com auditoria"
    )
    void deveRemoverEstabelecimentoDaOrganizacaoComAuditoria() {
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(usuarioEstabelecimentoRepository
                .existsByEstabelecimentoIdAndStatus(
                        2L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(2L);

        assertThat(estabelecimento.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(estabelecimento.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(estabelecimento.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao com usuarios vinculados"
    )
    void deveBloquearExclusaoComUsuariosVinculados() {
        var estabelecimento = criarEstabelecimento();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(usuarioEstabelecimentoRepository
                .existsByEstabelecimentoIdAndStatus(
                        2L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.excluir(2L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Estabelecimento possui usuarios vinculados "
                                + "e nao pode ser removido."
                );

        assertThat(estabelecimento.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(usuarioLogadoService, never())
                .getId();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao fora da organizacao"
    )
    void deveBloquearExclusaoForaDaOrganizacao() {
        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(2L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Estabelecimento nao encontrado ou removido."
                );

        verify(usuarioEstabelecimentoRepository, never())
                .existsByEstabelecimentoIdAndStatus(
                        any(),
                        any()
                );

        verify(usuarioLogadoService, never())
                .getId();
    }

    private EmpresaModel criarEmpresa(Long id) {
        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                ID_ORGANIZACAO
        );

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                id
        );

        return empresa;
    }

    private EstabelecimentoModel criarEstabelecimento() {
        var estabelecimento = new EstabelecimentoModel(
                criarEmpresa(1L),
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                estabelecimento,
                "id",
                2L
        );

        return estabelecimento;
    }
}