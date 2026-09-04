package com.empresa.erp.domain.configuracao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.configuracao.empresa.controller.EmpresaController;
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.controller.SubsidiariaController;
import com.empresa.erp.domain.configuracao.subsidiaria.record.AtualizaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.SubsidiariaRecord;

class ConfiguracaoControllersAuthorizationTest {

    @Test
    @DisplayName(
            "Deve validar permissoes do EmpresaController"
    )
    void deveValidarPermissoesDoEmpresaController()
            throws Exception {

        assertThat(
                preAuthorize(
                        EmpresaController.class,
                        "cadastrar",
                        EmpresaRecord.class,
                        UriComponentsBuilder.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_EMPRESA_CRIAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        EmpresaController.class,
                        "listar",
                        Pageable.class,
                        String.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_EMPRESA_LISTAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        EmpresaController.class,
                        "detalhar",
                        Long.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_EMPRESA_DETALHAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        EmpresaController.class,
                        "atualizar",
                        AtualizaEmpresaRecord.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_EMPRESA_EDITAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        EmpresaController.class,
                        "excluir",
                        Long.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_EMPRESA_EXCLUIR'"
                        + ")"
        );
    }

    @Test
    @DisplayName(
            "Deve validar permissoes do SubsidiariaController"
    )
    void deveValidarPermissoesDoSubsidiariaController()
            throws Exception {

        assertThat(
                preAuthorize(
                        SubsidiariaController.class,
                        "cadastrar",
                        SubsidiariaRecord.class,
                        UriComponentsBuilder.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_SUBSIDIARIA_CRIAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        SubsidiariaController.class,
                        "listar",
                        Pageable.class,
                        Long.class,
                        String.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_SUBSIDIARIA_LISTAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        SubsidiariaController.class,
                        "detalhar",
                        Long.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_SUBSIDIARIA_DETALHAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        SubsidiariaController.class,
                        "atualizar",
                        AtualizaSubsidiariaRecord.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_SUBSIDIARIA_EDITAR'"
                        + ")"
        );

        assertThat(
                preAuthorize(
                        SubsidiariaController.class,
                        "excluir",
                        Long.class
                )
        ).isEqualTo(
                "hasAuthority("
                        + "'CONFIGURACAO_SUBSIDIARIA_EXCLUIR'"
                        + ")"
        );
    }

    private String preAuthorize(
            Class<?> controller,
            String metodo,
            Class<?>... parametros
    ) throws Exception {

        return controller
                .getDeclaredMethod(
                        metodo,
                        parametros
                )
                .getAnnotation(
                        PreAuthorize.class
                )
                .value();
    }
}