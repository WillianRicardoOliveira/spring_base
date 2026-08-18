package com.empresa.erp.domain.acesso.usuarioEmpresa.service;

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
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.UsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository.UsuarioSubsidiariaRepository;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.empresa.service.EmpresaService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioEmpresaServiceTest {

    @Mock
    private UsuarioEmpresaRepository repository;

    @Mock
    private UsuarioSubsidiariaRepository
            usuarioSubsidiariaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @InjectMocks
    private UsuarioEmpresaService service;

    @Test
    @DisplayName("Deve cadastrar vinculo")
    void deveCadastrarVinculo() {
        var usuario = criarUsuario(1L);
        var empresa = criarEmpresa(2L);

        when(usuarioRepository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(usuario));

        when(empresaRepository.findByIdAndStatus(
                2L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(empresa));

        when(repository.existsByUsuarioAndEmpresaAndStatus(
                usuario,
                empresa,
                StatusEnum.ATIVO
        )).thenReturn(false);

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

        assertThat(resultado.getUsuario())
                .isSameAs(usuario);

        assertThat(resultado.getEmpresa())
                .isSameAs(empresa);

        assertThat(resultado.getTodasSubsidiarias())
                .isTrue();

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(repository).save(
                any(UsuarioEmpresaModel.class)
        );
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro para usuario inexistente"
    )
    void deveBloquearCadastroParaUsuarioInexistente() {
        when(usuarioRepository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario nao encontrado ou removido."
                );

        verify(empresaRepository, never())
                .findByIdAndStatus(
                        any(Long.class),
                        any(StatusEnum.class)
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioEmpresaModel.class
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro para empresa inexistente"
    )
    void deveBloquearCadastroParaEmpresaInexistente() {
        var usuario = criarUsuario(1L);

        when(usuarioRepository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(usuario));

        when(empresaRepository.findByIdAndStatus(
                2L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Empresa nao encontrada ou removida."
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioEmpresaModel.class
                        )
                );
    }

    @Test
    @DisplayName("Deve bloquear vinculo duplicado")
    void deveBloquearVinculoDuplicado() {
        var usuario = criarUsuario(1L);
        var empresa = criarEmpresa(2L);

        when(usuarioRepository.findByIdAndStatus(
                1L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(usuario));

        when(empresaRepository.findByIdAndStatus(
                2L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.of(empresa));

        when(repository.existsByUsuarioAndEmpresaAndStatus(
                usuario,
                empresa,
                StatusEnum.ATIVO
        )).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(
                        new UsuarioEmpresaRecord(
                                1L,
                                2L,
                                true
                        )
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario ja vinculado a esta empresa."
                );

        verify(repository, never())
                .save(
                        any(
                                UsuarioEmpresaModel.class
                        )
                );
    }

    @Test
    @DisplayName("Deve listar sem filtros")
    void deveListarSemFiltros() {
        var paginacao =
                PageRequest.of(0, 10);

        var usuarioEmpresa =
                criarUsuarioEmpresa();

        when(repository.findAllByStatus(
                paginacao,
                StatusEnum.ATIVO
        )).thenReturn(
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

        assertThat(
                resultado.getContent()
                        .get(0)
                        .id()
        ).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve listar por usuario")
    void deveListarPorUsuario() {
        var paginacao =
                PageRequest.of(0, 10);

        when(repository.findAllByUsuarioIdAndStatus(
                paginacao,
                1L,
                StatusEnum.ATIVO
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                1L,
                null
        );

        verify(repository)
                .findAllByUsuarioIdAndStatus(
                        paginacao,
                        1L,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName("Deve listar por empresa")
    void deveListarPorEmpresa() {
        var paginacao =
                PageRequest.of(0, 10);

        when(repository.findAllByEmpresaIdAndStatus(
                paginacao,
                2L,
                StatusEnum.ATIVO
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        service.listar(
                paginacao,
                null,
                2L
        );

        verify(repository)
                .findAllByEmpresaIdAndStatus(
                        paginacao,
                        2L,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName("Deve listar por usuario e empresa")
    void deveListarPorUsuarioEEmpresa() {
        var paginacao =
                PageRequest.of(0, 10);

        when(repository
                .findAllByUsuarioIdAndEmpresaIdAndStatus(
                        paginacao,
                        1L,
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
                .findAllByUsuarioIdAndEmpresaIdAndStatus(
                        paginacao,
                        1L,
                        2L,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName("Deve listar empresas para selecao")
    void deveListarEmpresasParaSelecao() {
        var paginacao =
                PageRequest.of(0, 10);

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

        var resultado =
                service.listarEmpresas(
                        paginacao,
                        "Exemplo"
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .id()
        ).isEqualTo(2L);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .nome()
        ).isEqualTo("Empresa Exemplo");

        assertThat(
                resultado.getContent()
                        .get(0)
                        .status()
        ).isEqualTo(StatusEnum.ATIVO);

        verify(empresaService).listar(
                paginacao,
                "Exemplo"
        );
    }

    @Test
    @DisplayName("Deve detalhar vinculo ativo")
    void deveDetalharVinculoAtivo() {
        var usuarioEmpresa =
                criarUsuarioEmpresa();

        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuarioEmpresa)
        );

        var resultado =
                service.detalhar(3L);

        assertThat(resultado.id())
                .isEqualTo(3L);

        assertThat(resultado.idUsuario())
                .isEqualTo(1L);

        assertThat(resultado.idEmpresa())
                .isEqualTo(2L);

        assertThat(resultado.todasSubsidiarias())
                .isTrue();
    }

    @Test
    @DisplayName("Deve bloquear detalhe inexistente")
    void deveBloquearDetalheInexistente() {
        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(3L)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
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
        var usuarioEmpresa =
                criarUsuarioEmpresa();

        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuarioEmpresa)
        );

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
            "Deve permitir habilitar todas subsidiarias sem vinculos"
    )
    void devePermitirHabilitarTodasSubsidiariasSemVinculos() {
        var usuarioEmpresa =
                criarUsuarioEmpresa(false);

        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuarioEmpresa)
        );

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
            "Deve bloquear acesso a todas subsidiarias com vinculos"
    )
    void deveBloquearAcessoATodasSubsidiariasComVinculos() {
        var usuarioEmpresa =
                criarUsuarioEmpresa(false);

        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuarioEmpresa)
        );

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
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Remova os vinculos com subsidiarias "
                                + "antes de habilitar o acesso "
                                + "a todas as subsidiarias."
                );

        assertThat(
                usuarioEmpresa
                        .getTodasSubsidiarias()
        ).isFalse();
    }

    @Test
    @DisplayName("Deve bloquear atualizacao inexistente")
    void deveBloquearAtualizacaoInexistente() {
        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.atualizar(
                        new AtualizaUsuarioEmpresaRecord(
                                3L,
                                false
                        )
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Vinculo entre usuario e empresa "
                                + "nao encontrado ou removido."
                );
    }

    @Test
    @DisplayName("Deve remover vinculo com auditoria")
    void deveRemoverVinculoComAuditoria() {
        var usuarioEmpresa =
                criarUsuarioEmpresa();

        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuarioEmpresa)
        );

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

        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuarioEmpresa)
        );

        when(usuarioSubsidiariaRepository
                .existsByUsuarioEmpresaIdAndStatus(
                        3L,
                        StatusEnum.ATIVO
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.excluir(3L)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "O vinculo entre usuario e empresa "
                                + "possui subsidiarias vinculadas "
                                + "e nao pode ser removido."
                );

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(
                usuarioLogadoService,
                never()
        ).getId();
    }

    @Test
    @DisplayName("Deve bloquear exclusao inexistente")
    void deveBloquearExclusaoInexistente() {
        when(repository.findByIdAndStatus(
                3L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(3L)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Vinculo entre usuario e empresa "
                                + "nao encontrado ou removido."
                );

        verify(
                usuarioLogadoService,
                never()
        ).getId();
    }

    private UsuarioModel criarUsuario(
            Long id
    ) {
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

    private EmpresaModel criarEmpresa(
            Long id
    ) {
        var empresa = new EmpresaModel(
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
        var usuarioEmpresa =
                new UsuarioEmpresaModel(
                        criarUsuario(1L),
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