package com.empresa.erp.domain.acesso.usuarioLoginTentativa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.old.StatusEnum;

class UsuarioLoginTentativaModelTest {

    @Test
    @DisplayName("Deve criar tentativa de login ativa com email normalizado")
    void deveCriarTentativaDeLoginAtivaComEmailNormalizado() {
        var tentativa = new UsuarioLoginTentativaModel(" Usuario@Teste.com ");

        assertThat(tentativa.getEmail()).isEqualTo("usuario@teste.com");
        assertThat(tentativa.getQuantidadeFalhas()).isZero();
        assertThat(tentativa.getUltimaFalhaEm()).isNull();
        assertThat(tentativa.getBloqueadoAte()).isNull();
        assertThat(tentativa.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve registrar falha sem bloquear antes do limite")
    void deveRegistrarFalhaSemBloquearAntesDoLimite() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        tentativa.registrarFalha(5, 15);

        assertThat(tentativa.getQuantidadeFalhas()).isEqualTo(1);
        assertThat(tentativa.getUltimaFalhaEm()).isNotNull();
        assertThat(tentativa.getBloqueadoAte()).isNull();
        assertThat(tentativa.estaBloqueado()).isFalse();
    }

    @Test
    @DisplayName("Deve bloquear ao atingir limite de falhas")
    void deveBloquearAoAtingirLimiteDeFalhas() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        tentativa.registrarFalha(3, 15);
        tentativa.registrarFalha(3, 15);
        tentativa.registrarFalha(3, 15);

        assertThat(tentativa.getQuantidadeFalhas()).isEqualTo(3);
        assertThat(tentativa.getUltimaFalhaEm()).isNotNull();
        assertThat(tentativa.getBloqueadoAte()).isNotNull();
        assertThat(tentativa.estaBloqueado()).isTrue();
    }

    @Test
    @DisplayName("Deve limpar falhas")
    void deveLimparFalhas() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        tentativa.registrarFalha(3, 15);
        tentativa.registrarFalha(3, 15);

        tentativa.limparFalhas();

        assertThat(tentativa.getQuantidadeFalhas()).isZero();
        assertThat(tentativa.getUltimaFalhaEm()).isNull();
        assertThat(tentativa.getBloqueadoAte()).isNull();
        assertThat(tentativa.estaBloqueado()).isFalse();
    }

    @Test
    @DisplayName("Deve remover tentativa com auditoria")
    void deveRemoverTentativaComAuditoria() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        tentativa.remover(10L);

        assertThat(tentativa.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(tentativa.getRemovidoPor()).isEqualTo(10L);
        assertThat(tentativa.getRemovidoEm()).isNotNull();
    }
}