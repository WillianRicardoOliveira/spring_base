package com.empresa.erp.domain.acesso.usuarioEmpresa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
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
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.UsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.empresa.service.EmpresaService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@ExtendWith(MockitoExtension.class)
class UsuarioEmpresaServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private UsuarioEmpresaRepository repository;

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private UsuarioSubsidiariaRepository
            usuarioSubsidiariaRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @InjectMocks
    private UsuarioEmpresaService service;

    @BeforeEach
    void setUp() {
        lenient()
                .when(
                        contextoOrganizacao
                                .getIdOrganizacao()
                )
                .thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve cadastrar vinculo na organizacao atual"
    )
    void deveCadastrarVinculoNaOrganizacaoAtual() {
        var usuario = criarUsuario(1L);
        var organizacao = criarOrganizacao(ID_ORGANIZACAO);
        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        11L,
                        usuario,
                        organizacao
                );
        var empresa = criarEmpresa(2L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioOrganizacao));

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByUsuarioOrganizacaoIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        11L,
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(repository.save(
                any(UsuarioEmpresaModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        var resultado = service.cadastrar(
                new UsuarioEmpresaRecord(
                        1L,
                        2L,
                        true
                )
        );

        assertThat(resultado.getUsuarioOrganizacao())
                .isSameAs(usuarioOrganizacao);

        assertThat(
                resultado
                        .getUsuarioOrganizacao()
                        .getUsuario()
        ).isSameAs(usuario);

        assertThat(resultado.getEmpresa())
                .isSameAs(empresa);

        assertThat(resultado.getTodasSubsidiarias())
                .isTrue();

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro para usuario inexistente"
    )
    void deveBloquearCadastroParaUsuarioInexistente() {
        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario nao encontrado na organizacao."
                );

        verify(empresaRepository, never())
                .findByIdAndOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(UsuarioEmpresaModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear usuario sem vinculo com organizacao"
    )
    void deveBloquearUsuarioSemVinculoComOrganizacao() {
        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario nao encontrado na organizacao."
                );

        verify(empresaRepository, never())
                .findByIdAndOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(UsuarioEmpresaModel.class));
    }

    @Test
    @DisplayName(
            "Deve bloquear empresa fora da organizacao"
    )
    void deveBloquearEmpresaForaDaOrganizacao() {
        var usuario = criarUsuario(1L);
        var organizacao = criarOrganizacao(ID_ORGANIZACAO);
        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        11L,
                        usuario,
                        organizacao
                );

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioOrganizacao));

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Empresa nao encontrada ou removida."
                );

        verify(repository, never())
                .save(any(UsuarioEmpresaModel.class));
    }

    @Test
    @DisplayName("Deve bloquear vinculo duplicado")
    void deveBloquearVinculoDuplicado() {
        var usuario = criarUsuario(1L);
        var organizacao = criarOrganizacao(ID_ORGANIZACAO);
        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        11L,
                        usuario,
                        organizacao
                );
        var empresa = criarEmpresa(2L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioOrganizacao));

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .existsByUsuarioOrganizacaoIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        11L,
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario ja vinculado a esta empresa."
                );

        verify(repository, never())
                .save(any(UsuarioEmpresaModel.class));
    }

    @Test
    @DisplayName(
            "Deve listar vinculos da organizacao sem filtros"
    )
    void deveListarVinculosDaOrganizacaoSemFiltros() {
        var paginacao = PageRequest.of(0, 10);
        var usuarioEmpresa = criarUsuarioEmpresa();

        when(repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        null,
                        null,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(usuarioEmpresa)
                )
        );

        var resultado = service.listar(
                paginacao,
                null,
                null
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(3L);
    }

    @Test
    @DisplayName(
            "Deve listar por usuario e organizacao"
    )
    void deveListarPorUsuarioEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);
        var usuario = criarUsuario(1L);
        var organizacao = criarOrganizacao(ID_ORGANIZACAO);
        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        11L,
                        usuario,
                        organizacao
                );

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioOrganizacao));

        when(repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        11L,
                        null,
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
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        11L,
                        null,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar por empresa e organizacao"
    )
    void deveListarPorEmpresaEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);
        var empresa = criarEmpresa(2L);

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        null,
                        2L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                null,
                2L
        );

        verify(repository)
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        null,
                        2L,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar por usuario empresa e organizacao"
    )
    void deveListarPorUsuarioEmpresaEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);
        var usuario = criarUsuario(1L);
        var organizacao = criarOrganizacao(ID_ORGANIZACAO);
        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        11L,
                        usuario,
                        organizacao
                );
        var empresa = criarEmpresa(2L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioOrganizacao));

        when(empresaRepository
                .findByIdAndOrganizacaoIdAndStatus(
                        2L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(empresa));

        when(repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        11L,
                        2L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                1L,
                2L
        );

        verify(repository)
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        11L,
                        2L,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar empresas pelo servico isolado"
    )
    void deveListarEmpresasPeloServicoIsolado() {
        var paginacao = PageRequest.of(0, 10);

        var empresas = new PageImpl<>(
                List.of(
                        new ListaEmpresaRecord(
                                2L,
                                "Empresa Exemplo",
                                StatusEnum.ATIVO
                        )
                ),
                paginacao,
                1
        );

        when(empresaService.listar(
                paginacao,
                "Exemplo"
        )).thenReturn(empresas);

        var resultado = service.listarEmpresas(
                paginacao,
                "Exemplo"
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(2L);

        verify(empresaService).listar(
                paginacao,
                "Exemplo"
        );
    }

    @Test
    @DisplayName(
            "Deve detalhar vinculo da organizacao"
    )
    void deveDetalharVinculoDaOrganizacao() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        var resultado = service.detalhar(3L);

        assertThat(resultado.id())
                .isEqualTo(3L);

        assertThat(resultado.idUsuario())
                .isEqualTo(1L);

        assertThat(resultado.idEmpresa())
                .isEqualTo(2L);
    }

    @Test
    @DisplayName(
            "Deve bloquear detalhe de outra organizacao"
    )
    void deveBloquearDetalheDeOutraOrganizacao() {
        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(3L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e empresa "
                                + "nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve atualizar acesso a todas subsidiarias"
    )
    void deveAtualizarAcessoATodasSubsidiarias() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        var resultado = service.atualizar(
                new AtualizaUsuarioEmpresaRecord(
                        3L,
                        false
                )
        );

        assertThat(resultado.todasSubsidiarias())
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve permitir habilitar todas sem vinculos"
    )
    void devePermitirHabilitarTodasSemVinculos() {
        var usuarioEmpresa =
                criarUsuarioEmpresa(false);

        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        3L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        var resultado = service.atualizar(
                new AtualizaUsuarioEmpresaRecord(
                        3L,
                        true
                )
        );

        assertThat(resultado.todasSubsidiarias())
                .isTrue();
    }

    @Test
    @DisplayName(
            "Deve bloquear habilitacao com vinculos"
    )
    void deveBloquearHabilitacaoComVinculos() {
        var usuarioEmpresa =
                criarUsuarioEmpresa(false);

        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        3L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaUsuarioEmpresaRecord(
                                3L,
                                true
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Remova os vinculos com subsidiarias "
                                + "antes de habilitar o acesso "
                                + "a todas as subsidiarias."
                );

        assertThat(usuarioEmpresa.getTodasSubsidiarias())
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve bloquear atualizacao de outra organizacao"
    )
    void deveBloquearAtualizacaoDeOutraOrganizacao() {
        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaUsuarioEmpresaRecord(
                                3L,
                                false
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e empresa "
                                + "nao encontrado ou removido."
                );

        verify(usuarioSubsidiariaRepository, never())
                .existsByUsuarioEmpresaIdAndStatus(
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName(
            "Deve remover vinculo da organizacao com auditoria"
    )
    void deveRemoverVinculoDaOrganizacaoComAuditoria() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        3L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(3L);

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioEmpresa.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioEmpresa.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao com subsidiarias vinculadas"
    )
    void deveBloquearExclusaoComSubsidiariasVinculadas() {
        var usuarioEmpresa =
                criarUsuarioEmpresa(false);

        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        3L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.excluir(3L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "O vinculo entre usuario e empresa "
                                + "possui subsidiarias vinculadas "
                                + "e nao pode ser removido."
                );

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(usuarioLogadoService, never())
                .getId();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de outra organizacao"
    )
    void deveBloquearExclusaoDeOutraOrganizacao() {
        when(repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(3L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e empresa "
                                + "nao encontrado ou removido."
                );

        verify(usuarioSubsidiariaRepository, never())
                .existsByUsuarioEmpresaIdAndStatus(
                        any(),
                        any()
                );

        verify(usuarioLogadoService, never())
                .getId();
    }

    private UsuarioModel criarUsuario(Long id) {
        var usuario = new UsuarioModel(
                new UsuarioRecord(
                        "usuario@teste.com",
                        "123456"
                ),
                "senha-criptografada"
        );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                id
        );

        return usuario;
    }

    private OrganizacaoModel criarOrganizacao(
            Long id
    ) {
        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                id
        );

        return organizacao;
    }

    private UsuarioOrganizacaoModel
            criarUsuarioOrganizacao(
                    Long id,
                    UsuarioModel usuario,
                    OrganizacaoModel organizacao
            ) {
        var usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        ReflectionTestUtils.setField(
                usuarioOrganizacao,
                "id",
                id
        );

        return usuarioOrganizacao;
    }

    private EmpresaModel criarEmpresa(Long id) {
        var empresa = new EmpresaModel(
                criarOrganizacao(ID_ORGANIZACAO),
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

    private UsuarioEmpresaModel
            criarUsuarioEmpresa() {
        return criarUsuarioEmpresa(true);
    }

    private UsuarioEmpresaModel
            criarUsuarioEmpresa(
                    Boolean todasSubsidiarias
            ) {
        var organizacao =
                criarOrganizacao(ID_ORGANIZACAO);

        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        11L,
                        criarUsuario(1L),
                        organizacao
                );

        var usuarioEmpresa =
                new UsuarioEmpresaModel(
                        usuarioOrganizacao,
                        criarEmpresa(2L),
                        todasSubsidiarias
                );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        return usuarioEmpresa;
    }
}