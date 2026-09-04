package com.empresa.erp.domain.configuracao.estabelecimento.repository;

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
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EstabelecimentoRepositoryTest {

    @Autowired
    private EstabelecimentoRepository repository;

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
            "Deve listar somente estabelecimentos ativos da organizacao"
    )
    void deveListarSomenteEstabelecimentosAtivosDaOrganizacao() {
        var ativo = criarEstabelecimento(
                empresa,
                "Filial Ativa"
        );

        var inativo = criarEstabelecimento(
                empresa,
                "Filial Inativa"
        );

        inativo.inativar();
        repository.save(inativo);

        var externo = criarEstabelecimento(
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
                .extracting(EstabelecimentoModel::getId)
                .containsExactly(ativo.getId())
                .doesNotContain(
                        inativo.getId(),
                        externo.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve filtrar nome somente dentro da organizacao"
    )
    void deveFiltrarNomeSomenteDentroDaOrganizacao() {
        criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        criarEstabelecimento(
                empresa,
                "Filial Londrina"
        );

        criarEstabelecimento(
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
                .extracting(EstabelecimentoModel::getNome)
                .containsExactly("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Deve listar por empresa somente na organizacao informada"
    )
    void deveListarPorEmpresaSomenteNaOrganizacaoInformada() {
        criarEstabelecimento(
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
        criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        criarEstabelecimento(
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
                .extracting(EstabelecimentoModel::getNome)
                .containsExactly("Filial Curitiba");
    }

    @Test
    @DisplayName(
            "Nao deve filtrar empresa usando outra organizacao"
    )
    void naoDeveFiltrarEmpresaUsandoOutraOrganizacao() {
        criarEstabelecimento(
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
        criarEstabelecimento(
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
        criarEstabelecimento(
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
        var estabelecimento = criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        boolean existe = repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        empresa,
                        "Filial Curitiba",
                        StatusEnum.ATIVO,
                        estabelecimento.getId()
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve identificar nome duplicado em outro estabelecimento"
    )
    void deveIdentificarNomeDuplicadoEmOutroEstabelecimento() {
        var primeiro = criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        criarEstabelecimento(
                empresa,
                "Filial Londrina"
        );

        boolean existe = repository
                .existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                        empresa,
                        "filial londrina",
                        StatusEnum.ATIVO,
                        primeiro.getId()
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Deve verificar estabelecimento ativo da empresa"
    )
    void deveVerificarEstabelecimentoAtivoDaEmpresa() {
        criarEstabelecimento(
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
            "Nao deve considerar estabelecimento inativo como ativo"
    )
    void naoDeveConsiderarEstabelecimentoInativoComoAtivo() {
        var estabelecimento = criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        estabelecimento.inativar();
        repository.save(estabelecimento);

        boolean existe =
                repository.existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve buscar estabelecimento somente na organizacao"
    )
    void deveBuscarEstabelecimentoSomenteNaOrganizacao() {
        var estabelecimento = criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        var resultadoCorreto = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        estabelecimento.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        estabelecimento.getId(),
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
            "Nao deve buscar estabelecimento inativo como ativo"
    )
    void naoDeveBuscarEstabelecimentoInativoComoAtivo() {
        var estabelecimento = criarEstabelecimento(
                empresa,
                "Filial Curitiba"
        );

        estabelecimento.inativar();
        repository.save(estabelecimento);

        var resultado = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        estabelecimento.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado).isEmpty();
    }

    private EstabelecimentoModel criarEstabelecimento(
            EmpresaModel empresa,
            String nome
    ) {
        return repository.save(
                new EstabelecimentoModel(
                        empresa,
                        nome
                )
        );
    }
}