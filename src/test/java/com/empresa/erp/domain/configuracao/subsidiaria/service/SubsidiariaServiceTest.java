package com.empresa.erp.domain.configuracao.subsidiaria.service;

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
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.record.AtualizaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.SubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

@ExtendWith(MockitoExtension.class)
class SubsidiariaServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private SubsidiariaRepository repository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioSubsidiariaRepository
            usuarioSubsidiariaRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @InjectMocks
    private SubsidiariaService service;

    @BeforeEach
    void setUp() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve cadastrar subsidiaria na organizacao atual"
    )
    void deveCadastrarSubsidiariaNaOrganizacaoAtual() {
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
                any(SubsidiariaModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        var resultado = service.cadastrar(
                new SubsidiariaRecord(
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
                        new SubsidiariaRecord(
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
                .save(any(SubsidiariaModel.class));
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
                        new SubsidiariaRecord(
                                1L,
                                "Filial Curitiba"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Subsidiaria ja cadastrada para esta empresa."
                );

        verify(repository, never())
                .save(any(SubsidiariaModel.class));
    }

    @Test
    @DisplayName(
            "Deve listar subsidiarias da organizacao sem filtros"
    )
    void deveListarSubsidiariasDaOrganizacaoSemFiltros() {
        var paginacao = PageRequest.of(0, 10);
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(subsidiaria)
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
            "Deve detalhar subsidiaria da organizacao atual"
    )
    void deveDetalharSubsidiariaDaOrganizacaoAtual() {
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

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
                        "Subsidiaria nao encontrada ou removida."
                );
    }

    @Test
    @DisplayName(
            "Deve atualizar subsidiaria da organizacao"
    )
    void deveAtualizarSubsidiariaDaOrganizacao() {
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        subsidiaria.getEmpresa(),
                        "Filial Atualizada",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaSubsidiariaRecord(
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
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        subsidiaria.getEmpresa(),
                        "Filial Curitiba",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaSubsidiariaRecord(
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
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        subsidiaria.getEmpresa(),
                        "Matriz",
                        StatusEnum.ATIVO,
                        2L
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaSubsidiariaRecord(
                                2L,
                                "Matriz"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Subsidiaria ja cadastrada para esta empresa."
                );

        assertThat(subsidiaria.getNome())
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
                        new AtualizaSubsidiariaRecord(
                                2L,
                                "Filial Atualizada"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Subsidiaria nao encontrada ou removida."
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
            "Deve remover subsidiaria da organizacao com auditoria"
    )
    void deveRemoverSubsidiariaDaOrganizacaoComAuditoria() {
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(usuarioSubsidiariaRepository
                .existsBySubsidiariaIdAndStatus(
                        2L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(2L);

        assertThat(subsidiaria.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(subsidiaria.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(subsidiaria.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao com usuarios vinculados"
    )
    void deveBloquearExclusaoComUsuariosVinculados() {
        var subsidiaria = criarSubsidiaria();

        when(repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(usuarioSubsidiariaRepository
                .existsBySubsidiariaIdAndStatus(
                        2L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.excluir(2L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Subsidiaria possui usuarios vinculados "
                                + "e nao pode ser removida."
                );

        assertThat(subsidiaria.getStatus())
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
                        "Subsidiaria nao encontrada ou removida."
                );

        verify(usuarioSubsidiariaRepository, never())
                .existsBySubsidiariaIdAndStatus(
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

    private SubsidiariaModel criarSubsidiaria() {
        var subsidiaria = new SubsidiariaModel(
                criarEmpresa(1L),
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                subsidiaria,
                "id",
                2L
        );

        return subsidiaria;
    }
}