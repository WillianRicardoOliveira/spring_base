package com.empresa.erp.domain.organizacao.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrganizacaoRepositoryTest {

    @Autowired
    private OrganizacaoRepository repository;

    @Test
    @DisplayName("Deve salvar e buscar organização")
    void deveSalvarEBuscarOrganizacao() {
        var organizacao = repository.save(
                new OrganizacaoModel("Organização Exemplo")
        );

        var resultado = repository.findById(
                organizacao.getId()
        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getNome())
                .isEqualTo("Organização Exemplo");

        assertThat(resultado.get().getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }
    
    @Test
    @DisplayName("Deve verificar organização ativa por id e status")
    void deveVerificarOrganizacaoAtivaPorIdEStatus() {
        var organizacao = repository.save(
                new OrganizacaoModel(
                        "Organização Exemplo"
                )
        );

        boolean ativa = repository.existsByIdAndStatus(
                organizacao.getId(),
                StatusEnum.ATIVO
        );

        boolean inativa = repository.existsByIdAndStatus(
                organizacao.getId(),
                StatusEnum.INATIVO
        );

        assertThat(ativa).isTrue();
        assertThat(inativa).isFalse();
    }
}