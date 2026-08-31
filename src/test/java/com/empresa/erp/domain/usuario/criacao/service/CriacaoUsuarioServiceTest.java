package com.empresa.erp.domain.usuario.criacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class CriacaoUsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Validator validator;

    @InjectMocks
    private CriacaoUsuarioService service;

    @Test
    @DisplayName(
            "Deve criar usuário global com e-mail normalizado e senha criptografada"
    )
    void deveCriarUsuarioGlobalComEmailNormalizadoESenhaCriptografada() {
        when(validator.validate(
                any(UsuarioRecord.class)
        )).thenReturn(
                Set.of()
        );

        when(repository.existsByEmailIgnoreCase(
                "usuario@teste.com"
        )).thenReturn(false);

        when(passwordEncoder.encode(
                "Senha@2026"
        )).thenReturn(
                "senha-criptografada"
        );

        when(repository.save(
                any(UsuarioModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        UsuarioModel resultado =
                service.criar(
                        "  Usuario@Teste.COM  ",
                        "Senha@2026"
                );

        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.getEmail())
                .isEqualTo(
                        "usuario@teste.com"
                );

        assertThat(resultado.getSenha())
                .isEqualTo(
                        "senha-criptografada"
                );

        assertThat(resultado.getStatus())
                .isEqualTo(
                        StatusEnum.ATIVO
                );

        var dadosCaptor =
                ArgumentCaptor.forClass(
                        UsuarioRecord.class
                );

        verify(validator)
                .validate(
                        dadosCaptor.capture()
                );

        assertThat(dadosCaptor.getValue().email())
                .isEqualTo(
                        "usuario@teste.com"
                );

        assertThat(dadosCaptor.getValue().senha())
                .isEqualTo(
                        "Senha@2026"
                );

        verify(repository)
                .existsByEmailIgnoreCase(
                        "usuario@teste.com"
                );

        verify(passwordEncoder)
                .encode(
                        "Senha@2026"
                );

        var usuarioCaptor =
                ArgumentCaptor.forClass(
                        UsuarioModel.class
                );

        verify(repository)
                .save(
                        usuarioCaptor.capture()
                );

        assertThat(usuarioCaptor.getValue())
                .isSameAs(resultado);
    }

    @Test
    @DisplayName(
            "Não deve criar usuário quando e-mail global já estiver cadastrado"
    )
    void naoDeveCriarUsuarioQuandoEmailGlobalJaEstiverCadastrado() {
        when(validator.validate(
                any(UsuarioRecord.class)
        )).thenReturn(
                Set.of()
        );

        when(repository.existsByEmailIgnoreCase(
                "usuario@teste.com"
        )).thenReturn(true);

        assertThatThrownBy(
                () -> service.criar(
                        "  Usuario@Teste.COM ",
                        "Senha@2026"
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Usuario ja cadastrado."
                );

        verify(validator)
                .validate(
                        any(UsuarioRecord.class)
                );

        verify(repository)
                .existsByEmailIgnoreCase(
                        "usuario@teste.com"
                );

        verify(repository, never())
                .save(any());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName(
            "Não deve criar usuário com formato de e-mail inválido"
    )
    void naoDeveCriarUsuarioComFormatoDeEmailInvalido() {
        ConstraintViolation<UsuarioRecord> violacao =
                criarViolacao(
                        "Formato do e-mail é inválido"
                );

        when(validator.validate(
                any(UsuarioRecord.class)
        )).thenReturn(
                Set.of(violacao)
        );

        assertThatThrownBy(
                () -> service.criar(
                        "email-invalido",
                        "Senha@2026"
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Formato do e-mail é inválido"
                );

        verify(validator)
                .validate(
                        any(UsuarioRecord.class)
                );

        verifyNoInteractions(
                repository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName(
            "Não deve criar usuário com senha fraca"
    )
    void naoDeveCriarUsuarioComSenhaFraca() {
        ConstraintViolation<UsuarioRecord> violacao =
                criarViolacao(
                        "A senha deve atender aos requisitos de segurança"
                );

        when(validator.validate(
                any(UsuarioRecord.class)
        )).thenReturn(
                Set.of(violacao)
        );

        assertThatThrownBy(
                () -> service.criar(
                        "usuario@teste.com",
                        "senha"
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "A senha deve atender aos requisitos de segurança"
                );

        verify(validator)
                .validate(
                        any(UsuarioRecord.class)
                );

        verifyNoInteractions(
                repository,
                passwordEncoder
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "   "
    })
    @DisplayName(
            "Não deve criar usuário sem e-mail"
    )
    void naoDeveCriarUsuarioSemEmail(
            String email
    ) {
        assertThatThrownBy(
                () -> service.criar(
                        email,
                        "Senha@2026"
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "E-mail do usuario obrigatorio."
                );

        verifyNoInteractions(
                repository,
                passwordEncoder,
                validator
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "   "
    })
    @DisplayName(
            "Não deve criar usuário sem senha"
    )
    void naoDeveCriarUsuarioSemSenha(
            String senha
    ) {
        assertThatThrownBy(
                () -> service.criar(
                        "usuario@teste.com",
                        senha
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Senha do usuario obrigatoria."
                );

        verifyNoInteractions(
                repository,
                passwordEncoder,
                validator
        );
    }

    @SuppressWarnings("unchecked")
    private ConstraintViolation<UsuarioRecord>
            criarViolacao(
                    String mensagem
            ) {
        ConstraintViolation<UsuarioRecord> violacao =
                mock(ConstraintViolation.class);

        when(violacao.getMessage())
                .thenReturn(mensagem);

        return violacao;
    }
}