package com.empresa.erp.domain.configuracao.empresa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EmpresaRepositoryTest {

    @Autowired
    private EmpresaRepository repository;

    @Autowired
    private OrganizacaoRepository
            organizacaoRepository;

    @Test
    @DisplayName(
            "Deve listar somente empresas ativas da organizacao informada"
    )
    void deveListarSomenteEmpresasAtivasDaOrganizacaoInformada() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var outraOrganizacao =
                criarOrganizacao("Outra Organizacao");

        var empresaAtiva = criarEmpresa(
                organizacao,
                "Empresa Ativa"
        );

        var empresaInativa = criarEmpresa(
                organizacao,
                "Empresa Inativa"
        );

        empresaInativa.inativar();
        repository.save(empresaInativa);

        var empresaDeOutraOrganizacao = criarEmpresa(
                outraOrganizacao,
                "Empresa de Outro Cliente"
        );

        var resultado =
                repository.findAllByOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(EmpresaModel::getId)
                .containsExactly(empresaAtiva.getId())
                .doesNotContain(
                        empresaInativa.getId(),
                        empresaDeOutraOrganizacao.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar empresas pelo nome somente na organizacao informada"
    )
    void deveFiltrarEmpresasPeloNomeSomenteNaOrganizacaoInformada() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var outraOrganizacao =
                criarOrganizacao("Outra Organizacao");

        criarEmpresa(
                organizacao,
                "Empresa Agrícola"
        );

        criarEmpresa(
                organizacao,
                "Indústria Exemplo"
        );

        criarEmpresa(
                outraOrganizacao,
                "Empresa Agrícola Externa"
        );

        var resultado = repository
                .findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        "AGRÍCOLA",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(EmpresaModel::getNome)
                .containsExactly("Empresa Agrícola");
    }

    @Test
    @DisplayName(
            "Deve verificar nome existente somente na organizacao informada"
    )
    void deveVerificarNomeExistenteSomenteNaOrganizacaoInformada() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var outraOrganizacao =
                criarOrganizacao("Outra Organizacao");

        criarEmpresa(
                organizacao,
                "Empresa Exemplo"
        );

        boolean existeNaOrganizacao = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        organizacao.getId(),
                        "empresa exemplo",
                        StatusEnum.ATIVO
                );

        boolean existeNaOutraOrganizacao = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        outraOrganizacao.getId(),
                        "empresa exemplo",
                        StatusEnum.ATIVO
                );

        assertThat(existeNaOrganizacao).isTrue();
        assertThat(existeNaOutraOrganizacao).isFalse();
    }

    @Test
    @DisplayName(
            "Deve permitir mesmo nome em organizacoes diferentes"
    )
    void devePermitirMesmoNomeEmOrganizacoesDiferentes() {
        var primeiraOrganizacao =
                criarOrganizacao("Primeira Organizacao");

        var segundaOrganizacao =
                criarOrganizacao("Segunda Organizacao");

        criarEmpresa(
                primeiraOrganizacao,
                "Empresa Exemplo"
        );

        boolean existeAntesDoCadastro = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        segundaOrganizacao.getId(),
                        "Empresa Exemplo",
                        StatusEnum.ATIVO
                );

        var empresaDaSegundaOrganizacao = criarEmpresa(
                segundaOrganizacao,
                "Empresa Exemplo"
        );

        assertThat(existeAntesDoCadastro).isFalse();

        assertThat(empresaDaSegundaOrganizacao.getId())
                .isNotNull();
    }

    @Test
    @DisplayName(
            "Deve ignorar empresa removida na verificacao de nome"
    )
    void deveIgnorarEmpresaRemovidaNaVerificacaoDeNome() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var empresa = criarEmpresa(
                organizacao,
                "Empresa Exemplo"
        );

        empresa.remover(10L);
        repository.save(empresa);

        boolean existe = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
                        organizacao.getId(),
                        "Empresa Exemplo",
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve verificar nome duplicado desconsiderando o proprio id"
    )
    void deveVerificarNomeDuplicadoDesconsiderandoProprioId() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var primeira = criarEmpresa(
                organizacao,
                "Primeira Empresa"
        );

        criarEmpresa(
                organizacao,
                "Segunda Empresa"
        );

        boolean nomeDeOutraEmpresa = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        organizacao.getId(),
                        "segunda empresa",
                        StatusEnum.ATIVO,
                        primeira.getId()
                );

        boolean proprioNome = repository
                .existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                        organizacao.getId(),
                        "primeira empresa",
                        StatusEnum.ATIVO,
                        primeira.getId()
                );

        assertThat(nomeDeOutraEmpresa).isTrue();
        assertThat(proprioNome).isFalse();
    }

    @Test
    @DisplayName(
            "Deve buscar empresa ativa somente na organizacao informada"
    )
    void deveBuscarEmpresaAtivaSomenteNaOrganizacaoInformada() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var outraOrganizacao =
                criarOrganizacao("Outra Organizacao");

        var empresa = criarEmpresa(
                organizacao,
                "Empresa Exemplo"
        );

        var resultadoCorreto = repository
                .findByIdAndOrganizacaoIdAndStatus(
                        empresa.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndOrganizacaoIdAndStatus(
                        empresa.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto).isPresent();

        assertThat(resultadoCorreto.get().getNome())
                .isEqualTo("Empresa Exemplo");

        assertThat(resultadoOutraOrganizacao).isEmpty();
    }

    @Test
    @DisplayName(
            "Nao deve buscar empresa inativa como ativa"
    )
    void naoDeveBuscarEmpresaInativaComoAtiva() {
        var organizacao =
                criarOrganizacao("Organizacao Principal");

        var empresa = criarEmpresa(
                organizacao,
                "Empresa Exemplo"
        );

        empresa.inativar();
        repository.save(empresa);

        var resultado = repository
                .findByIdAndOrganizacaoIdAndStatus(
                        empresa.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado).isEmpty();
    }

    private OrganizacaoModel criarOrganizacao(
            String nome
    ) {
        return organizacaoRepository.save(
                new OrganizacaoModel(nome)
        );
    }

    private EmpresaModel criarEmpresa(
            OrganizacaoModel organizacao,
            String nome
    ) {
        return repository.save(
                new EmpresaModel(
                        organizacao,
                        new EmpresaRecord(nome)
                )
        );
    }
}