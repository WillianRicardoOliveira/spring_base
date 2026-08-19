package com.empresa.erp.domain.configuracao.empresa.service;

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
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private EmpresaRepository repository;

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private SubsidiariaRepository
            subsidiariaRepository;

    @Mock
    private UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    @Mock
    private UsuarioLogadoService
            usuarioLogadoService;

    @Mock
    private ContextoOrganizacao
            contextoOrganizacao;

    @InjectMocks
    private EmpresaService service;

    @BeforeEach
    void setUp() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve cadastrar empresa vinculada a organizacao do contexto"
    )
    void deveCadastrarEmpresaVinculadaAOrganizacaoDoContexto() {
        var organizacao = criarOrganizacao(
                ID_ORGANIZACAO,
                "Organizacao Principal"
        );

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        ID_ORGANIZACAO,
                        "Empresa Exemplo",
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(organizacaoRepository.getReferenceById(
                ID_ORGANIZACAO
        )).thenReturn(organizacao);

        when(repository.save(any(EmpresaModel.class)))
                .thenAnswer(
                        invocacao -> invocacao.getArgument(0)
                );

        var resultado = service.cadastrar(
                new EmpresaRecord(
                        "  Empresa   Exemplo  "
                )
        );

        assertThat(resultado.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(resultado.getNome())
                .isEqualTo("Empresa Exemplo");

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository)
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        ID_ORGANIZACAO,
                        "Empresa Exemplo",
                        StatusEnum.ATIVO
                );

        verify(organizacaoRepository)
                .getReferenceById(ID_ORGANIZACAO);

        verify(repository)
                .save(any(EmpresaModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro duplicado somente na organizacao atual"
    )
    void deveBloquearCadastroDuplicadoSomenteNaOrganizacaoAtual() {
        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        ID_ORGANIZACAO,
                        "Empresa Exemplo",
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(
                        new EmpresaRecord(
                                "Empresa Exemplo"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Empresa ja cadastrada.");

        verify(organizacaoRepository, never())
                .getReferenceById(any());

        verify(repository, never())
                .save(any(EmpresaModel.class));
    }

    @Test
    @DisplayName(
            "Deve listar empresas ativas da organizacao sem filtro"
    )
    void deveListarEmpresasAtivasDaOrganizacaoSemFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findAllByOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of(empresa))
        );

        var resultado = service.listar(
                paginacao,
                null
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(1L);

        assertThat(resultado.getContent().get(0).nome())
                .isEqualTo("Empresa Exemplo");

        verify(repository)
                .findAllByOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar empresas da organizacao com filtro"
    )
    void deveListarEmpresasDaOrganizacaoComFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "Empresa",
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of(empresa))
        );

        var resultado = service.listar(
                paginacao,
                "  Empresa  "
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        verify(repository)
                .findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "Empresa",
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve considerar filtro em branco como ausencia de filtro"
    )
    void deveConsiderarFiltroEmBrancoComoAusenciaDeFiltro() {
        var paginacao = PageRequest.of(0, 10);

        when(repository
                .findAllByOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                "   "
        );

        verify(repository)
                .findAllByOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar empresa ativa da organizacao atual"
    )
    void deveDetalharEmpresaAtivaDaOrganizacaoAtual() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        var resultado = service.detalhar(1L);

        assertThat(resultado.id())
                .isEqualTo(1L);

        assertThat(resultado.nome())
                .isEqualTo("Empresa Exemplo");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve rejeitar empresa inexistente ou de outra organizacao"
    )
    void deveRejeitarEmpresaInexistenteOuDeOutraOrganizacao() {
        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa nao encontrada ou removida."
                );
    }

    @Test
    @DisplayName(
            "Deve atualizar empresa da organizacao e normalizar nome"
    )
    void deveAtualizarEmpresaDaOrganizacaoENormalizarNome() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        ID_ORGANIZACAO,
                        "Empresa Atualizada",
                        StatusEnum.ATIVO,
                        1L
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaEmpresaRecord(
                        1L,
                        "  Empresa   Atualizada  "
                )
        );

        assertThat(resultado.nome())
                .isEqualTo("Empresa Atualizada");

        assertThat(empresa.getOrganizacao().getId())
                .isEqualTo(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve permitir atualizacao mantendo o mesmo nome"
    )
    void devePermitirAtualizacaoMantendoMesmoNome() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        ID_ORGANIZACAO,
                        "Empresa Exemplo",
                        StatusEnum.ATIVO,
                        1L
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaEmpresaRecord(
                        1L,
                        "Empresa Exemplo"
                )
        );

        assertThat(resultado.nome())
                .isEqualTo("Empresa Exemplo");
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao com nome duplicado na organizacao"
    )
    void deveBloquearAtualizacaoComNomeDuplicadoNaOrganizacao() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        ID_ORGANIZACAO,
                        "Empresa Existente",
                        StatusEnum.ATIVO,
                        1L
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaEmpresaRecord(
                                1L,
                                "Empresa Existente"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Empresa ja cadastrada.");

        assertThat(empresa.getNome())
                .isEqualTo("Empresa Exemplo");
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao de empresa fora da organizacao"
    )
    void deveBloquearAtualizacaoDeEmpresaForaDaOrganizacao() {
        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaEmpresaRecord(
                                1L,
                                "Empresa Atualizada"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa nao encontrada ou removida."
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
            "Deve remover empresa da organizacao com auditoria"
    )
    void deveRemoverEmpresaDaOrganizacaoComAuditoria() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(subsidiariaRepository
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(usuarioEmpresaRepository
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(1L);

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(empresa.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(empresa.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de empresa com subsidiarias ativas"
    )
    void deveBloquearExclusaoDeEmpresaComSubsidiariasAtivas() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(subsidiariaRepository
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.excluir(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa possui subsidiarias ativas "
                                + "e nao pode ser removida."
                );

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(usuarioEmpresaRepository, never())
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                );

        verify(usuarioLogadoService, never())
                .getId();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de empresa com usuarios vinculados"
    )
    void deveBloquearExclusaoDeEmpresaComUsuariosVinculados() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(subsidiariaRepository
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(usuarioEmpresaRepository
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.excluir(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa possui usuarios vinculados "
                                + "e nao pode ser removida."
                );

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(usuarioLogadoService, never())
                .getId();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de empresa fora da organizacao"
    )
    void deveBloquearExclusaoDeEmpresaForaDaOrganizacao() {
        when(repository
                .findByIdAndOrganizacaoIdAndStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa nao encontrada ou removida."
                );

        verify(subsidiariaRepository, never())
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                );

        verify(usuarioEmpresaRepository, never())
                .existsByEmpresaIdAndStatus(
                        1L,
                        StatusEnum.ATIVO
                );

        verify(usuarioLogadoService, never())
                .getId();
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

    private EmpresaModel criarEmpresa(
            Long id,
            String nome
    ) {
        var organizacao = criarOrganizacao(
                ID_ORGANIZACAO,
                "Organizacao Principal"
        );

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(nome)
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                id
        );

        return empresa;
    }
}