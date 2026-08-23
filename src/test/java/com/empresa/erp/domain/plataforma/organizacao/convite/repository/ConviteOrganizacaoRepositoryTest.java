package com.empresa.erp.domain.plataforma.organizacao.convite.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ConviteOrganizacaoRepositoryTest {

    private static final LocalDateTime AGORA =
            LocalDateTime.of(
                    2026,
                    8,
                    23,
                    10,
                    0
            );

    @Autowired
    private ConviteOrganizacaoRepository repository;

    @Test
    @DisplayName(
            "Deve salvar convite pendente"
    )
    void deveSalvarConvitePendente() {
        var convite =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Principal",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        assertThat(convite.getId())
                .isNotNull();

        assertThat(convite.getNomeOrganizacao())
                .isEqualTo(
                        "Organização Principal"
                );

        assertThat(convite.getEmailAdministrador())
                .isEqualTo(
                        "admin@teste.com"
                );

        assertThat(convite.getTokenHash())
                .isEqualTo(
                        "hash-token-1"
                );

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(convite.getExpiraEm())
                .isEqualTo(
                        AGORA.plusHours(48)
                );
    }

    @Test
    @DisplayName(
            "Deve buscar convite pendente por e-mail para atualização"
    )
    void deveBuscarConvitePendentePorEmailParaAtualizacao() {
        var convite =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Principal",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        var resultado =
                repository
                        .buscarPendentePorEmailParaAtualizacao(
                                "admin@teste.com"
                        );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(convite.getId());

        assertThat(resultado.get().getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );
    }

    @Test
    @DisplayName(
            "Não deve buscar convite revogado como pendente por e-mail"
    )
    void naoDeveBuscarConviteRevogadoComoPendentePorEmail() {
        var convite =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Principal",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        convite.revogar();

        repository.saveAndFlush(convite);

        var resultado =
                repository
                        .buscarPendentePorEmailParaAtualizacao(
                                "admin@teste.com"
                        );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve buscar convite pendente por hash e status"
    )
    void deveBuscarConvitePendentePorHashEStatus() {
        var convite =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Principal",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        var resultado =
                repository.buscarPorTokenHashEStatus(
                        "hash-token-1",
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(convite.getId());
    }

    @Test
    @DisplayName(
            "Não deve buscar convite por status diferente"
    )
    void naoDeveBuscarConvitePorStatusDiferente() {
        repository.saveAndFlush(
                criarConvite(
                        "Organização Principal",
                        "admin@teste.com",
                        "hash-token-1"
                )
        );

        var resultado =
                repository.buscarPorTokenHashEStatus(
                        "hash-token-1",
                        StatusConviteOrganizacaoEnum.ACEITO
                );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve buscar convite por hash e status para atualização"
    )
    void deveBuscarConvitePorHashEStatusParaAtualizacao() {
        var convite =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Principal",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        var resultado =
                repository
                        .buscarPorTokenHashEStatusParaAtualizacao(
                                "hash-token-1",
                                StatusConviteOrganizacaoEnum.PENDENTE
                        );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(convite.getId());
    }

    @Test
    @DisplayName(
            "Deve buscar convite por ID para atualização"
    )
    void deveBuscarConvitePorIdParaAtualizacao() {
        var convite =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Principal",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        var resultado =
                repository
                        .buscarPorIdParaAtualizacao(
                                convite.getId()
                        );

        assertThat(resultado)
                .isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(convite.getId());
    }

    @Test
    @DisplayName(
            "Deve listar convites sem filtro e sem status"
    )
    void deveListarConvitesSemFiltroESemStatus() {
        repository.saveAndFlush(
                criarConvite(
                        "Organização Alfa",
                        "admin@alfa.com",
                        "hash-token-alfa"
                )
        );

        var conviteAceito =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Beta",
                                "admin@beta.com",
                                "hash-token-beta"
                        )
                );

        conviteAceito.aceitar(AGORA);

        repository.saveAndFlush(
                conviteAceito
        );

        var resultado =
                repository.listar(
                        PageRequest.of(0, 10),
                        null,
                        null
                );

        assertThat(resultado.getContent())
                .hasSize(2);

        assertThat(resultado.getContent())
                .extracting(
                        ConviteOrganizacaoModel::getNomeOrganizacao
                )
                .containsExactlyInAnyOrder(
                        "Organização Alfa",
                        "Organização Beta"
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar convites pelo nome da organização"
    )
    void deveFiltrarConvitesPeloNomeDaOrganizacao() {
        repository.saveAndFlush(
                criarConvite(
                        "Organização Alfa",
                        "admin@alfa.com",
                        "hash-token-alfa"
                )
        );

        repository.saveAndFlush(
                criarConvite(
                        "Organização Beta",
                        "admin@beta.com",
                        "hash-token-beta"
                )
        );

        var resultado =
                repository.listar(
                        PageRequest.of(0, 10),
                        "ALFA",
                        null
                );

        assertThat(resultado.getContent())
                .extracting(
                        ConviteOrganizacaoModel::getNomeOrganizacao
                )
                .containsExactly(
                        "Organização Alfa"
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar convites pelo e-mail do administrador"
    )
    void deveFiltrarConvitesPeloEmailDoAdministrador() {
        repository.saveAndFlush(
                criarConvite(
                        "Organização Alfa",
                        "financeiro@alfa.com",
                        "hash-token-alfa"
                )
        );

        repository.saveAndFlush(
                criarConvite(
                        "Organização Beta",
                        "admin@beta.com",
                        "hash-token-beta"
                )
        );

        var resultado =
                repository.listar(
                        PageRequest.of(0, 10),
                        "FINANCEIRO",
                        null
                );

        assertThat(resultado.getContent())
                .extracting(
                        ConviteOrganizacaoModel::getEmailAdministrador
                )
                .containsExactly(
                        "financeiro@alfa.com"
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar convites pelo status"
    )
    void deveFiltrarConvitesPeloStatus() {
        repository.saveAndFlush(
                criarConvite(
                        "Organização Pendente",
                        "pendente@teste.com",
                        "hash-token-pendente"
                )
        );

        var conviteAceito =
                repository.saveAndFlush(
                        criarConvite(
                                "Organização Aceita",
                                "aceito@teste.com",
                                "hash-token-aceito"
                        )
                );

        conviteAceito.aceitar(AGORA);

        repository.saveAndFlush(
                conviteAceito
        );

        var resultado =
                repository.listar(
                        PageRequest.of(0, 10),
                        null,
                        StatusConviteOrganizacaoEnum.ACEITO
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getStatus()
        ).isEqualTo(
                StatusConviteOrganizacaoEnum.ACEITO
        );

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getNomeOrganizacao()
        ).isEqualTo(
                "Organização Aceita"
        );
    }

    @Test
    @DisplayName(
            "Não deve permitir dois convites pendentes para o mesmo e-mail"
    )
    void naoDevePermitirDoisConvitesPendentesParaOMesmoEmail() {
        repository.saveAndFlush(
                criarConvite(
                        "Primeira Organização",
                        "admin@teste.com",
                        "hash-token-1"
                )
        );

        assertThatThrownBy(
                () -> repository.saveAndFlush(
                        criarConvite(
                                "Segunda Organização",
                                "admin@teste.com",
                                "hash-token-2"
                        )
                )
        ).isInstanceOf(
                DataIntegrityViolationException.class
        );
    }

    @Test
    @DisplayName(
            "Deve permitir novo convite após revogação do anterior"
    )
    void devePermitirNovoConviteAposRevogacaoDoAnterior() {
        var conviteAnterior =
                repository.saveAndFlush(
                        criarConvite(
                                "Primeira Organização",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        conviteAnterior.revogar();

        repository.saveAndFlush(
                conviteAnterior
        );

        var novoConvite =
                repository.saveAndFlush(
                        criarConvite(
                                "Segunda Organização",
                                "admin@teste.com",
                                "hash-token-2"
                        )
                );

        assertThat(novoConvite.getId())
                .isNotNull();

        assertThat(novoConvite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );
    }

    @Test
    @DisplayName(
            "Deve permitir novo convite após aceite do anterior"
    )
    void devePermitirNovoConviteAposAceiteDoAnterior() {
        var conviteAnterior =
                repository.saveAndFlush(
                        criarConvite(
                                "Primeira Organização",
                                "admin@teste.com",
                                "hash-token-1"
                        )
                );

        conviteAnterior.aceitar(AGORA);

        repository.saveAndFlush(
                conviteAnterior
        );

        var novoConvite =
                repository.saveAndFlush(
                        criarConvite(
                                "Segunda Organização",
                                "admin@teste.com",
                                "hash-token-2"
                        )
                );

        assertThat(novoConvite.getId())
                .isNotNull();

        assertThat(novoConvite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );
    }

    @Test
    @DisplayName(
            "Não deve permitir token hash duplicado"
    )
    void naoDevePermitirTokenHashDuplicado() {
        repository.saveAndFlush(
                criarConvite(
                        "Organização Alfa",
                        "admin@alfa.com",
                        "hash-token-repetido"
                )
        );

        assertThatThrownBy(
                () -> repository.saveAndFlush(
                        criarConvite(
                                "Organização Beta",
                                "admin@beta.com",
                                "hash-token-repetido"
                        )
                )
        ).isInstanceOf(
                DataIntegrityViolationException.class
        );
    }

    private ConviteOrganizacaoModel criarConvite(
            String nomeOrganizacao,
            String emailAdministrador,
            String tokenHash
    ) {
        return new ConviteOrganizacaoModel(
                nomeOrganizacao,
                emailAdministrador,
                tokenHash,
                AGORA.plusHours(48)
        );
    }
}