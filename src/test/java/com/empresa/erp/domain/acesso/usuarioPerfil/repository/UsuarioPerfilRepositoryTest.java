package com.empresa.erp.domain.acesso.usuarioPerfil.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.model.TipoPerfilSistemaEnum;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioPerfilRepositoryTest {

    @Autowired
    private UsuarioPerfilRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Autowired
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private PerfilPermissaoRepository
            perfilPermissaoRepository;

    @Test
    @DisplayName(
            "Deve listar vínculos ativos do usuário na organização"
    )
    void deveListarVinculosAtivosDoUsuarioNaOrganizacao() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfilAtivo =
                salvarPerfil(
                        organizacao,
                        "Perfil ativo"
                );

        var perfilRemovido =
                salvarPerfil(
                        organizacao,
                        "Perfil removido"
                );

        var vinculoAtivo =
                repository.save(
                        new UsuarioPerfilModel(
                                vinculoOrganizacao,
                                perfilAtivo
                        )
                );

        var vinculoRemovido =
                repository.save(
                        new UsuarioPerfilModel(
                                vinculoOrganizacao,
                                perfilRemovido
                        )
                );

        vinculoRemovido.remover(10L);

        repository.saveAndFlush(
                vinculoRemovido
        );

        var resultado =
                repository
                        .findAllByUsuarioOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                vinculoOrganizacao.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .extracting(
                        UsuarioPerfilModel::getId
                )
                .containsExactly(
                        vinculoAtivo.getId()
                );
    }

    @Test
    @DisplayName(
            "Não deve listar perfil pertencente a outra organização"
    )
    void naoDeveListarPerfilPertencenteAOutraOrganizacao() {
        var organizacaoA =
                salvarOrganizacao(
                        "Organização A"
                );

        var organizacaoB =
                salvarOrganizacao(
                        "Organização B"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacaoA =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacaoA
                );

        var perfilOrganizacaoA =
                salvarPerfil(
                        organizacaoA,
                        "Perfil A"
                );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoOrganizacaoA,
                        perfilOrganizacaoA
                )
        );

        var resultado =
                repository
                        .findAllByUsuarioOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                vinculoOrganizacaoA.getId(),
                                organizacaoB.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve verificar vínculo ativo pelo usuário, perfil e organização"
    )
    void deveVerificarVinculoAtivoPeloUsuarioPerfilEOrganizacao() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfil
                )
        );

        boolean existe =
                repository
                        .existsByUsuarioOrganizacaoIdAndPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                vinculoOrganizacao.getId(),
                                perfil.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(existe)
                .isTrue();
    }

    @Test
    @DisplayName(
            "Não deve encontrar vínculo usando outra organização"
    )
    void naoDeveEncontrarVinculoUsandoOutraOrganizacao() {
        var organizacaoA =
                salvarOrganizacao(
                        "Organização A"
                );

        var organizacaoB =
                salvarOrganizacao(
                        "Organização B"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacaoA =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacaoA
                );

        var perfilOrganizacaoA =
                salvarPerfil(
                        organizacaoA,
                        "Financeiro"
                );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoOrganizacaoA,
                        perfilOrganizacaoA
                )
        );

        boolean existe =
                repository
                        .existsByUsuarioOrganizacaoIdAndPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                vinculoOrganizacaoA.getId(),
                                perfilOrganizacaoA.getId(),
                                organizacaoB.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(existe)
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve detalhar vínculo somente dentro da mesma organização"
    )
    void deveDetalharVinculoSomenteDentroDaMesmaOrganizacao() {
        var organizacaoA =
                salvarOrganizacao(
                        "Organização A"
                );

        var organizacaoB =
                salvarOrganizacao(
                        "Organização B"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacaoA =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacaoA
                );

        var perfilOrganizacaoA =
                salvarPerfil(
                        organizacaoA,
                        "Financeiro"
                );

        var usuarioPerfil =
                repository.saveAndFlush(
                        new UsuarioPerfilModel(
                                vinculoOrganizacaoA,
                                perfilOrganizacaoA
                        )
                );

        var resultadoCorreto =
                repository
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                usuarioPerfil.getId(),
                                organizacaoA.getId(),
                                organizacaoA.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoCorreto)
                .isPresent();

        var resultadoOutraOrganizacao =
                repository
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                usuarioPerfil.getId(),
                                organizacaoB.getId(),
                                organizacaoB.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoOutraOrganizacao)
                .isEmpty();

        var resultadoOrganizacoesDivergentes =
                repository
                        .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                                usuarioPerfil.getId(),
                                organizacaoA.getId(),
                                organizacaoB.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoOrganizacoesDivergentes)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve verificar existência de vínculo ativo no perfil"
    )
    void deveVerificarExistenciaDeVinculoAtivoNoPerfil() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfil =
                salvarPerfil(
                        organizacao,
                        "Financeiro"
                );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfil
                )
        );

        boolean existe =
                repository
                        .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                perfil.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(existe)
                .isTrue();

        boolean existeEmOutraOrganizacao =
                repository
                        .existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                                perfil.getId(),
                                organizacao.getId() + 999L,
                                StatusEnum.ATIVO
                        );

        assertThat(existeEmOutraOrganizacao)
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve identificar perfil administrador ativo"
    )
    void deveIdentificarPerfilAdministradorAtivo() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "admin@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfilAdministrador =
                perfilRepository.save(
                        PerfilModel
                                .criarAdministradorSistema(
                                        organizacao
                                )
                );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfilAdministrador
                )
        );

        boolean resultado =
                repository
                        .possuiPerfilAdministradorAtivo(
                                vinculoOrganizacao.getId(),
                                TipoPerfilSistemaEnum.ADMINISTRADOR,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isTrue();
    }

    @Test
    @DisplayName(
            "Não deve considerar perfil comum como administrador"
    )
    void naoDeveConsiderarPerfilComumComoAdministrador() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfilComum =
                salvarPerfil(
                        organizacao,
                        "Administrador personalizado"
                );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfilComum
                )
        );

        boolean resultado =
                repository
                        .possuiPerfilAdministradorAtivo(
                                vinculoOrganizacao.getId(),
                                TipoPerfilSistemaEnum.ADMINISTRADOR,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve identificar outro administrador ativo da organização"
    )
    void deveIdentificarOutroAdministradorAtivoDaOrganizacao() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var primeiroUsuario =
                salvarUsuario(
                        "primeiro.admin@teste.com"
                );

        var segundoUsuario =
                salvarUsuario(
                        "segundo.admin@teste.com"
                );

        var primeiroVinculo =
                salvarVinculoOrganizacao(
                        primeiroUsuario,
                        organizacao
                );

        var segundoVinculo =
                salvarVinculoOrganizacao(
                        segundoUsuario,
                        organizacao
                );

        var perfilAdministrador =
                perfilRepository.save(
                        PerfilModel
                                .criarAdministradorSistema(
                                        organizacao
                                )
                );

        repository.save(
                new UsuarioPerfilModel(
                        primeiroVinculo,
                        perfilAdministrador
                )
        );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        segundoVinculo,
                        perfilAdministrador
                )
        );

        boolean resultado =
                repository
                        .existeOutroAdministradorAtivo(
                                organizacao.getId(),
                                primeiroVinculo.getId(),
                                TipoPerfilSistemaEnum.ADMINISTRADOR,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isTrue();
    }

    @Test
    @DisplayName(
            "Não deve encontrar outro administrador em outra organização"
    )
    void naoDeveEncontrarOutroAdministradorEmOutraOrganizacao() {
        var organizacaoA =
                salvarOrganizacao(
                        "Organização A"
                );

        var organizacaoB =
                salvarOrganizacao(
                        "Organização B"
                );

        var usuarioA =
                salvarUsuario(
                        "admin.a@teste.com"
                );

        var usuarioB =
                salvarUsuario(
                        "admin.b@teste.com"
                );

        var vinculoA =
                salvarVinculoOrganizacao(
                        usuarioA,
                        organizacaoA
                );

        var vinculoB =
                salvarVinculoOrganizacao(
                        usuarioB,
                        organizacaoB
                );

        var perfilAdministradorA =
                perfilRepository.save(
                        PerfilModel
                                .criarAdministradorSistema(
                                        organizacaoA
                                )
                );

        var perfilAdministradorB =
                perfilRepository.save(
                        PerfilModel
                                .criarAdministradorSistema(
                                        organizacaoB
                                )
                );

        repository.save(
                new UsuarioPerfilModel(
                        vinculoA,
                        perfilAdministradorA
                )
        );

        repository.saveAndFlush(
                new UsuarioPerfilModel(
                        vinculoB,
                        perfilAdministradorB
                )
        );

        boolean resultado =
                repository
                        .existeOutroAdministradorAtivo(
                                organizacaoA.getId(),
                                vinculoA.getId(),
                                TipoPerfilSistemaEnum.ADMINISTRADOR,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve buscar somente permissões ativas da organização informada"
    )
    void deveBuscarSomentePermissoesAtivasDaOrganizacaoInformada() {
        var organizacaoA =
                salvarOrganizacao(
                        "Organização A"
                );

        var organizacaoB =
                salvarOrganizacao(
                        "Organização B"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoA =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacaoA
                );

        var vinculoB =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacaoB
                );

        var perfilA =
                salvarPerfil(
                        organizacaoA,
                        "Perfil A"
                );

        var perfilB =
                salvarPerfil(
                        organizacaoB,
                        "Perfil B"
                );

        var permissaoA =
                salvarPermissao(
                        "Listar empresas",
                        "EMPRESA_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var permissaoB =
                salvarPermissao(
                        "Listar usuários",
                        "USUARIO_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        repository.save(
                new UsuarioPerfilModel(
                        vinculoA,
                        perfilA
                )
        );

        repository.save(
                new UsuarioPerfilModel(
                        vinculoB,
                        perfilB
                )
        );

        perfilPermissaoRepository.save(
                new PerfilPermissaoModel(
                        perfilA,
                        permissaoA
                )
        );

        perfilPermissaoRepository.saveAndFlush(
                new PerfilPermissaoModel(
                        perfilB,
                        permissaoB
                )
        );

        var resultadoOrganizacaoA =
                repository
                        .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                                usuario.getId(),
                                organizacaoA.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoOrganizacaoA)
                .containsExactly(
                        "EMPRESA_LISTAR"
                );

        var resultadoOrganizacaoB =
                repository
                        .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                                usuario.getId(),
                                organizacaoB.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoOrganizacaoB)
                .containsExactly(
                        "USUARIO_LISTAR"
                );
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão de escopo plataforma"
    )
    void naoDeveRetornarPermissaoDeEscopoPlataforma() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfil =
                salvarPerfil(
                        organizacao,
                        "Perfil"
                );

        var permissaoOrganizacao =
                salvarPermissao(
                        "Listar empresas",
                        "EMPRESA_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        var permissaoPlataforma =
                salvarPermissao(
                        "Listar organizações",
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        EscopoPermissaoEnum.PLATAFORMA,
                        StatusEnum.ATIVO
                );

        repository.save(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfil
                )
        );

        perfilPermissaoRepository.save(
                new PerfilPermissaoModel(
                        perfil,
                        permissaoOrganizacao
                )
        );

        perfilPermissaoRepository.saveAndFlush(
                new PerfilPermissaoModel(
                        perfil,
                        permissaoPlataforma
                )
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                                usuario.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .containsExactly(
                        "EMPRESA_LISTAR"
                );
    }

    @Test
    @DisplayName(
            "Não deve retornar permissão com vínculo removido"
    )
    void naoDeveRetornarPermissaoComVinculoRemovido() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfil =
                salvarPerfil(
                        organizacao,
                        "Perfil"
                );

        var permissao =
                salvarPermissao(
                        "Listar empresas",
                        "EMPRESA_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        repository.save(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfil
                )
        );

        var perfilPermissao =
                perfilPermissaoRepository.save(
                        new PerfilPermissaoModel(
                                perfil,
                                permissao
                        )
                );

        perfilPermissao.remover(10L);

        perfilPermissaoRepository.saveAndFlush(
                perfilPermissao
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                                usuario.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve retornar permissões de vínculo de usuário inativo"
    )
    void naoDeveRetornarPermissoesDeVinculoDeUsuarioInativo() {
        var organizacao =
                salvarOrganizacao(
                        "Organização Principal"
                );

        var usuario =
                salvarUsuario(
                        "usuario@teste.com"
                );

        var vinculoOrganizacao =
                salvarVinculoOrganizacao(
                        usuario,
                        organizacao
                );

        var perfil =
                salvarPerfil(
                        organizacao,
                        "Perfil"
                );

        var permissao =
                salvarPermissao(
                        "Listar empresas",
                        "EMPRESA_LISTAR",
                        EscopoPermissaoEnum.ORGANIZACAO,
                        StatusEnum.ATIVO
                );

        repository.save(
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfil
                )
        );

        perfilPermissaoRepository.save(
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                )
        );

        vinculoOrganizacao.inativar();

        usuarioOrganizacaoRepository.saveAndFlush(
                vinculoOrganizacao
        );

        var resultado =
                repository
                        .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                                usuario.getId(),
                                organizacao.getId(),
                                EscopoPermissaoEnum.ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .isEmpty();
    }

    private UsuarioModel salvarUsuario(
            String email
    ) {
        return usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                email,
                                "Senha@123"
                        ),
                        "senha-criptografada"
                )
        );
    }

    private OrganizacaoModel salvarOrganizacao(
            String nome
    ) {
        return organizacaoRepository.save(
                new OrganizacaoModel(nome)
        );
    }

    private UsuarioOrganizacaoModel
            salvarVinculoOrganizacao(
                    UsuarioModel usuario,
                    OrganizacaoModel organizacao
            ) {
        return usuarioOrganizacaoRepository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );
    }

    private PerfilModel salvarPerfil(
            OrganizacaoModel organizacao,
            String nome
    ) {
        return perfilRepository.save(
                new PerfilModel(
                        organizacao,
                        new PerfilRecord(
                                nome,
                                "Descrição do perfil"
                        )
                )
        );
    }

    private PermissaoModel salvarPermissao(
            String nome,
            String chave,
            EscopoPermissaoEnum escopo,
            StatusEnum status
    ) {
        var permissao =
                instanciarPermissao();

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
                "Descrição da permissão"
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
}