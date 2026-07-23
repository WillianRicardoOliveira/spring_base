package com.empresa.erp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @Test
    @DisplayName("Deve carregar contexto da aplicacao com profile de teste")
    void deveCarregarContextoDaAplicacaoComProfileDeTeste() {
    }
}