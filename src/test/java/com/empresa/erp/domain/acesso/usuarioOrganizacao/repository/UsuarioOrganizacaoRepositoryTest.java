package com.empresa.erp.domain.acesso.usuarioOrganizacao.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioOrganizacaoRepositoryTest {

    @Autowired
    private UsuarioOrganizacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    private UsuarioModel usuario;
    private OrganizacaoModel organizacao;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "usuario@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        organizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Organização Exemplo"
                )
        );
    }

    @Test
    @DisplayName("Deve listar organizações ativas do usuário")
    void deveListarOrganizacoesAtivasDoUsuario() {
        var vinculoAtivo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        var outraOrganizacao =
                organizacaoRepository.save(
                        new OrganizacaoModel(
                                "Outra Organização"
                        )
                );

        var vinculoInativo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        outraOrganizacao
                )
        );

        vinculoInativo.inativar();
        repository.save(vinculoInativo);

        var resultado =
                repository.findAllByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .extracting(UsuarioOrganizacaoModel::getId)
                .contains(vinculoAtivo.getId())
                .doesNotContain(vinculoInativo.getId());
    }

    @Test
    @DisplayName("Deve buscar vínculo independentemente do status")
    void deveBuscarVinculoIndependentementeDoStatus() {
        var vinculo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        vinculo.inativar();
        repository.save(vinculo);

        var resultado =
                repository
                        .findByUsuarioIdAndOrganizacaoId(
                                usuario.getId(),
                                organizacao.getId()
                        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve buscar vínculo ativo por usuário e organização")
    void deveBuscarVinculoAtivoPorUsuarioEOrganizacao() {
        var vinculo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        var resultado =
                repository
                        .findByUsuarioIdAndOrganizacaoIdAndStatus(
                                usuario.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(vinculo.getId());
    }

    @Test
    @DisplayName("Não deve buscar vínculo inativo como ativo")
    void naoDeveBuscarVinculoInativoComoAtivo() {
        var vinculo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        vinculo.inativar();
        repository.save(vinculo);

        var resultado =
                repository
                        .findByUsuarioIdAndOrganizacaoIdAndStatus(
                                usuario.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar acesso ativo à organização")
    void deveVerificarAcessoAtivoAOrganizacao() {
        repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        boolean existe =
                repository
                        .existsByUsuarioIdAndOrganizacaoIdAndStatus(
                                usuario.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Não deve permitir vínculo duplicado")
    void naoDevePermitirVinculoDuplicado() {
        repository.saveAndFlush(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        assertThatThrownBy(() ->
                repository.saveAndFlush(
                        new UsuarioOrganizacaoModel(
                                usuario,
                                organizacao
                        )
                )
        ).isInstanceOf(
                DataIntegrityViolationException.class
        );
    }
    
    @Test
    @DisplayName(
            "Deve listar organizações ativas disponíveis "
                    + "ordenadas pelo nome"
    )
    void deveListarOrganizacoesAtivasDisponiveisOrdenadasPeloNome() {
        var organizacaoB = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Organização B"
                )
        );

        var organizacaoA = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Organização A"
                )
        );

        var organizacaoInativa = new OrganizacaoModel(
                "Organização Inativa"
        );

        ReflectionTestUtils.setField(
                organizacaoInativa,
                "status",
                StatusEnum.INATIVO
        );

        organizacaoInativa =
                organizacaoRepository.save(
                        organizacaoInativa
                );

        repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacaoB
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacaoA
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacaoInativa
                )
        );

        var resultado =
                repository
                        .findAllByUsuarioIdAndStatusAndOrganizacaoStatusOrderByOrganizacaoNomeAsc(
                                usuario.getId(),
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado)
                .extracting(
                        vinculo ->
                                vinculo
                                        .getOrganizacao()
                                        .getNome()
                )
                .containsExactly(
                        "Organização A",
                        "Organização B"
                );
    }
    
    @Test
    @DisplayName(
            "Deve listar somente usuários ativos vinculados à organização"
    )
    void deveListarSomenteUsuariosAtivosVinculadosAOrganizacao() {
        var vinculoAtivo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        var usuarioComVinculoInativo = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "vinculo.inativo@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        var vinculoInativo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuarioComVinculoInativo,
                        organizacao
                )
        );

        vinculoInativo.inativar();
        repository.save(vinculoInativo);

        var usuarioInativo = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "usuario.inativo@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        usuarioInativo,
                        organizacao
                )
        );

        usuarioInativo.inativar();
        usuarioRepository.save(usuarioInativo);

        var outraOrganizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Outra Organização"
                )
        );

        var usuarioOutraOrganizacao = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "outra.organizacao@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        usuarioOutraOrganizacao,
                        outraOrganizacao
                )
        );

        var resultado =
                repository
                        .findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                                PageRequest.of(0, 10),
                                organizacao.getId(),
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent())
                .extracting(UsuarioOrganizacaoModel::getId)
                .containsExactly(vinculoAtivo.getId());
    }

    @Test
    @DisplayName(
            "Deve filtrar usuários ativos por e-mail dentro da organização"
    )
    void deveFiltrarUsuariosAtivosPorEmailDentroDaOrganizacao() {
        repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        var outroUsuario = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "outro@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        outroUsuario,
                        organizacao
                )
        );

        var outraOrganizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Outra Organização"
                )
        );

        var usuarioOutraOrganizacao = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "usuario@outra-organizacao.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        usuarioOutraOrganizacao,
                        outraOrganizacao
                )
        );

        var resultado =
                repository
                        .findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                                PageRequest.of(0, 10),
                                organizacao.getId(),
                                "USUARIO@TESTE",
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent())
                .extracting(
                        vinculo -> vinculo.getUsuario().getEmail()
                )
                .containsExactly("usuario@teste.com");
    }

    @Test
    @DisplayName(
            "Deve buscar usuário ativo somente dentro da organização informada"
    )
    void deveBuscarUsuarioAtivoSomenteDentroDaOrganizacaoInformada() {
        var vinculo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        var resultado =
                repository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                usuario.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId())
                .isEqualTo(vinculo.getId());

        var outraOrganizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Outra Organização"
                )
        );

        var resultadoOutraOrganizacao =
                repository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                usuario.getId(),
                                outraOrganizacao.getId(),
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoOutraOrganizacao).isEmpty();
    }

    @Test
    @DisplayName(
            "Não deve detalhar vínculo inativo ou usuário inativo"
    )
    void naoDeveDetalharVinculoInativoOuUsuarioInativo() {
        var vinculoInativo = repository.save(
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                )
        );

        vinculoInativo.inativar();
        repository.save(vinculoInativo);

        var resultadoVinculoInativo =
                repository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                usuario.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoVinculoInativo).isEmpty();

        var outroUsuario = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "usuario.inativo@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        repository.save(
                new UsuarioOrganizacaoModel(
                        outroUsuario,
                        organizacao
                )
        );

        outroUsuario.inativar();
        usuarioRepository.save(outroUsuario);

        var resultadoUsuarioInativo =
                repository
                        .findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                                outroUsuario.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO,
                                StatusEnum.ATIVO
                        );

        assertThat(resultadoUsuarioInativo).isEmpty();
    }
    
}