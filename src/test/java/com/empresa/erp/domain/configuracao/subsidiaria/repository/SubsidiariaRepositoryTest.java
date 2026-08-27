package com.empresa.erp.domain.configuracao.subsidiaria.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SubsidiariaRepositoryTest {

    @Autowired
    private SubsidiariaRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private OrganizacaoRepository
            organizacaoRepository;

    private OrganizacaoModel organizacao;
    private OrganizacaoModel outraOrganizacao;
    private EmpresaModel empresa;
    private EmpresaModel empresaDeOutraOrganizacao;

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

        empresa = empresaRepository.save(
                new EmpresaModel(
                        organizacao,
                        new EmpresaRecord(
                                "Empresa Exemplo"
                        )
                )
        );

        empresaDeOutraOrganizacao =
                empresaRepository.save(
                        new EmpresaModel(
                                outraOrganizacao,
                                new EmpresaRecord(
                                        "Empresa Externa"
                                )
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve listar somente subsidiarias ativas da organizacao"
    )
    void deveListarSomenteSubsidiariasAtivasDaOrganizacao() {
        var ativa = criarSubsidiaria(
                empresa,
                "Filial Ativa"
        );

        var inativa = criarSubsidiaria(
                empresa,
                "Filial Inativa"
        );

        inativa.inativar();
        repository.save(inativa);

        var externa = criarSubsidiaria(
                empresaDeOutraOrganizacao,
                "Filial Externa"
        );

        var resultado = repository
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(SubsidiariaModel::getId)
                .containsExactly(ativa.getId())
                .doesNotContain(
                        inativa.getId(),
                        externa.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar nome somente dentro da organizacao"
    )
    void deveFiltrarNomeSomenteDentroDaOrganizacao() {
        criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        criarSubsidiaria(
                empresa,
                "Filial Londrina"
        );

        criarSubsidiaria(
                empresaDeOutraOrganizacao,
                "Filial Curitiba Externa"
        );

        var resultado = repository
                .findByEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        "CURITIBA",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(SubsidiariaModel::getNome)
                .containsExactly("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Deve listar por empresa somente na organizacao informada"
    )
    void deveListarPorEmpresaSomenteNaOrganizacaoInformada() {
        criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        var resultadoCorreto = repository
                .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto.getContent())
                .hasSize(1);

        assertThat(resultadoOutraOrganizacao.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve filtrar nome por empresa e organizacao"
    )
    void deveFiltrarNomePorEmpresaEOrganizacao() {
        criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        criarSubsidiaria(
                empresa,
                "Filial Londrina"
        );

        var resultado = repository
                .findByEmpresaIdAndEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        organizacao.getId(),
                        "curitiba",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(SubsidiariaModel::getNome)
                .containsExactly("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Nao deve filtrar empresa usando outra organizacao"
    )
    void naoDeveFiltrarEmpresaUsandoOutraOrganizacao() {
        criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        var resultado = repository
                .findByEmpresaIdAndEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        outraOrganizacao.getId(),
                        "Curitiba",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve verificar nome duplicado na mesma empresa"
    )
    void deveVerificarNomeDuplicadoNaMesmaEmpresa() {
        criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        boolean existe = repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                        empresa,
                        "filial curitiba",
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Deve permitir mesmo nome em empresas diferentes"
    )
    void devePermitirMesmoNomeEmEmpresasDiferentes() {
        criarSubsidiaria(
                empresa,
                "Matriz"
        );

        var outraEmpresaDaMesmaOrganizacao =
                empresaRepository.save(
                        new EmpresaModel(
                                organizacao,
                                new EmpresaRecord(
                                        "Outra Empresa"
                                )
                        )
                );

        boolean existeNaOutraEmpresa = repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                        outraEmpresaDaMesmaOrganizacao,
                        "Matriz",
                        StatusEnum.ATIVO
                );

        assertThat(existeNaOutraEmpresa).isFalse();
    }

    @Test
    @DisplayName(
            "Deve desconsiderar o proprio id na duplicidade"
    )
    void deveDesconsiderarProprioIdNaDuplicidade() {
        var subsidiaria = criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        boolean existe = repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        empresa,
                        "Filial Curitiba",
                        StatusEnum.ATIVO,
                        subsidiaria.getId()
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve identificar nome duplicado em outra subsidiaria"
    )
    void deveIdentificarNomeDuplicadoEmOutraSubsidiaria() {
        var primeira = criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        criarSubsidiaria(
                empresa,
                "Filial Londrina"
        );

        boolean existe = repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        empresa,
                        "filial londrina",
                        StatusEnum.ATIVO,
                        primeira.getId()
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Deve verificar subsidiaria ativa da empresa"
    )
    void deveVerificarSubsidiariaAtivaDaEmpresa() {
        criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        boolean existe =
                repository.existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Nao deve considerar subsidiaria inativa como ativa"
    )
    void naoDeveConsiderarSubsidiariaInativaComoAtiva() {
        var subsidiaria = criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        subsidiaria.inativar();
        repository.save(subsidiaria);

        boolean existe =
                repository.existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve buscar subsidiaria somente na organizacao"
    )
    void deveBuscarSubsidiariaSomenteNaOrganizacao() {
        var subsidiaria = criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        var resultadoCorreto = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        subsidiaria.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        subsidiaria.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto).isPresent();

        assertThat(resultadoCorreto.get().getNome())
                .isEqualTo("Filial Curitiba");

        assertThat(resultadoOutraOrganizacao)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Nao deve buscar subsidiaria inativa como ativa"
    )
    void naoDeveBuscarSubsidiariaInativaComoAtiva() {
        var subsidiaria = criarSubsidiaria(
                empresa,
                "Filial Curitiba"
        );

        subsidiaria.inativar();
        repository.save(subsidiaria);

        var resultado = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        subsidiaria.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado).isEmpty();
    }

    private SubsidiariaModel criarSubsidiaria(
            EmpresaModel empresa,
            String nome
    ) {
        return repository.save(
                new SubsidiariaModel(
                        empresa,
                        nome
                )
        );
    }
}