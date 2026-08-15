package com.empresa.erp.domain.configuracao.empresa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
import com.empresa.erp.domain.old.StatusEnum;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository repository;

    @Mock
    private SubsidiariaRepository subsidiariaRepository;

    @Mock
    private UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @InjectMocks
    private EmpresaService service;

    @Test
    @DisplayName("Deve cadastrar empresa com nome normalizado")
    void deveCadastrarEmpresaComNomeNormalizado() {
        when(repository.existsByNomeIgnoreCaseAndStatus(
                "Empresa Exemplo",
                StatusEnum.ATIVO
        )).thenReturn(false);

        when(repository.save(any(EmpresaModel.class)))
                .thenAnswer(
                        invocacao -> invocacao.getArgument(0)
                );

        var resultado = service.cadastrar(
                new EmpresaRecord(
                        "  Empresa   Exemplo  "
                )
        );

        assertThat(resultado.getNome())
                .isEqualTo("Empresa Exemplo");

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(
                any(EmpresaModel.class)
        );
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro de empresa ativa duplicada"
    )
    void deveBloquearCadastroDeEmpresaAtivaDuplicada() {
        when(repository.existsByNomeIgnoreCaseAndStatus(
                "Empresa Exemplo",
                StatusEnum.ATIVO
        )).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(
                        new EmpresaRecord(
                                "Empresa Exemplo"
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Empresa ja cadastrada.");

        verify(repository, never())
                .save(any(EmpresaModel.class));
    }

    @Test
    @DisplayName("Deve listar empresas ativas sem filtro")
    void deveListarEmpresasAtivasSemFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository.findAllByStatus(
                paginacao,
                StatusEnum.ATIVO
        )).thenReturn(
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
    }

    @Test
    @DisplayName("Deve listar empresas ativas com filtro")
    void deveListarEmpresasAtivasComFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .findByNomeContainingIgnoreCaseAndStatus(
                        paginacao,
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
                .findByNomeContainingIgnoreCaseAndStatus(
                        paginacao,
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

        when(repository.findAllByStatus(
                paginacao,
                StatusEnum.ATIVO
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                "   "
        );

        verify(repository).findAllByStatus(
                paginacao,
                StatusEnum.ATIVO
        );
    }

    @Test
    @DisplayName("Deve detalhar empresa ativa")
    void deveDetalharEmpresaAtiva() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(empresa)
        );

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
            "Deve rejeitar detalhamento de empresa inexistente"
    )
    void deveRejeitarDetalhamentoDeEmpresaInexistente() {
        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

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
            "Deve atualizar empresa e normalizar nome"
    )
    void deveAtualizarEmpresaENormalizarNome() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository
                .existsByNomeIgnoreCaseAndStatusAndIdNot(
                        "Empresa Atualizada",
                        StatusEnum.ATIVO,
                        1L
                )
        ).thenReturn(false);

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(empresa)
        );

        var resultado = service.atualizar(
                new AtualizaEmpresaRecord(
                        1L,
                        "  Empresa   Atualizada  "
                )
        );

        assertThat(resultado.nome())
                .isEqualTo("Empresa Atualizada");
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
                .existsByNomeIgnoreCaseAndStatusAndIdNot(
                        "Empresa Exemplo",
                        StatusEnum.ATIVO,
                        1L
                )
        ).thenReturn(false);

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(empresa)
        );

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
            "Deve bloquear atualizacao com nome duplicado"
    )
    void deveBloquearAtualizacaoComNomeDuplicado() {
        when(repository
                .existsByNomeIgnoreCaseAndStatusAndIdNot(
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
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao de empresa removida"
    )
    void deveBloquearAtualizacaoDeEmpresaRemovida() {
        when(repository
                .existsByNomeIgnoreCaseAndStatusAndIdNot(
                        "Empresa Atualizada",
                        StatusEnum.ATIVO,
                        1L
                )
        ).thenReturn(false);

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

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
    }

    @Test
    @DisplayName("Deve remover empresa com auditoria")
    void deveRemoverEmpresaComAuditoria() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(empresa));

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

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(empresa));

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

        assertThat(empresa.getRemovidoEm())
                .isNull();

        verify(
                usuarioEmpresaRepository,
                never()
        ).existsByEmpresaIdAndStatus(
                1L,
                StatusEnum.ATIVO
        );

        verify(
                usuarioLogadoService,
                never()
        ).getId();
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

        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(empresa));

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

        assertThat(empresa.getRemovidoEm())
                .isNull();

        verify(
                usuarioLogadoService,
                never()
        ).getId();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de empresa inexistente"
    )
    void deveBloquearExclusaoDeEmpresaInexistente() {
        when(repository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa nao encontrada ou removida."
                );

        verify(
                subsidiariaRepository,
                never()
        ).existsByEmpresaIdAndStatus(
                1L,
                StatusEnum.ATIVO
        );

        verify(
                usuarioEmpresaRepository,
                never()
        ).existsByEmpresaIdAndStatus(
                1L,
                StatusEnum.ATIVO
        );

        verify(
                usuarioLogadoService,
                never()
        ).getId();
    }

    private EmpresaModel criarEmpresa(
            Long id,
            String nome
    ) {
        var empresa = new EmpresaModel(
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