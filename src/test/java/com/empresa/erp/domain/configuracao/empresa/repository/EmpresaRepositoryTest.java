package com.empresa.erp.domain.configuracao.empresa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EmpresaRepositoryTest {

    @Autowired
    private EmpresaRepository repository;

    @Test
    @DisplayName("Deve listar somente empresas ativas")
    void deveListarSomenteEmpresasAtivas() {
        var empresaAtiva = repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Ativa")
                )
        );

        var empresaInativa = repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Inativa")
                )
        );

        empresaInativa.inativar();
        repository.save(empresaInativa);

        var resultado = repository.findAllByStatus(
                PageRequest.of(0, 10),
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent())
                .extracting(EmpresaModel::getId)
                .contains(empresaAtiva.getId())
                .doesNotContain(empresaInativa.getId());
    }

    @Test
    @DisplayName("Deve filtrar empresas ativas pelo nome ignorando caixa")
    void deveFiltrarEmpresasAtivasPeloNomeIgnorandoCaixa() {
        repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Agrícola")
                )
        );

        repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Indústria Exemplo")
                )
        );

        var resultado =
                repository.findByNomeContainingIgnoreCaseAndStatus(
                        PageRequest.of(0, 10),
                        "AGRÍCOLA",
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent()).hasSize(1);

        assertThat(resultado.getContent().get(0).getNome())
                .isEqualTo("Empresa Agrícola");
    }

    @Test
    @DisplayName("Deve verificar nome existente ignorando caixa")
    void deveVerificarNomeExistenteIgnorandoCaixa() {
        repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Exemplo")
                )
        );

        boolean existe =
                repository.existsByNomeIgnoreCaseAndStatus(
                        "empresa exemplo",
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve ignorar empresa removida na verificação de nome")
    void deveIgnorarEmpresaRemovidaNaVerificacaoDeNome() {
        var empresa = repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Exemplo")
                )
        );

        empresa.remover(10L);
        repository.save(empresa);

        boolean existe =
                repository.existsByNomeIgnoreCaseAndStatus(
                        "Empresa Exemplo",
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve verificar nome duplicado desconsiderando o próprio id")
    void deveVerificarNomeDuplicadoDesconsiderandoProprioId() {
        var primeira = repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Primeira Empresa")
                )
        );

        repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Segunda Empresa")
                )
        );

        boolean nomeDeOutraEmpresa =
                repository.existsByNomeIgnoreCaseAndStatusAndIdNot(
                        "segunda empresa",
                        StatusEnum.ATIVO,
                        primeira.getId()
                );

        boolean proprioNome =
                repository.existsByNomeIgnoreCaseAndStatusAndIdNot(
                        "primeira empresa",
                        StatusEnum.ATIVO,
                        primeira.getId()
                );

        assertThat(nomeDeOutraEmpresa).isTrue();
        assertThat(proprioNome).isFalse();
    }

    @Test
    @DisplayName("Deve buscar empresa ativa por id e status")
    void deveBuscarEmpresaAtivaPorIdEStatus() {
        var empresa = repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Exemplo")
                )
        );

        var resultado = repository.findByIdAndStatus(
                empresa.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getNome())
                .isEqualTo("Empresa Exemplo");
    }

    @Test
    @DisplayName("Não deve buscar empresa inativa como ativa")
    void naoDeveBuscarEmpresaInativaComoAtiva() {
        var empresa = repository.save(
                new EmpresaModel(
                        new EmpresaRecord("Empresa Exemplo")
                )
        );

        empresa.inativar();
        repository.save(empresa);

        var resultado = repository.findByIdAndStatus(
                empresa.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isEmpty();
    }
}