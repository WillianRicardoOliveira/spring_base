package com.empresa.erp.domain.acesso.usuarioEstabelecimento.service;

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
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.UsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.repository.UsuarioEstabelecimentoRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.repository.EstabelecimentoRepository;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@ExtendWith(MockitoExtension.class)
class UsuarioEstabelecimentoServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private UsuarioEstabelecimentoRepository repository;

    @Mock
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private ContextoOrganizacao contextoOrganizacao;

    @InjectMocks
    private UsuarioEstabelecimentoService service;

    @BeforeEach
    void setUp() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName("Deve cadastrar vinculo na organizacao atual")
    void deveCadastrarVinculoNaOrganizacaoAtual() {
        var empresa = criarEmpresa(2L, "Empresa Exemplo");
        var usuarioEmpresa = criarUsuarioEmpresa(empresa, false);
        var estabelecimento = criarEstabelecimento(4L, empresa, "Filial Curitiba");

        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(estabelecimentoRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(repository
                .existsByUsuarioEmpresaAndEstabelecimentoAndStatus(
                        usuarioEmpresa,
                        estabelecimento,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(repository.save(any(UsuarioEstabelecimentoModel.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        var resultado = service.cadastrar(
                new UsuarioEstabelecimentoRecord(3L, 4L)
        );

        assertThat(resultado.getUsuarioEmpresa())
                .isSameAs(usuarioEmpresa);

        assertThat(resultado.getEstabelecimento())
                .isSameAs(estabelecimento);

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve bloquear usuario empresa fora da organizacao")
    void deveBloquearUsuarioEmpresaForaDaOrganizacao() {
        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(new UsuarioEstabelecimentoRecord(3L, 4L))
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e empresa nao encontrado ou removido."
                );

        verify(estabelecimentoRepository, never())
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(UsuarioEstabelecimentoModel.class));
    }

    @Test
    @DisplayName("Deve bloquear cadastro quando usuario acessa todos estabelecimentos")
    void deveBloquearCadastroQuandoUsuarioAcessaTodosEstabelecimentos() {
        var empresa = criarEmpresa(2L, "Empresa Exemplo");
        var usuarioEmpresa = criarUsuarioEmpresa(empresa, true);

        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        assertThatThrownBy(() ->
                service.cadastrar(new UsuarioEstabelecimentoRecord(3L, 4L))
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "O usuario possui acesso a todos os estabelecimentos desta empresa."
                );

        verify(estabelecimentoRepository, never())
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(any(UsuarioEstabelecimentoModel.class));
    }

    @Test
    @DisplayName("Deve bloquear estabelecimento fora da organizacao")
    void deveBloquearEstabelecimentoForaDaOrganizacao() {
        var empresa = criarEmpresa(2L, "Empresa Exemplo");
        var usuarioEmpresa = criarUsuarioEmpresa(empresa, false);

        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(estabelecimentoRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(new UsuarioEstabelecimentoRecord(3L, 4L))
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Estabelecimento nao encontrado ou removido.");

        verify(repository, never())
                .save(any(UsuarioEstabelecimentoModel.class));
    }

    @Test
    @DisplayName("Deve bloquear estabelecimento de outra empresa")
    void deveBloquearEstabelecimentoDeOutraEmpresa() {
        var empresa = criarEmpresa(2L, "Empresa Exemplo");
        var outraEmpresa = criarEmpresa(5L, "Outra Empresa");
        var usuarioEmpresa = criarUsuarioEmpresa(empresa, false);
        var estabelecimento = criarEstabelecimento(4L, outraEmpresa, "Filial Curitiba");

        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(estabelecimentoRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() ->
                service.cadastrar(new UsuarioEstabelecimentoRecord(3L, 4L))
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "O estabelecimento nao pertence a empresa vinculada ao usuario."
                );

        verify(repository, never())
                .save(any(UsuarioEstabelecimentoModel.class));
    }

    @Test
    @DisplayName("Deve bloquear vinculo duplicado")
    void deveBloquearVinculoDuplicado() {
        var empresa = criarEmpresa(2L, "Empresa Exemplo");
        var usuarioEmpresa = criarUsuarioEmpresa(empresa, false);
        var estabelecimento = criarEstabelecimento(4L, empresa, "Filial Curitiba");

        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(estabelecimentoRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(estabelecimento));

        when(repository
                .existsByUsuarioEmpresaAndEstabelecimentoAndStatus(
                        usuarioEmpresa,
                        estabelecimento,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(new UsuarioEstabelecimentoRecord(3L, 4L))
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario ja vinculado a este estabelecimento.");

        verify(repository, never())
                .save(any(UsuarioEstabelecimentoModel.class));
    }

    @Test
    @DisplayName("Deve listar vinculos da organizacao sem filtro")
    void deveListarVinculosDaOrganizacaoSemFiltro() {
        var paginacao = PageRequest.of(0, 10);
        var usuarioEstabelecimento = criarUsuarioEstabelecimento();

        when(repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        null,
                        StatusEnum.ATIVO
                )
        ).thenReturn(new PageImpl<>(List.of(usuarioEstabelecimento)));

        var resultado = service.listar(paginacao, null);

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(6L);
    }

    @Test
    @DisplayName("Deve listar por usuario empresa e organizacao")
    void deveListarPorUsuarioEmpresaEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);
        var empresa = criarEmpresa(2L, "Empresa Exemplo");
        var usuarioEmpresa = criarUsuarioEmpresa(empresa, false);

        when(usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        3L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(new PageImpl<>(List.of()));

        service.listar(paginacao, 3L);

        verify(repository)
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        ID_ORGANIZACAO,
                        3L,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName("Deve detalhar vinculo da organizacao")
    void deveDetalharVinculoDaOrganizacao() {
        var usuarioEstabelecimento = criarUsuarioEstabelecimento();

        when(repository
                .buscarAtivoDaOrganizacaoPorId(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEstabelecimento));

        var resultado = service.detalhar(6L);

        assertThat(resultado.id())
                .isEqualTo(6L);

        assertThat(resultado.idUsuarioEmpresa())
                .isEqualTo(3L);

        assertThat(resultado.idUsuario())
                .isEqualTo(1L);

        assertThat(resultado.idEmpresa())
                .isEqualTo(2L);

        assertThat(resultado.idEstabelecimento())
                .isEqualTo(4L);
    }

    @Test
    @DisplayName("Deve bloquear detalhe de outra organizacao")
    void deveBloquearDetalheDeOutraOrganizacao() {
        when(repository
                .buscarAtivoDaOrganizacaoPorId(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalhar(6L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e estabelecimento nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName("Deve remover vinculo da organizacao com auditoria")
    void deveRemoverVinculoDaOrganizacaoComAuditoria() {
        var usuarioEstabelecimento = criarUsuarioEstabelecimento();

        when(repository
                .buscarAtivoDaOrganizacaoPorId(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEstabelecimento));

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(6L);

        assertThat(usuarioEstabelecimento.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioEstabelecimento.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioEstabelecimento.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName("Deve bloquear exclusao de outra organizacao")
    void deveBloquearExclusaoDeOutraOrganizacao() {
        when(repository
                .buscarAtivoDaOrganizacaoPorId(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(6L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e estabelecimento nao encontrado ou removido."
                );

        verify(usuarioLogadoService, never())
                .getId();
    }

    private UsuarioModel criarUsuario() {
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
                1L
        );

        return usuario;
    }

    private EmpresaModel criarEmpresa(
            Long id,
            String nome
    ) {
        var organizacao = new OrganizacaoModel(
                "Organizacao Principal"
        );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                ID_ORGANIZACAO
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

    private UsuarioEmpresaModel criarUsuarioEmpresa(
            EmpresaModel empresa,
            Boolean todosEstabelecimentos
    ) {
        var usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        criarUsuario(),
                        empresa.getOrganizacao()
                );

        ReflectionTestUtils.setField(
                usuarioOrganizacao,
                "id",
                11L
        );

        var usuarioEmpresa =
                new UsuarioEmpresaModel(
                        usuarioOrganizacao,
                        empresa,
                        todosEstabelecimentos
                );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        return usuarioEmpresa;
    }

    private EstabelecimentoModel criarEstabelecimento(
            Long id,
            EmpresaModel empresa,
            String nome
    ) {
        var estabelecimento = new EstabelecimentoModel(
                empresa,
                nome
        );

        ReflectionTestUtils.setField(
                estabelecimento,
                "id",
                id
        );

        return estabelecimento;
    }

    private UsuarioEstabelecimentoModel criarUsuarioEstabelecimento() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                false
        );

        var estabelecimento = criarEstabelecimento(
                4L,
                empresa,
                "Filial Curitiba"
        );

        var usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        estabelecimento
                );

        ReflectionTestUtils.setField(
                usuarioEstabelecimento,
                "id",
                6L
        );

        return usuarioEstabelecimento;
    }
}