package com.empresa.erp.domain.plataforma;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.plataforma.organizacao.controller.OrganizacaoPlataformaController;
import com.empresa.erp.domain.plataforma.organizacao.convite.controller.ConviteOrganizacaoController;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoNovoUsuarioRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoUsuarioExistenteRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.TokenConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.OrganizacaoRecord;

class PlataformaControllersAuthorizationTest {

    @Test
    @DisplayName(
            "Deve validar permissões administrativas "
                    + "de organização"
    )
    void deveValidarPermissoesAdministrativasDeOrganizacao()
            throws Exception {
        assertThat(preAuthorize(
                OrganizacaoPlataformaController.class,
                "listar",
                Pageable.class,
                String.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_LISTAR')"
        );

        assertThat(preAuthorize(
                OrganizacaoPlataformaController.class,
                "detalhar",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_DETALHAR')"
        );

        assertThat(preAuthorize(
                OrganizacaoPlataformaController.class,
                "editar",
                Long.class,
                OrganizacaoRecord.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_EDITAR')"
        );

        assertThat(preAuthorize(
                OrganizacaoPlataformaController.class,
                "inativar",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_STATUS')"
        );

        assertThat(preAuthorize(
                OrganizacaoPlataformaController.class,
                "reativar",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_STATUS')"
        );

        assertThat(preAuthorize(
                OrganizacaoPlataformaController.class,
                "remover",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_EXCLUIR')"
        );
    }

    @Test
    @DisplayName(
            "Deve validar permissões administrativas "
                    + "dos convites"
    )
    void deveValidarPermissoesAdministrativasDosConvites()
            throws Exception {
        assertThat(preAuthorize(
                ConviteOrganizacaoController.class,
                "convidar",
                ConviteOrganizacaoRecord.class,
                UriComponentsBuilder.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_CRIAR')"
        );

        assertThat(preAuthorize(
                ConviteOrganizacaoController.class,
                "listar",
                Pageable.class,
                String.class,
                StatusConviteOrganizacaoEnum.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_LISTAR')"
        );

        assertThat(preAuthorize(
                ConviteOrganizacaoController.class,
                "detalhar",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_DETALHAR')"
        );

        assertThat(preAuthorize(
                ConviteOrganizacaoController.class,
                "revogar",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_CRIAR')"
        );

        assertThat(preAuthorize(
                ConviteOrganizacaoController.class,
                "reenviar",
                Long.class
        )).isEqualTo(
                "hasAuthority('PLATAFORMA_ORGANIZACAO_CRIAR')"
        );
    }

    @Test
    @DisplayName(
            "Não deve exigir permissão funcional "
                    + "nos fluxos de aceite"
    )
    void naoDeveExigirPermissaoFuncionalNosFluxosDeAceite()
            throws Exception {
        assertThat(preAuthorizeOpcional(
                ConviteOrganizacaoController.class,
                "consultar",
                TokenConsultaConviteOrganizacaoRecord.class
        )).isNull();

        assertThat(preAuthorizeOpcional(
                ConviteOrganizacaoController.class,
                "aceitarUsuarioExistente",
                AceiteConviteOrganizacaoUsuarioExistenteRecord.class
        )).isNull();

        assertThat(preAuthorizeOpcional(
                ConviteOrganizacaoController.class,
                "aceitarNovoUsuario",
                AceiteConviteOrganizacaoNovoUsuarioRecord.class
        )).isNull();
    }

    private String preAuthorize(
            Class<?> controller,
            String metodo,
            Class<?>... parametros
    ) throws Exception {
        PreAuthorize preAuthorize =
                controller
                        .getDeclaredMethod(
                                metodo,
                                parametros
                        )
                        .getAnnotation(
                                PreAuthorize.class
                        );

        assertThat(preAuthorize)
                .as(
                        "O método %s deve possuir @PreAuthorize",
                        metodo
                )
                .isNotNull();

        return preAuthorize.value();
    }

    private PreAuthorize preAuthorizeOpcional(
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
                );
    }
}