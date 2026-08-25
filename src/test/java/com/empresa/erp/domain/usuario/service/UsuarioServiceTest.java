package com.empresa.erp.domain.usuario.service;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.administrador.service.ProtecaoAdministradorOrganizacaoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.criacao.service.CriacaoUsuarioService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final Long ID_ORGANIZACAO =
            10L;

    private static final Long ID_USUARIO =
            20L;

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private ProtecaoAdministradorOrganizacaoService
            protecaoAdministradorOrganizacaoService;

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private CriacaoUsuarioService
            criacaoUsuarioService;

    @Mock
    private UsuarioAutenticadoService
            usuarioAutenticadoService;

    @Mock
    private UsuarioLogadoService
            usuarioLogadoService;

    @Mock
    private ContextoOrganizacao
            contextoOrganizacao;

    @InjectMocks
    private UsuarioService service;

    @Test
    @DisplayName(
            "Deve criar usuário global e vinculá-lo à organização atual"
    )
    void deveCriarUsuarioGlobalEVinculaLoAOrganizacaoAtual() {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "usuario@teste.com",
                        "Senha@2026"
                );

        UsuarioModel usuario =
                criarUsuario(
                        ID_USUARIO,
                        "usuario@teste.com"
                );

        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(criacaoUsuarioService.criar(
                dados.email(),
                dados.senha()
        )).thenReturn(usuario);

        when(organizacaoRepository.getReferenceById(
                ID_ORGANIZACAO
        )).thenReturn(organizacao);

        when(usuarioOrganizacaoRepository.save(
                org.mockito.ArgumentMatchers.any(
                        UsuarioOrganizacaoModel.class
                )
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        UsuarioOrganizacaoModel resultado =
                service.cadastrar(dados);

        assertThat(resultado.getUsuario())
                .isSameAs(usuario);

        assertThat(resultado.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(resultado.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(criacaoUsuarioService)
                .criar(
                        "usuario@teste.com",
                        "Senha@2026"
                );

        verify(organizacaoRepository)
                .getReferenceById(
                        ID_ORGANIZACAO
                );

        verify(usuarioOrganizacaoRepository)
                .save(resultado);
    }

    @Test
    @DisplayName(
            "Não deve criar vínculo quando a criação global do usuário falhar"
    )
    void naoDeveCriarVinculoQuandoCriacaoGlobalDoUsuarioFalhar() {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "usuario@teste.com",
                        "Senha@2026"
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(criacaoUsuarioService.criar(
                dados.email(),
                dados.senha()
        )).thenThrow(
                new ValidacaoException(
                        "Usuario ja cadastrado."
                )
        );

        assertThatThrownBy(
                () -> service.cadastrar(dados)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario ja cadastrado."
                );

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(criacaoUsuarioService)
                .criar(
                        dados.email(),
                        dados.senha()
                );

        verifyNoInteractions(
                organizacaoRepository,
                usuarioOrganizacaoRepository
        );
    }

    @Test
    @DisplayName(
            "Deve listar usuários ativos da organização sem filtro"
    )
    void deveListarUsuariosAtivosDaOrganizacaoSemFiltro() {
        var paginacao =
                PageRequest.of(0, 10);

        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        ID_USUARIO,
                        "usuario@teste.com"
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(vinculo)
                )
        );

        var resultado =
                service.listar(
                        paginacao,
                        null,
                        null
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(ID_USUARIO);

        assertThat(resultado.getContent().get(0).email())
                .isEqualTo(
                        "usuario@teste.com"
                );

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve listar usuários inativos com filtro normalizado"
    )
    void deveListarUsuariosInativosComFiltroNormalizado() {
        var paginacao =
                PageRequest.of(0, 10);

        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        ID_USUARIO,
                        "financeiro@teste.com"
                );

        vinculo.inativar();

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "fin",
                        StatusEnum.INATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(
                        List.of(vinculo)
                )
        );

        var resultado =
                service.listar(
                        paginacao,
                        " fin ",
                        StatusEnum.INATIVO
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(ID_USUARIO);

        assertThat(resultado.getContent().get(0).email())
                .isEqualTo(
                        "financeiro@teste.com"
                );

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.INATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                        paginacao,
                        ID_ORGANIZACAO,
                        "fin",
                        StatusEnum.INATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve listar usuários com status removido"
    )
    void naoDeveListarUsuariosComStatusRemovido() {
        var paginacao =
                PageRequest.of(0, 10);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        assertThatThrownBy(
                () -> service.listar(
                        paginacao,
                        null,
                        StatusEnum.REMOVIDO
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Status de usuario invalido."
                );

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verifyNoInteractions(
                usuarioOrganizacaoRepository
        );
    }

    @Test
    @DisplayName(
            "Deve detalhar usuário ativo somente na organização atual"
    )
    void deveDetalharUsuarioAtivoSomenteNaOrganizacaoAtual() {
        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        ID_USUARIO,
                        "usuario@teste.com"
                );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(vinculo)
        );

        var resultado =
                service.detalhar(ID_USUARIO);

        assertThat(resultado.id())
                .isEqualTo(ID_USUARIO);

        assertThat(resultado.email())
                .isEqualTo(
                        "usuario@teste.com"
                );

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve detalhar usuário sem vínculo ativo na organização"
    )
    void naoDeveDetalharUsuarioSemVinculoAtivoNaOrganizacao() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.detalhar(ID_USUARIO)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario nao encontrado ou removido."
                );

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve permitir que usuário remova o próprio acesso"
    )
    void naoDevePermitirQueUsuarioRemovaOProprioAcesso() {
        when(usuarioLogadoService.getId())
                .thenReturn(ID_USUARIO);

        assertThatThrownBy(
                () -> service.excluir(ID_USUARIO)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "O usuario nao pode remover o proprio "
                                + "acesso a organizacao."
                );

        verify(usuarioLogadoService)
                .getId();

        verifyNoInteractions(
                contextoOrganizacao,
                usuarioOrganizacaoRepository,
                protecaoAdministradorOrganizacaoService
        );
    }

    @Test
    @DisplayName(
            "Deve validar proteção administrativa e inativar somente o vínculo"
    )
    void deveValidarProtecaoAdministrativaEInativarSomenteOVinculo() {
        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        ID_USUARIO,
                        "usuario@teste.com"
                );

        when(usuarioLogadoService.getId())
                .thenReturn(99L);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(vinculo)
        );

        service.excluir(ID_USUARIO);

        verify(usuarioLogadoService)
                .getId();

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verify(protecaoAdministradorOrganizacaoService)
                .validarInativacaoUsuario(
                        vinculo,
                        ID_ORGANIZACAO
                );

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.INATIVO);

        assertThat(vinculo.getUsuario().getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Não deve inativar vínculo quando proteção administrativa bloquear"
    )
    void naoDeveInativarVinculoQuandoProtecaoAdministrativaBloquear() {
        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        ID_USUARIO,
                        "administrador@teste.com"
                );

        when(usuarioLogadoService.getId())
                .thenReturn(99L);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(vinculo)
        );

        org.mockito.Mockito.doThrow(
                new ValidacaoException(
                        "A organização deve manter um administrador ativo."
                )
        ).when(
                protecaoAdministradorOrganizacaoService
        ).validarInativacaoUsuario(
                vinculo,
                ID_ORGANIZACAO
        );

        assertThatThrownBy(
                () -> service.excluir(ID_USUARIO)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "A organização deve manter um administrador ativo."
                );

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(protecaoAdministradorOrganizacaoService)
                .validarInativacaoUsuario(
                        vinculo,
                        ID_ORGANIZACAO
                );
    }

    @Test
    @DisplayName(
            "Não deve inativar usuário sem vínculo ativo na organização"
    )
    void naoDeveInativarUsuarioSemVinculoAtivoNaOrganizacao() {
        when(usuarioLogadoService.getId())
                .thenReturn(99L);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.excluir(ID_USUARIO)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario nao encontrado ou removido."
                );

        verifyNoInteractions(
                protecaoAdministradorOrganizacaoService
        );
    }

    @Test
    @DisplayName(
            "Deve reativar vínculo inativo na organização atual"
    )
    void deveReativarVinculoInativoNaOrganizacaoAtual() {
        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        ID_USUARIO,
                        "usuario@teste.com"
                );

        vinculo.inativar();

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.INATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.of(vinculo)
        );

        var resultado =
                service.reativar(ID_USUARIO);

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        assertThat(resultado.id())
                .isEqualTo(ID_USUARIO);

        assertThat(resultado.email())
                .isEqualTo(
                        "usuario@teste.com"
                );

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.INATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Não deve reativar vínculo inexistente"
    )
    void naoDeveReativarVinculoInexistente() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(ID_ORGANIZACAO);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.INATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.reativar(ID_USUARIO)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario inativo nao encontrado."
                );

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        ID_USUARIO,
                        ID_ORGANIZACAO,
                        StatusEnum.INATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName(
            "Deve carregar usuário autenticado pelo e-mail"
    )
    void deveCarregarUsuarioAutenticadoPeloEmail() {
        UsuarioAutenticado usuarioAutenticado =
                org.mockito.Mockito.mock(
                        UsuarioAutenticado.class
                );

        when(usuarioAutenticadoService.buscarPorEmail(
                "usuario@teste.com"
        )).thenReturn(usuarioAutenticado);

        var resultado =
                service.loadUserByUsername(
                        "usuario@teste.com"
                );

        assertThat(resultado)
                .isSameAs(usuarioAutenticado);

        verify(usuarioAutenticadoService)
                .buscarPorEmail(
                        "usuario@teste.com"
                );
    }

    @Test
    @DisplayName(
            "Deve lançar exceção quando usuário autenticado não for encontrado"
    )
    void deveLancarExcecaoQuandoUsuarioAutenticadoNaoForEncontrado() {
        when(usuarioAutenticadoService.buscarPorEmail(
                "usuario@teste.com"
        )).thenReturn(null);

        assertThatThrownBy(
                () -> service.loadUserByUsername(
                        "usuario@teste.com"
                )
        )
                .isInstanceOf(
                        UsernameNotFoundException.class
                )
                .hasMessage(
                        "Usuario nao encontrado"
                );

        verify(usuarioAutenticadoService)
                .buscarPorEmail(
                        "usuario@teste.com"
                );
    }

    private UsuarioOrganizacaoModel criarVinculo(
            Long idUsuario,
            String email
    ) {
        UsuarioModel usuario =
                criarUsuario(
                        idUsuario,
                        email
                );

        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        return new UsuarioOrganizacaoModel(
                usuario,
                organizacao
        );
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email
    ) {
        return new UsuarioModel(
                id,
                email,
                "senha-criptografada",
                StatusEnum.ATIVO
        );
    }
}