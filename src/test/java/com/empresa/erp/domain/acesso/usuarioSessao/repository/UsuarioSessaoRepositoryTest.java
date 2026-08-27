package com.empresa.erp.domain.acesso.usuarioSessao.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.empresa.erp.domain.acesso.usuarioSessao.model.UsuarioSessaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioSessaoRepositoryTest {

    @Autowired
    private UsuarioSessaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioModel usuario;

    private LocalDateTime referencia;

    @BeforeEach
    void setUp() {
        usuario =
                usuarioRepository.save(
                        new UsuarioModel(
                                new UsuarioRecord(
                                        "usuario@teste.com",
                                        "Senha@123"
                                ),
                                "senha-criptografada"
                        )
                );

        referencia =
                LocalDateTime.now();
    }

    @Test
    @DisplayName(
            "Deve buscar sessão pelo hash do refresh token e status"
    )
    void deveBuscarSessaoPeloHashDoRefreshTokenEStatus() {
        UsuarioSessaoModel sessaoAtiva =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-ativo",
                                "jti-ativo",
                                referencia.plusDays(1)
                        )
                );

        UsuarioSessaoModel sessaoInativa =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-inativo",
                                "jti-inativo",
                                referencia.plusDays(1)
                        )
                );

        sessaoInativa.revogar(
                usuario.getId(),
                "Logout"
        );

        repository.save(sessaoInativa);

        var resultadoAtivo =
                repository.findByRefreshTokenHashAndStatus(
                        "refresh-hash-ativo",
                        StatusEnum.ATIVO
                );

        var resultadoInativoComoAtivo =
                repository.findByRefreshTokenHashAndStatus(
                        "refresh-hash-inativo",
                        StatusEnum.ATIVO
                );

        assertThat(resultadoAtivo)
                .isPresent()
                .contains(sessaoAtiva);

        assertThat(resultadoInativoComoAtivo)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve listar sessões do usuário pelo usuário e status"
    )
    void deveListarSessoesDoUsuarioPeloUsuarioEStatus() {
        UsuarioSessaoModel sessaoAtiva =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-usuario-ativo",
                                "jti-usuario-ativo",
                                referencia.plusDays(1)
                        )
                );

        UsuarioSessaoModel sessaoInativa =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-usuario-inativo",
                                "jti-usuario-inativo",
                                referencia.plusDays(1)
                        )
                );

        sessaoInativa.revogar(
                usuario.getId(),
                "Revogação administrativa"
        );

        repository.save(sessaoInativa);

        var resultado =
                repository.findAllByUsuarioAndStatus(
                        usuario,
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .extracting(
                        UsuarioSessaoModel::getId
                )
                .containsExactly(
                        sessaoAtiva.getId()
                )
                .doesNotContain(
                        sessaoInativa.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve listar sessões pelo identificador do usuário e status"
    )
    void deveListarSessoesPeloIdentificadorDoUsuarioEStatus() {
        UsuarioSessaoModel sessaoDoUsuario =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-principal",
                                "jti-principal",
                                referencia.plusDays(1)
                        )
                );

        UsuarioModel outroUsuario =
                usuarioRepository.save(
                        new UsuarioModel(
                                new UsuarioRecord(
                                        "outro@teste.com",
                                        "Senha@123"
                                ),
                                "outra-senha-criptografada"
                        )
                );

        UsuarioSessaoModel sessaoDoOutroUsuario =
                repository.save(
                        criarSessao(
                                outroUsuario,
                                "refresh-hash-outro",
                                "jti-outro",
                                referencia.plusDays(1)
                        )
                );

        var resultado =
                repository.findAllByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .extracting(
                        UsuarioSessaoModel::getId
                )
                .containsExactly(
                        sessaoDoUsuario.getId()
                )
                .doesNotContain(
                        sessaoDoOutroUsuario.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve listar somente sessões ativas expiradas antes da referência"
    )
    void deveListarSomenteSessoesAtivasExpiradasAntesDaReferencia() {
        UsuarioSessaoModel sessaoExpiradaAtiva =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-expirado-ativo",
                                "jti-expirado-ativo",
                                referencia.minusHours(2)
                        )
                );

        UsuarioSessaoModel sessaoFutura =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-futuro",
                                "jti-futuro",
                                referencia.plusHours(2)
                        )
                );

        UsuarioSessaoModel sessaoExpiradaInativa =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-expirado-inativo",
                                "jti-expirado-inativo",
                                referencia.minusHours(3)
                        )
                );

        sessaoExpiradaInativa.revogar(
                usuario.getId(),
                "Sessão revogada"
        );

        repository.save(sessaoExpiradaInativa);

        var resultado =
                repository.findAllByExpiraEmBeforeAndStatus(
                        referencia,
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .extracting(
                        UsuarioSessaoModel::getId
                )
                .containsExactly(
                        sessaoExpiradaAtiva.getId()
                )
                .doesNotContain(
                        sessaoFutura.getId(),
                        sessaoExpiradaInativa.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve verificar a existência do JTI somente no status informado"
    )
    void deveVerificarExistenciaDoJtiSomenteNoStatusInformado() {
        repository.save(
                criarSessao(
                        usuario,
                        "refresh-hash-jti-ativo",
                        "jti-consultado",
                        referencia.plusDays(1)
                )
        );

        UsuarioSessaoModel sessaoInativa =
                repository.save(
                        criarSessao(
                                usuario,
                                "refresh-hash-jti-inativo",
                                "jti-revogado",
                                referencia.plusDays(1)
                        )
                );

        sessaoInativa.revogar(
                usuario.getId(),
                "Logout"
        );

        repository.save(sessaoInativa);

        boolean jtiAtivoExiste =
                repository.existsByAccessTokenJtiAndStatus(
                        "jti-consultado",
                        StatusEnum.ATIVO
                );

        boolean jtiRevogadoExisteComoAtivo =
                repository.existsByAccessTokenJtiAndStatus(
                        "jti-revogado",
                        StatusEnum.ATIVO
                );

        boolean jtiInexistenteExiste =
                repository.existsByAccessTokenJtiAndStatus(
                        "jti-inexistente",
                        StatusEnum.ATIVO
                );

        assertThat(jtiAtivoExiste)
                .isTrue();

        assertThat(jtiRevogadoExisteComoAtivo)
                .isFalse();

        assertThat(jtiInexistenteExiste)
                .isFalse();
    }

    private UsuarioSessaoModel criarSessao(
            UsuarioModel usuarioDaSessao,
            String refreshTokenHash,
            String accessTokenJti,
            LocalDateTime expiraEm
    ) {
        return new UsuarioSessaoModel(
                usuarioDaSessao,
                refreshTokenHash,
                accessTokenJti,
                expiraEm,
                "192.168.0.10",
                "Navegador de teste"
        );
    }
}