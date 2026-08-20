package com.empresa.erp.domain.acesso.usuarioSubsidiaria.service;

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
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.UsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@ExtendWith(MockitoExtension.class)
class UsuarioSubsidiariaServiceTest {

    private static final Long ID_ORGANIZACAO = 1L;

    @Mock
    private UsuarioSubsidiariaRepository repository;

    @Mock
    private UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    @Mock
    private SubsidiariaRepository
            subsidiariaRepository;

    @Mock
    private UsuarioLogadoService
            usuarioLogadoService;

    @Mock
    private ContextoOrganizacao
            contextoOrganizacao;

    @InjectMocks
    private UsuarioSubsidiariaService service;

    @BeforeEach
    void setUp() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve cadastrar vinculo na organizacao atual"
    )
    void deveCadastrarVinculoNaOrganizacaoAtual() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                false
        );

        var subsidiaria = criarSubsidiaria(
                4L,
                empresa,
                "Filial Curitiba"
        );

        when(usuarioEmpresaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(subsidiariaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(repository
                .existsByUsuarioEmpresaAndSubsidiariaAndStatus(
                        usuarioEmpresa,
                        subsidiaria,
                        StatusEnum.ATIVO
                )
        ).thenReturn(false);

        when(repository.save(
                any(UsuarioSubsidiariaModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        var resultado = service.cadastrar(
                new UsuarioSubsidiariaRecord(
                        3L,
                        4L
                )
        );

        assertThat(resultado.getUsuarioEmpresa())
                .isSameAs(usuarioEmpresa);

        assertThat(resultado.getSubsidiaria())
                .isSameAs(subsidiaria);

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear usuario empresa fora da organizacao"
    )
    void deveBloquearUsuarioEmpresaForaDaOrganizacao() {
        when(usuarioEmpresaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioSubsidiariaRecord(
                                3L,
                                4L
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e empresa "
                                + "nao encontrado ou removido."
                );

        verify(subsidiariaRepository, never())
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioSubsidiariaModel.class
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro quando usuario acessa todas subsidiarias"
    )
    void deveBloquearCadastroQuandoUsuarioAcessaTodasSubsidiarias() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                true
        );

        when(usuarioEmpresaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioSubsidiariaRecord(
                                3L,
                                4L
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "O usuario possui acesso a todas as "
                                + "subsidiarias desta empresa."
                );

        verify(subsidiariaRepository, never())
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        any(),
                        any(),
                        any()
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioSubsidiariaModel.class
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear subsidiaria fora da organizacao"
    )
    void deveBloquearSubsidiariaForaDaOrganizacao() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                false
        );

        when(usuarioEmpresaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(subsidiariaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioSubsidiariaRecord(
                                3L,
                                4L
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Subsidiaria nao encontrada ou removida."
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioSubsidiariaModel.class
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear subsidiaria de outra empresa"
    )
    void deveBloquearSubsidiariaDeOutraEmpresa() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var outraEmpresa = criarEmpresa(
                5L,
                "Outra Empresa"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                false
        );

        var subsidiaria = criarSubsidiaria(
                4L,
                outraEmpresa,
                "Filial Curitiba"
        );

        when(usuarioEmpresaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(subsidiariaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioSubsidiariaRecord(
                                3L,
                                4L
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "A subsidiaria nao pertence a empresa "
                                + "vinculada ao usuario."
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioSubsidiariaModel.class
                        )
                );
    }

    @Test
    @DisplayName("Deve bloquear vinculo duplicado")
    void deveBloquearVinculoDuplicado() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                false
        );

        var subsidiaria = criarSubsidiaria(
                4L,
                empresa,
                "Filial Curitiba"
        );

        when(usuarioEmpresaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(usuarioEmpresa));

        when(subsidiariaRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        4L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(subsidiaria));

        when(repository
                .existsByUsuarioEmpresaAndSubsidiariaAndStatus(
                        usuarioEmpresa,
                        subsidiaria,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioSubsidiariaRecord(
                                3L,
                                4L
                        )
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario ja vinculado a esta subsidiaria."
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioSubsidiariaModel.class
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve listar vinculos da organizacao sem filtro"
    )
    void deveListarVinculosDaOrganizacaoSemFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var usuarioSubsidiaria =
                criarUsuarioSubsidiaria();

        when(repository
                .findAllByUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(usuarioSubsidiaria)
                )
        );

        var resultado = service.listar(
                paginacao,
                null
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(6L);
    }

    @Test
    @DisplayName(
            "Deve listar por usuario empresa e organizacao"
    )
    void deveListarPorUsuarioEmpresaEOrganizacao() {
        var paginacao = PageRequest.of(0, 10);

        when(repository
                .findAllByUsuarioEmpresaIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                3L
        );

        verify(repository)
                .findAllByUsuarioEmpresaIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        paginacao,
                        3L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar vinculo da organizacao"
    )
    void deveDetalharVinculoDaOrganizacao() {
        var usuarioSubsidiaria =
                criarUsuarioSubsidiaria();

        when(repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(usuarioSubsidiaria)
        );

        var resultado = service.detalhar(6L);

        assertThat(resultado.id())
                .isEqualTo(6L);

        assertThat(resultado.idUsuarioEmpresa())
                .isEqualTo(3L);

        assertThat(resultado.idUsuario())
                .isEqualTo(1L);

        assertThat(resultado.idEmpresa())
                .isEqualTo(2L);

        assertThat(resultado.idSubsidiaria())
                .isEqualTo(4L);
    }

    @Test
    @DisplayName(
            "Deve bloquear detalhe de outra organizacao"
    )
    void deveBloquearDetalheDeOutraOrganizacao() {
        when(repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(6L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e subsidiaria "
                                + "nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName(
            "Deve remover vinculo da organizacao com auditoria"
    )
    void deveRemoverVinculoDaOrganizacaoComAuditoria() {
        var usuarioSubsidiaria =
                criarUsuarioSubsidiaria();

        when(repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(usuarioSubsidiaria)
        );

        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        service.excluir(6L);

        assertThat(usuarioSubsidiaria.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioSubsidiaria.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioSubsidiaria.getRemovidoEm())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de outra organizacao"
    )
    void deveBloquearExclusaoDeOutraOrganizacao() {
        when(repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        6L,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(6L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Vinculo entre usuario e subsidiaria "
                                + "nao encontrado ou removido."
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
            Boolean todasSubsidiarias
    ) {
        var usuarioEmpresa =
                new UsuarioEmpresaModel(
                        criarUsuario(),
                        empresa,
                        todasSubsidiarias
                );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        return usuarioEmpresa;
    }

    private SubsidiariaModel criarSubsidiaria(
            Long id,
            EmpresaModel empresa,
            String nome
    ) {
        var subsidiaria = new SubsidiariaModel(
                empresa,
                nome
        );

        ReflectionTestUtils.setField(
                subsidiaria,
                "id",
                id
        );

        return subsidiaria;
    }

    private UsuarioSubsidiariaModel
            criarUsuarioSubsidiaria() {
        var empresa = criarEmpresa(
                2L,
                "Empresa Exemplo"
        );

        var usuarioEmpresa = criarUsuarioEmpresa(
                empresa,
                false
        );

        var subsidiaria = criarSubsidiaria(
                4L,
                empresa,
                "Filial Curitiba"
        );

        var usuarioSubsidiaria =
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                );

        ReflectionTestUtils.setField(
                usuarioSubsidiaria,
                "id",
                6L
        );

        return usuarioSubsidiaria;
    }
}