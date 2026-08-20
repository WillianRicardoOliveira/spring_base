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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private OrganizacaoRepository
            organizacaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioAutenticadoService
            usuarioAutenticadoService;

    @Mock
    private ContextoOrganizacao
            contextoOrganizacao;

    @InjectMocks
    private UsuarioService service;

    @Test
    @DisplayName(
            "Deve cadastrar usuario e vincular à organizacao atual"
    )
    void deveCadastrarUsuarioEVincularAOrganizacaoAtual() {
        var dados = new UsuarioRecord(
                "Usuario@Teste.com",
                "Senha@123"
        );

        var organizacao = new OrganizacaoModel(
                "Organização Exemplo"
        );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                10L
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(repository.existsByEmailIgnoreCase(
                "Usuario@Teste.com"
        )).thenReturn(false);

        when(passwordEncoder.encode("Senha@123"))
                .thenReturn("senha-criptografada");

        when(organizacaoRepository.getReferenceById(10L))
                .thenReturn(organizacao);

        UsuarioModel usuario =
                service.cadastrar(dados);

        assertThat(usuario.getEmail())
                .isEqualTo("usuario@teste.com");

        assertThat(usuario.getSenha())
                .isEqualTo("senha-criptografada");

        assertThat(usuario.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(repository)
                .save(usuario);

        verify(organizacaoRepository)
                .getReferenceById(10L);

        var captor = ArgumentCaptor.forClass(
                UsuarioOrganizacaoModel.class
        );

        verify(usuarioOrganizacaoRepository)
                .save(captor.capture());

        var vinculo = captor.getValue();

        assertThat(vinculo.getUsuario())
                .isSameAs(usuario);

        assertThat(vinculo.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve bloquear cadastro de usuario duplicado "
                    + "sem criar vinculo"
    )
    void deveBloquearCadastroDeUsuarioDuplicadoSemCriarVinculo() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "Senha@123"
        );

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(repository.existsByEmailIgnoreCase(
                "usuario@teste.com"
        )).thenReturn(true);

        assertThatThrownBy(() ->
                service.cadastrar(dados)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario ja cadastrado.");

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verifyNoInteractions(
                organizacaoRepository,
                usuarioOrganizacaoRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName(
            "Deve listar usuarios ativos da organizacao sem filtro"
    )
    void deveListarUsuariosAtivosDaOrganizacaoSemFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var usuario = criarUsuario(
                1L,
                "usuario@teste.com"
        );

        var vinculo = criarVinculo(usuario);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                        paginacao,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of(vinculo))
        );

        var resultado = service.listar(
                paginacao,
                null
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(1L);

        assertThat(resultado.getContent().get(0).email())
                .isEqualTo("usuario@teste.com");

        assertThat(resultado.getContent().get(0).status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                        paginacao,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName(
            "Deve listar usuarios ativos da organizacao com filtro"
    )
    void deveListarUsuariosAtivosDaOrganizacaoComFiltro() {
        var paginacao = PageRequest.of(0, 10);

        var usuario = criarUsuario(
                1L,
                "financeiro@teste.com"
        );

        var vinculo = criarVinculo(usuario);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                        paginacao,
                        10L,
                        "fin",
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                new PageImpl<>(List.of(vinculo))
        );

        var resultado = service.listar(
                paginacao,
                " fin "
        );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(resultado.getContent().get(0).id())
                .isEqualTo(1L);

        assertThat(resultado.getContent().get(0).email())
                .isEqualTo("financeiro@teste.com");

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                        paginacao,
                        10L,
                        "fin",
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName(
            "Deve detalhar usuario ativo da organizacao"
    )
    void deveDetalharUsuarioAtivoDaOrganizacao() {
        var usuario = criarUsuario(
                1L,
                "usuario@teste.com"
        );

        var vinculo = criarVinculo(usuario);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(vinculo));

        var resultado = service.detalhar(1L);

        assertThat(resultado.id())
                .isEqualTo(1L);

        assertThat(resultado.email())
                .isEqualTo("usuario@teste.com");

        assertThat(resultado.status())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName(
            "Deve bloquear detalhamento de usuario sem vinculo ativo "
                    + "na organizacao"
    )
    void deveBloquearDetalhamentoDeUsuarioSemVinculoAtivoNaOrganizacao() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.detalhar(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario nao encontrado ou removido."
                );

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName(
            "Deve inativar somente o vinculo do usuario "
                    + "com a organizacao atual"
    )
    void deveInativarSomenteVinculoDoUsuarioComOrganizacaoAtual() {
        var usuario = criarUsuario(
                1L,
                "usuario@teste.com"
        );

        var vinculo = criarVinculo(usuario);

        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.of(vinculo));

        service.excluir(1L);

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.INATIVO);

        assertThat(usuario.getStatus())
                .isEqualTo(StatusEnum.ATIVO);

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName(
            "Deve bloquear exclusao de usuario sem vinculo ativo "
                    + "na organizacao atual"
    )
    void deveBloquearExclusaoDeUsuarioSemVinculoAtivoNaOrganizacaoAtual() {
        when(contextoOrganizacao.getIdOrganizacao())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.excluir(1L)
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage(
                        "Usuario nao encontrado ou removido."
                );

        verify(contextoOrganizacao)
                .getIdOrganizacao();

        verify(usuarioOrganizacaoRepository)
                .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                        1L,
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName(
            "Deve carregar usuario autenticado pelo email"
    )
    void deveCarregarUsuarioAutenticadoPeloEmail() {
        var usuarioAutenticado =
                org.mockito.Mockito.mock(
                        UsuarioAutenticado.class
                );

        when(usuarioAutenticadoService.buscarPorEmail(
                "usuario@teste.com"
        )).thenReturn(usuarioAutenticado);

        var resultado = service.loadUserByUsername(
                "usuario@teste.com"
        );

        assertThat(resultado)
                .isEqualTo(usuarioAutenticado);
    }

    @Test
    @DisplayName(
            "Deve lancar excecao quando usuario autenticado "
                    + "nao for encontrado"
    )
    void deveLancarExcecaoQuandoUsuarioAutenticadoNaoForEncontrado() {
        when(usuarioAutenticadoService.buscarPorEmail(
                "usuario@teste.com"
        )).thenReturn(null);

        assertThatThrownBy(() ->
                service.loadUserByUsername(
                        "usuario@teste.com"
                )
        )
                .isInstanceOf(
                        UsernameNotFoundException.class
                )
                .hasMessage("Usuario nao encontrado");
    }

    private UsuarioOrganizacaoModel criarVinculo(
            UsuarioModel usuario
    ) {
        var organizacao = new OrganizacaoModel(
                "Organização Exemplo"
        );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                10L
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
        var usuario = new UsuarioModel(
                new UsuarioRecord(
                        email,
                        "Senha@123"
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
}