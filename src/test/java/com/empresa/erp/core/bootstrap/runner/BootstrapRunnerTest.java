package com.empresa.erp.core.bootstrap.runner;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import com.empresa.erp.core.bootstrap.config.BootstrapProperties;
import com.empresa.erp.core.bootstrap.service.BootstrapService;

@ExtendWith(MockitoExtension.class)
class BootstrapRunnerTest {

    @Mock
    private BootstrapProperties properties;

    @Mock
    private BootstrapService bootstrapService;

    @Mock
    private ApplicationArguments applicationArguments;

    private BootstrapRunner runner;

    @BeforeEach
    void setUp() {
        runner =
                new BootstrapRunner(
                        properties,
                        bootstrapService
                );
    }

    @Test
    @DisplayName(
            "Não deve executar bootstrap quando estiver desabilitado"
    )
    void naoDeveExecutarBootstrapQuandoEstiverDesabilitado() {
        when(properties.enabled())
                .thenReturn(false);

        assertThatCode(
                () -> runner.run(applicationArguments)
        ).doesNotThrowAnyException();

        verify(properties)
                .enabled();

        verifyNoInteractions(
                bootstrapService,
                applicationArguments
        );
    }

    @Test
    @DisplayName(
            "Deve executar bootstrap quando estiver habilitado"
    )
    void deveExecutarBootstrapQuandoEstiverHabilitado() {
        when(properties.enabled())
                .thenReturn(true);

        when(bootstrapService.provisionar())
                .thenReturn(true);

        assertThatCode(
                () -> runner.run(applicationArguments)
        ).doesNotThrowAnyException();

        verify(properties)
                .enabled();

        verify(bootstrapService)
                .provisionar();

        verifyNoInteractions(applicationArguments);
    }

    @Test
    @DisplayName(
            "Deve concluir normalmente quando instalação já estiver provisionada"
    )
    void deveConcluirQuandoInstalacaoJaEstiverProvisionada() {
        when(properties.enabled())
                .thenReturn(true);

        when(bootstrapService.provisionar())
                .thenReturn(false);

        assertThatCode(
                () -> runner.run(applicationArguments)
        ).doesNotThrowAnyException();

        verify(properties)
                .enabled();

        verify(bootstrapService)
                .provisionar();

        verifyNoInteractions(applicationArguments);
    }

    @Test
    @DisplayName(
            "Deve propagar erro ocorrido durante o bootstrap"
    )
    void devePropagarErroOcorridoDuranteBootstrap() {
        when(properties.enabled())
                .thenReturn(true);

        when(bootstrapService.provisionar())
                .thenThrow(
                        new IllegalStateException(
                                "estado inicial inconsistente"
                        )
                );

        assertThatThrownBy(
                () -> runner.run(applicationArguments)
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "estado inicial inconsistente"
                );

        verify(properties)
                .enabled();

        verify(bootstrapService)
                .provisionar();

        verifyNoInteractions(applicationArguments);
    }

    @Test
    @DisplayName(
            "Não deve consultar o serviço quando bootstrap estiver desabilitado"
    )
    void naoDeveConsultarServicoQuandoBootstrapEstiverDesabilitado() {
        when(properties.enabled())
                .thenReturn(false);

        runner.run(applicationArguments);

        verify(bootstrapService, never())
                .provisionar();
    }
}