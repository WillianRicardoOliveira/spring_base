package com.empresa.erp.core.bootstrap.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.core.bootstrap.config.BootstrapProperties;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BootstrapPropertiesValidatorTest {

    private static ValidatorFactory validatorFactory;

    private static Validator validator;

    private BootstrapPropertiesValidator propertiesValidator;

    @BeforeAll
    static void beforeAll() {
        validatorFactory =
                Validation
                        .buildDefaultValidatorFactory();

        validator =
                validatorFactory.getValidator();
    }

    @AfterAll
    static void afterAll() {
        validatorFactory.close();
    }

    @BeforeEach
    void setUp() {
        propertiesValidator =
                new BootstrapPropertiesValidator(
                        validator
                );
    }

    @Test
    @DisplayName(
            "Deve ignorar validação quando bootstrap está desabilitado"
    )
    void deveIgnorarValidacaoQuandoBootstrapEstaDesabilitado() {
        var properties =
                new BootstrapProperties(
                        false,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertThatCode(
                () -> propertiesValidator.validar(
                        properties
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
            "Deve aceitar configurações válidas"
    )
    void deveAceitarConfiguracoesValidas() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatCode(
                () -> propertiesValidator.validar(
                        properties
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
            "Não deve aceitar nome da organização em branco"
    )
    void naoDeveAceitarNomeDaOrganizacaoEmBranco() {
        var properties =
                criarProperties(
                        "   ",
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "nome da organizacao obrigatorio"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar espaços externos no nome da organização"
    )
    void naoDeveAceitarEspacosExternosNoNomeDaOrganizacao() {
        var properties =
                criarProperties(
                        " Organização Principal ",
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "nome da organizacao nao deve iniciar "
                                + "ou terminar com espacos"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar nome da organização acima de 100 caracteres"
    )
    void naoDeveAceitarNomeDaOrganizacaoAcimaDeCemCaracteres() {
        var properties =
                criarProperties(
                        "A".repeat(101),
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "nome da organizacao deve possuir "
                                + "no maximo 100 caracteres"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar e-mail inválido do administrador da organização"
    )
    void naoDeveAceitarEmailInvalidoDoAdministradorDaOrganizacao() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "email-invalido",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "e-mail ou senha do "
                                + "administrador da organizacao "
                                + "invalidos"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar e-mail inválido do administrador da plataforma"
    )
    void naoDeveAceitarEmailInvalidoDoAdministradorDaPlataforma() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "admin.organizacao@teste.com",
                        "Organizacao@2026",
                        "email-invalido",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "e-mail ou senha do "
                                + "administrador da plataforma "
                                + "invalidos"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar espaços externos no e-mail"
    )
    void naoDeveAceitarEspacosExternosNoEmail() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        " admin.organizacao@teste.com ",
                        "Organizacao@2026",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "e-mail do administrador da organizacao "
                                + "nao deve iniciar ou terminar "
                                + "com espacos"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar senha fraca"
    )
    void naoDeveAceitarSenhaFraca() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "admin.organizacao@teste.com",
                        "senha-fraca",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "e-mail ou senha do "
                                + "administrador da organizacao "
                                + "invalidos"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar senha padrão"
    )
    void naoDeveAceitarSenhaPadrao() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "admin.organizacao@teste.com",
                        "Senha@123",
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "senha padrao nao permitida para o "
                                + "administrador da organizacao"
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar senha acima do limite seguro do BCrypt"
    )
    void naoDeveAceitarSenhaAcimaDoLimiteSeguroDoBcrypt() {
        String senhaAcimaDoLimite =
                "Aa1@" + "ç".repeat(35);

        var properties =
                criarProperties(
                        "Organização Principal",
                        "admin.organizacao@teste.com",
                        senhaAcimaDoLimite,
                        "admin.plataforma@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "senha do administrador da organizacao "
                                + "excede o limite seguro do BCrypt"
                );
    }

    @Test
    @DisplayName(
            "Deve permitir a mesma identidade com a mesma senha"
    )
    void devePermitirAMesmaIdentidadeComAMesmaSenha() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "ADMIN@TESTE.COM",
                        "Identidade@2026",
                        "admin@teste.com",
                        "Identidade@2026"
                );

        assertThatCode(
                () -> propertiesValidator.validar(
                        properties
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
            "Não deve permitir a mesma identidade com senhas diferentes"
    )
    void naoDevePermitirAMesmaIdentidadeComSenhasDiferentes() {
        var properties =
                criarProperties(
                        "Organização Principal",
                        "ADMIN@TESTE.COM",
                        "Organizacao@2026",
                        "admin@teste.com",
                        "Plataforma@2026"
                );

        assertThatThrownBy(
                () -> propertiesValidator.validar(
                        properties
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Configuracao de bootstrap invalida: "
                                + "as senhas devem ser iguais quando "
                                + "o administrador da organizacao "
                                + "e o administrador da plataforma "
                                + "utilizam o mesmo e-mail"
                );
    }

    private BootstrapProperties criarProperties(
            String nomeOrganizacao,
            String emailAdministradorOrganizacao,
            String senhaAdministradorOrganizacao,
            String emailAdministradorPlataforma,
            String senhaAdministradorPlataforma
    ) {
        return new BootstrapProperties(
                true,
                nomeOrganizacao,
                emailAdministradorOrganizacao,
                senhaAdministradorOrganizacao,
                emailAdministradorPlataforma,
                senhaAdministradorPlataforma
        );
    }
}