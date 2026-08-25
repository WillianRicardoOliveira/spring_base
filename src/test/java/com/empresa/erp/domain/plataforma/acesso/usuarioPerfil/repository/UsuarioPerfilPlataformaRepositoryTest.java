package com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.acesso.perfil.model.PerfilPlataformaModel;
import com.empresa.erp.domain.plataforma.acesso.perfil.repository.PerfilPlataformaRepository;
import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.model.PerfilPlataformaPermissaoModel;
import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.repository.PerfilPlataformaPermissaoRepository;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.model.UsuarioPerfilPlataformaModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioPerfilPlataformaRepositoryTest {

    @Autowired
    private UsuarioPerfilPlataformaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilPlataformaRepository
            perfilPlataformaRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private PerfilPlataformaPermissaoRepository
            perfilPlataformaPermissaoRepository;

    @Test
    @DisplayName(
            "Deve retornar permissão ativa da plataforma para usuário autorizado"
    )
    void deveRetornarPermissaoAtivaDaPlataformaParaUsuarioAutorizado() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .containsExactly(
                        "PLATAFORMA_ORGANIZACAO_LISTAR"
                );
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão de escopo organizacional"
    )
    void naoDeveRetornarPermissaoDeEscopoOrganizacional() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.ORGANIZACAO
                );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão para usuário inativo"
    )
    void naoDeveRetornarPermissaoParaUsuarioInativo() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        cenario.usuario().inativar();

        usuarioRepository.saveAndFlush(
                cenario.usuario()
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão quando perfil da plataforma estiver inativo"
    )
    void naoDeveRetornarPermissaoQuandoPerfilEstiverInativo() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        ReflectionTestUtils.setField(
                cenario.perfil(),
                "status",
                StatusEnum.INATIVO
        );

        perfilPlataformaRepository.saveAndFlush(
                cenario.perfil()
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão quando vínculo do usuário estiver inativo"
    )
    void naoDeveRetornarPermissaoQuandoVinculoDoUsuarioEstiverInativo() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        ReflectionTestUtils.setField(
                cenario.usuarioPerfil(),
                "status",
                StatusEnum.INATIVO
        );

        repository.saveAndFlush(
                cenario.usuarioPerfil()
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão quando vínculo da permissão estiver inativo"
    )
    void naoDeveRetornarPermissaoQuandoVinculoDaPermissaoEstiverInativo() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        ReflectionTestUtils.setField(
                cenario.perfilPermissao(),
                "status",
                StatusEnum.INATIVO
        );

        perfilPlataformaPermissaoRepository.saveAndFlush(
                cenario.perfilPermissao()
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão inativa"
    )
    void naoDeveRetornarPermissaoInativa() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        ReflectionTestUtils.setField(
                cenario.permissao(),
                "status",
                StatusEnum.INATIVO
        );

        permissaoRepository.saveAndFlush(
                cenario.permissao()
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                cenario.usuario().getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissões pertencentes a outro usuário"
    )
    void naoDeveRetornarPermissoesPertencentesAOutroUsuario() {
        Cenario cenario =
                salvarCenario(
                        EscopoPermissaoEnum.PLATAFORMA
                );

        UsuarioModel outroUsuario =
                salvarUsuario(
                        "outro.usuario@teste.com"
                );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuario(
                                outroUsuario.getId(),
                                EscopoPermissaoEnum.PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();

        assertThat(cenario.usuario().getId())
                .isNotEqualTo(
                        outroUsuario.getId()
                );
    }

    private Cenario salvarCenario(
            EscopoPermissaoEnum escopo
    ) {
        UsuarioModel usuario =
                salvarUsuario(
                        "administrador.plataforma@teste.com"
                );

        PerfilPlataformaModel perfil =
                perfilPlataformaRepository.save(
                        PerfilPlataformaModel
                                .criarAdministradorSistema()
                );

        PermissaoModel permissao =
                salvarPermissao(escopo);

        PerfilPlataformaPermissaoModel
                perfilPermissao =
                perfilPlataformaPermissaoRepository.save(
                        new PerfilPlataformaPermissaoModel(
                                perfil,
                                permissao
                        )
                );

        UsuarioPerfilPlataformaModel usuarioPerfil =
                repository.saveAndFlush(
                        new UsuarioPerfilPlataformaModel(
                                usuario,
                                perfil
                        )
                );

        return new Cenario(
                usuario,
                perfil,
                permissao,
                perfilPermissao,
                usuarioPerfil
        );
    }

    private UsuarioModel salvarUsuario(
            String email
    ) {
        return usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                email,
                                "Senha@2026"
                        ),
                        "senha-criptografada"
                )
        );
    }

    private PermissaoModel salvarPermissao(
            EscopoPermissaoEnum escopo
    ) {
        PermissaoModel permissao =
                instanciarPermissao();

        ReflectionTestUtils.setField(
                permissao,
                "nome",
                "Listar organizações"
        );

        ReflectionTestUtils.setField(
                permissao,
                "chave",
                "PLATAFORMA_ORGANIZACAO_LISTAR"
        );

        ReflectionTestUtils.setField(
                permissao,
                "descricao",
                "Permite listar organizações"
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
                StatusEnum.ATIVO
        );

        return permissaoRepository.save(
                permissao
        );
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

    private record Cenario(
            UsuarioModel usuario,
            PerfilPlataformaModel perfil,
            PermissaoModel permissao,
            PerfilPlataformaPermissaoModel perfilPermissao,
            UsuarioPerfilPlataformaModel usuarioPerfil
    ) {
    }
}