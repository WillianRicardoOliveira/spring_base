package com.empresa.erp.domain.acesso.perfil.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PerfilRepositoryTest {

    @Autowired
    private PerfilRepository repository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    private OrganizacaoModel organizacao;
    private OrganizacaoModel outraOrganizacao;

    @BeforeEach
    void setUp() {
        organizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Organizacao Principal"
                )
        );

        outraOrganizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Outra Organizacao"
                )
        );
    }

    @Test
    @DisplayName("Deve listar perfis ativos da organizacao")
    void deveListarPerfisAtivosDaOrganizacao() {
        var perfilAtivo = criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        var perfilInativo = criarPerfil(
                organizacao,
                "Compras",
                "Perfil compras"
        );

        perfilInativo.inativar();
        repository.save(perfilInativo);

        var perfilOutraOrganizacao = criarPerfil(
                outraOrganizacao,
                "Financeiro Externo",
                "Perfil externo"
        );

        var resultado = repository
                .findAllByOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(PerfilModel::getId)
                .containsExactly(perfilAtivo.getId())
                .doesNotContain(
                        perfilInativo.getId(),
                        perfilOutraOrganizacao.getId()
                );
    }

    @Test
    @DisplayName("Deve filtrar perfis por nome dentro da organizacao")
    void deveFiltrarPerfisPorNomeDentroDaOrganizacao() {
        criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        criarPerfil(
                organizacao,
                "Administrador",
                "Perfil administrador"
        );

        criarPerfil(
                outraOrganizacao,
                "Financeiro Externo",
                "Perfil externo"
        );

        var resultado = repository
                .findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        "FIN",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(PerfilModel::getNome)
                .containsExactly("Financeiro");
    }

    @Test
    @DisplayName("Deve verificar existencia por nome na organizacao")
    void deveVerificarExistenciaPorNomeNaOrganizacao() {
        criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        boolean existe = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        organizacao.getId(),
                        "financeiro",
                        StatusEnum.ATIVO
                );

        boolean existeEmOutraOrganizacao = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        outraOrganizacao.getId(),
                        "financeiro",
                        StatusEnum.ATIVO
                );

        assertThat(existe)
                .isTrue();

        assertThat(existeEmOutraOrganizacao)
                .isFalse();
    }

    @Test
    @DisplayName("Deve verificar existencia por nome excluindo id")
    void deveVerificarExistenciaPorNomeExcluindoId() {
        var perfil = criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        criarPerfil(
                organizacao,
                "Administrador",
                "Perfil administrador"
        );

        var existeEmOutroRegistro = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        organizacao.getId(),
                        "administrador",
                        StatusEnum.ATIVO,
                        perfil.getId()
                );

        var naoExisteNoMesmoRegistro = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        organizacao.getId(),
                        "financeiro",
                        StatusEnum.ATIVO,
                        perfil.getId()
                );

        assertThat(existeEmOutroRegistro)
                .isTrue();

        assertThat(naoExisteNoMesmoRegistro)
                .isFalse();
    }

    @Test
    @DisplayName("Deve buscar perfil por id organizacao e status")
    void deveBuscarPerfilPorIdOrganizacaoEStatus() {
        var perfil = criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        var resultadoCorreto = repository
                .findByIdAndOrganizacaoIdAndStatus(
                        perfil.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndOrganizacaoIdAndStatus(
                        perfil.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto)
                .isPresent();

        assertThat(resultadoCorreto.get().getNome())
                .isEqualTo("Financeiro");

        assertThat(resultadoOutraOrganizacao)
                .isEmpty();
    }

    @Test
    @DisplayName("Nao deve buscar perfil inativo como ativo")
    void naoDeveBuscarPerfilInativoComoAtivo() {
        var perfil = criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        perfil.inativar();
        repository.save(perfil);

        var resultado = repository
                .findByIdAndOrganizacaoIdAndStatus(
                        perfil.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado)
                .isEmpty();
    }

    @Test
    @DisplayName("Deve buscar perfil para atualizacao com escopo da organizacao")
    void deveBuscarPerfilParaAtualizacaoComEscopoDaOrganizacao() {
        var perfil = criarPerfil(
                organizacao,
                "Financeiro",
                "Perfil financeiro"
        );

        var resultadoCorreto = repository
                .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                        perfil.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                        perfil.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto)
                .isPresent();

        assertThat(resultadoOutraOrganizacao)
                .isEmpty();
    }

    private PerfilModel criarPerfil(
            OrganizacaoModel organizacao,
            String nome,
            String descricao
    ) {
        return repository.save(
                new PerfilModel(
                        organizacao,
                        new PerfilRecord(
                                nome,
                                descricao
                        )
                )
        );
    }
}