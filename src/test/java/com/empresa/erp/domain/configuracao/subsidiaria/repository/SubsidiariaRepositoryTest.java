package com.empresa.erp.domain.configuracao.subsidiaria.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SubsidiariaRepositoryTest {

    @Autowired
    private SubsidiariaRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private EmpresaModel empresa;

    @BeforeEach
    void setUp() {
        empresa = empresaRepository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Exemplo")
                )
        );
    }

    @Test
    @DisplayName("Deve listar somente subsidiarias ativas")
    void deveListarSomenteSubsidiariasAtivas() {
        var ativa = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Ativa"
                )
        );

        var inativa = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Inativa"
                )
        );

        inativa.inativar();
        repository.save(inativa);

        var resultado = repository.findAllByStatus(
                PageRequest.of(0, 10),
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent())
                .extracting(SubsidiariaModel::getId)
                .contains(ativa.getId())
                .doesNotContain(inativa.getId());
    }

    @Test
    @DisplayName("Deve filtrar subsidiaria pelo nome")
    void deveFiltrarSubsidiariaPeloNome() {
        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Londrina"
                )
        );

        var resultado =
                repository.findByNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        "CURITIBA",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent()).hasSize(1);

        assertThat(resultado.getContent().get(0).getNome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName("Deve listar subsidiarias por empresa")
    void deveListarSubsidiariasPorEmpresa() {
        var outraEmpresa = empresaRepository.save(
                new EmpresaModel(
                        new EmpresaRecord("Outra Empresa")
                )
        );

        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        repository.save(
                new SubsidiariaModel(
                        outraEmpresa,
                        "Filial Sao Paulo"
                )
        );

        var resultado =
                repository.findAllByEmpresaIdAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent()).hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getEmpresa()
                        .getId()
        ).isEqualTo(empresa.getId());
    }

    @Test
    @DisplayName("Deve filtrar nome dentro da empresa")
    void deveFiltrarNomeDentroDaEmpresa() {
        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Londrina"
                )
        );

        var resultado =
                repository
                        .findByEmpresaIdAndNomeContainingIgnoreCaseAndStatus(
                                PageRequest.of(0, 10),
                                empresa.getId(),
                                "curitiba",
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent()).hasSize(1);

        assertThat(resultado.getContent().get(0).getNome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName("Deve verificar nome duplicado na mesma empresa")
    void deveVerificarNomeDuplicadoNaMesmaEmpresa() {
        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        boolean existe =
                repository
                        .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                                empresa,
                                "filial curitiba",
                                StatusEnum.ATIVO
                        );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve permitir mesmo nome em empresas diferentes")
    void devePermitirMesmoNomeEmEmpresasDiferentes() {
        var outraEmpresa = empresaRepository.save(
                new EmpresaModel(
                        new EmpresaRecord("Outra Empresa")
                )
        );

        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Matriz"
                )
        );

        boolean existeNaOutraEmpresa =
                repository
                        .existsByEmpresaAndNomeIgnoreCaseAndStatus(
                                outraEmpresa,
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
        var subsidiaria = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        boolean existe =
                repository
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
        var primeira = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Londrina"
                )
        );

        boolean existeEmOutraSubsidiaria =
                repository
                        .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                                empresa,
                                "filial londrina",
                                StatusEnum.ATIVO,
                                primeira.getId()
                        );

        assertThat(existeEmOutraSubsidiaria).isTrue();
    }

    @Test
    @DisplayName("Deve verificar subsidiaria ativa da empresa")
    void deveVerificarSubsidiariaAtivaDaEmpresa() {
        repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
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
        var subsidiaria = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
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
    @DisplayName("Deve buscar subsidiaria ativa por id")
    void deveBuscarSubsidiariaAtivaPorId() {
        var subsidiaria = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        var resultado = repository.findByIdAndStatus(
                subsidiaria.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getNome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName("Nao deve buscar subsidiaria inativa como ativa")
    void naoDeveBuscarSubsidiariaInativaComoAtiva() {
        var subsidiaria = repository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        subsidiaria.inativar();
        repository.save(subsidiaria);

        var resultado = repository.findByIdAndStatus(
                subsidiaria.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isEmpty();
    }
}