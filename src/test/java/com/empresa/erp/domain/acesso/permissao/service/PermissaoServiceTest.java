package com.empresa.erp.domain.acesso.permissao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
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
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;

@ExtendWith(MockitoExtension.class)
class PermissaoServiceTest {

    @Mock
    private PermissaoRepository repository;

    @InjectMocks
    private PermissaoService service;

    @Test
    @DisplayName(
            "Deve listar permissões ativas da organização sem filtro"
    )
    void deveListarPermissoesAtivasDaOrganizacaoSemFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis",
                EscopoPermissaoEnum.ORGANIZACAO,
                StatusEnum.ATIVO
        );

        when(
                repository.findAllByEscopoAndStatus(
                        paginacao,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(permissao)
                )
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
                .isEqualTo("Listar perfis");

        assertThat(resultado.getContent().get(0).chave())
                .isEqualTo("ACESSO_PERFIL_LISTAR");

        assertThat(resultado.getContent().get(0).descricao())
                .isEqualTo("Permite listar perfis");

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository)
                .findAllByEscopoAndStatus(
                        paginacao,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar permissões ativas da organização com filtro"
    )
    void deveListarPermissoesAtivasDaOrganizacaoComFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var permissao = criarPermissao(
                2L,
                "Criar perfis",
                "ACESSO_PERFIL_CRIAR",
                "Permite criar perfis",
                EscopoPermissaoEnum.ORGANIZACAO,
                StatusEnum.ATIVO
        );

        when(
                repository
                        .findByNomeContainingIgnoreCaseAndEscopoAndStatus(
                                paginacao,
                                "criar",
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(
                new PageImpl<>(
                        List.of(permissao)
                )
        );

        var resultado = service.listar(
                paginacao,
                "criar"
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(2L);

        assertThat(resultado.getContent().get(0).nome())
                .isEqualTo("Criar perfis");

        assertThat(resultado.getContent().get(0).chave())
                .isEqualTo("ACESSO_PERFIL_CRIAR");

        assertThat(resultado.getContent().get(0).descricao())
                .isEqualTo("Permite criar perfis");

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository)
                .findByNomeContainingIgnoreCaseAndEscopoAndStatus(
                        paginacao,
                        "criar",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar permissões sem filtro quando filtro estiver vazio"
    )
    void deveListarPermissoesSemFiltroQuandoFiltroEstiverVazio() {
        var paginacao = PageRequest.of(0, 10);

        when(
                repository.findAllByEscopoAndStatus(
                        paginacao,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of()
                )
        );

        var resultado = service.listar(
                paginacao,
                "   "
        );

        assertThat(resultado.getContent())
                .isEmpty();

        verify(repository)
                .findAllByEscopoAndStatus(
                        paginacao,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar permissão ativa da organização"
    )
    void deveDetalharPermissaoAtivaDaOrganizacao() {
        var permissao = criarPermissao(
                1L,
                "Listar perfis",
                "ACESSO_PERFIL_LISTAR",
                "Permite listar perfis",
                EscopoPermissaoEnum.ORGANIZACAO,
                StatusEnum.ATIVO
        );

        when(
                repository.findByIdAndEscopoAndStatus(
                        1L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(permissao)
        );

        var resultado = service.detalhar(1L);

        assertThat(resultado.id())
                .isEqualTo(1L);

        assertThat(resultado.nome())
                .isEqualTo("Listar perfis");

        assertThat(resultado.chave())
                .isEqualTo("ACESSO_PERFIL_LISTAR");

        assertThat(resultado.descricao())
                .isEqualTo("Permite listar perfis");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository)
                .findByIdAndEscopoAndStatus(
                        1L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear detalhamento de permissão inexistente ou removida"
    )
    void deveBloquearDetalhamentoDePermissaoInexistenteOuRemovida() {
        when(
                repository.findByIdAndEscopoAndStatus(
                        99L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.detalhar(99L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Permissao nao encontrada ou removida."
                );

        verify(repository)
                .findByIdAndEscopoAndStatus(
                        99L,
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );
    }

    private PermissaoModel criarPermissao(
            Long id,
            String nome,
            String chave,
            String descricao,
            EscopoPermissaoEnum escopo,
            StatusEnum status
    ) {
        PermissaoModel permissao =
                instanciarPermissao();

        ReflectionTestUtils.setField(
                permissao,
                "id",
                id
        );

        ReflectionTestUtils.setField(
                permissao,
                "nome",
                nome
        );

        ReflectionTestUtils.setField(
                permissao,
                "chave",
                chave
        );

        ReflectionTestUtils.setField(
                permissao,
                "descricao",
                descricao
        );

        ReflectionTestUtils.setField(
                permissao,
                "sistema",
                true
        );

        ReflectionTestUtils.setField(
                permissao,
                "escopo",
                escopo
        );

        ReflectionTestUtils.setField(
                permissao,
                "status",
                status
        );

        return permissao;
    }

    private PermissaoModel instanciarPermissao() {
        try {
            var construtor =
                    PermissaoModel.class
                            .getDeclaredConstructor();

            construtor.setAccessible(true);

            return construtor.newInstance();
        } catch (
                InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException exception
        ) {
            throw new IllegalStateException(
                    "Não foi possível criar permissão para o teste.",
                    exception
            );
        }
    }
}