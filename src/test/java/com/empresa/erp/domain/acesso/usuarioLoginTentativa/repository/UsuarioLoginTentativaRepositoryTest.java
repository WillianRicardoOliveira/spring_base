package com.empresa.erp.domain.acesso.usuarioLoginTentativa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.empresa.erp.domain.acesso.usuarioLoginTentativa.model.UsuarioLoginTentativaModel;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioLoginTentativaRepositoryTest {

    @Autowired
    private UsuarioLoginTentativaRepository repository;

    @Test
    @DisplayName("Deve buscar tentativa ativa por email ignorando maiusculas e minusculas")
    void deveBuscarTentativaAtivaPorEmailIgnorandoMaiusculasEMinusculas() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        repository.save(tentativa);

        var resultado = repository.findByEmailIgnoreCaseAndStatus("USUARIO@TESTE.COM", StatusEnum.ATIVO);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("usuario@teste.com");
        assertThat(resultado.get().getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar vazio quando tentativa nao existir")
    void deveRetornarVazioQuandoTentativaNaoExistir() {
        var resultado = repository.findByEmailIgnoreCaseAndStatus("naoexiste@teste.com", StatusEnum.ATIVO);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar vazio quando tentativa estiver removida")
    void deveRetornarVazioQuandoTentativaEstiverRemovida() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");
        tentativa.remover(1L);

        repository.save(tentativa);

        var resultado = repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO);

        assertThat(resultado).isEmpty();
    }
}