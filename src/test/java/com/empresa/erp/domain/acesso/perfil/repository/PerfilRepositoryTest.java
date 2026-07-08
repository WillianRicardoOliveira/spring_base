package com.empresa.erp.domain.acesso.perfil.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PerfilRepositoryTest {

    @Autowired
    private PerfilRepository repository;

    @Test
    @DisplayName("Deve listar perfis ativos")
    void deveListarPerfisAtivos() {
        var perfilAtivo = repository.save(new PerfilModel(
                new PerfilRecord("Financeiro", "Perfil financeiro")
        ));

        var perfilInativo = repository.save(new PerfilModel(
                new PerfilRecord("Compras", "Perfil compras")
        ));
        perfilInativo.inativar();
        repository.save(perfilInativo);

        var resultado = repository.findAllByStatus(PageRequest.of(0, 10), StatusEnum.ATIVO);

        assertThat(resultado.getContent())
                .extracting(PerfilModel::getId)
                .contains(perfilAtivo.getId())
                .doesNotContain(perfilInativo.getId());
    }

    @Test
    @DisplayName("Deve filtrar perfis ativos por nome ignorando caixa")
    void deveFiltrarPerfisAtivosPorNomeIgnorandoCaixa() {
        repository.save(new PerfilModel(
                new PerfilRecord("Financeiro", "Perfil financeiro")
        ));

        repository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var resultado = repository.findByNomeContainingIgnoreCaseAndStatus(
                PageRequest.of(0, 10),
                "FIN",
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Financeiro");
    }

    @Test
    @DisplayName("Deve verificar existencia por nome ignorando caixa e status")
    void deveVerificarExistenciaPorNomeIgnorandoCaixaEStatus() {
        repository.save(new PerfilModel(
                new PerfilRecord("Financeiro", "Perfil financeiro")
        ));

        var existe = repository.existsByNomeIgnoreCaseAndStatus("financeiro", StatusEnum.ATIVO);
        var naoExiste = repository.existsByNomeIgnoreCaseAndStatus("financeiro", StatusEnum.REMOVIDO);

        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    @DisplayName("Deve verificar existencia por nome excluindo id")
    void deveVerificarExistenciaPorNomeExcluindoId() {
        var perfil = repository.save(new PerfilModel(
                new PerfilRecord("Financeiro", "Perfil financeiro")
        ));

        repository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var existeEmOutroRegistro = repository.existsByNomeIgnoreCaseAndStatusAndIdNot(
                "administrador",
                StatusEnum.ATIVO,
                perfil.getId()
        );

        var naoExisteNoMesmoRegistro = repository.existsByNomeIgnoreCaseAndStatusAndIdNot(
                "financeiro",
                StatusEnum.ATIVO,
                perfil.getId()
        );

        assertThat(existeEmOutroRegistro).isTrue();
        assertThat(naoExisteNoMesmoRegistro).isFalse();
    }

    @Test
    @DisplayName("Deve buscar perfil por id e status")
    void deveBuscarPerfilPorIdEStatus() {
        var perfil = repository.save(new PerfilModel(
                new PerfilRecord("Financeiro", "Perfil financeiro")
        ));

        var resultado = repository.findByIdAndStatus(perfil.getId(), StatusEnum.ATIVO);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Financeiro");
    }
}