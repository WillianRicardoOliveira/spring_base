package com.empresa.erp.domain.acesso.usuarioLoginTentativa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.model.UsuarioLoginTentativaModel;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.repository.UsuarioLoginTentativaRepository;
import com.empresa.erp.domain.old.StatusEnum;

class UsuarioLoginTentativaServiceTest {

    private UsuarioLoginTentativaRepository repository;
    private UsuarioLoginTentativaService service;

    @BeforeEach
    void setUp() {
        repository = org.mockito.Mockito.mock(UsuarioLoginTentativaRepository.class);
        service = new UsuarioLoginTentativaService(repository);

        ReflectionTestUtils.setField(service, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(service, "lockMinutes", 15);
    }

    @Test
    @DisplayName("Deve validar configuracao correta")
    void deveValidarConfiguracaoCorreta() {
        service.validarConfiguracao();
    }

    @Test
    @DisplayName("Deve bloquear configuracao com limite de falhas menor que minimo")
    void deveBloquearConfiguracaoComLimiteDeFalhasMenorQueMinimo() {
        ReflectionTestUtils.setField(service, "maxFailedAttempts", 2);

        assertThatThrownBy(() -> service.validarConfiguracao())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("app.security.login.max-failed-attempts deve ser configurado com no minimo 3");
    }

    @Test
    @DisplayName("Deve bloquear configuracao com minutos de bloqueio menor que minimo")
    void deveBloquearConfiguracaoComMinutosDeBloqueioMenorQueMinimo() {
        ReflectionTestUtils.setField(service, "lockMinutes", 0);

        assertThatThrownBy(() -> service.validarConfiguracao())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("app.security.login.lock-minutes deve ser configurado com no minimo 1");
    }

    @Test
    @DisplayName("Deve permitir login quando nao houver tentativa registrada")
    void devePermitirLoginQuandoNaoHouverTentativaRegistrada() {
        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        service.validarLoginPermitido("Usuario@Teste.com");

        verify(repository).findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve permitir login quando tentativa nao estiver bloqueada")
    void devePermitirLoginQuandoTentativaNaoEstiverBloqueada() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.of(tentativa));

        service.validarLoginPermitido("usuario@teste.com");

        verify(repository).findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve bloquear login quando tentativa estiver bloqueada")
    void deveBloquearLoginQuandoTentativaEstiverBloqueada() {
        var tentativa = new UsuarioLoginTentativaModel(
                1L,
                "usuario@teste.com",
                5,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                StatusEnum.ATIVO
        );

        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.of(tentativa));

        assertThatThrownBy(() -> service.validarLoginPermitido("usuario@teste.com"))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Login temporariamente bloqueado. Tente novamente mais tarde.");

        verify(repository).findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve ignorar validacao quando email nao for informado")
    void deveIgnorarValidacaoQuandoEmailNaoForInformado() {
        service.validarLoginPermitido(null);
        service.validarLoginPermitido(" ");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve registrar primeira falha criando tentativa")
    void deveRegistrarPrimeiraFalhaCriandoTentativa() {
        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        service.registrarFalha("Usuario@Teste.com");

        ArgumentCaptor<UsuarioLoginTentativaModel> captor = ArgumentCaptor.forClass(UsuarioLoginTentativaModel.class);
        verify(repository).save(captor.capture());

        var tentativa = captor.getValue();

        assertThat(tentativa.getEmail()).isEqualTo("usuario@teste.com");
        assertThat(tentativa.getQuantidadeFalhas()).isEqualTo(1);
        assertThat(tentativa.getUltimaFalhaEm()).isNotNull();
        assertThat(tentativa.getBloqueadoAte()).isNull();
        assertThat(tentativa.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve registrar falha em tentativa existente")
    void deveRegistrarFalhaEmTentativaExistente() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");
        tentativa.registrarFalha(5, 15);

        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.of(tentativa));

        service.registrarFalha("usuario@teste.com");

        assertThat(tentativa.getQuantidadeFalhas()).isEqualTo(2);
        assertThat(tentativa.getUltimaFalhaEm()).isNotNull();
        assertThat(tentativa.getBloqueadoAte()).isNull();

        verify(repository).save(tentativa);
    }

    @Test
    @DisplayName("Deve bloquear tentativa ao atingir limite de falhas")
    void deveBloquearTentativaAoAtingirLimiteDeFalhas() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");

        tentativa.registrarFalha(5, 15);
        tentativa.registrarFalha(5, 15);
        tentativa.registrarFalha(5, 15);
        tentativa.registrarFalha(5, 15);

        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.of(tentativa));

        service.registrarFalha("usuario@teste.com");

        assertThat(tentativa.getQuantidadeFalhas()).isEqualTo(5);
        assertThat(tentativa.getBloqueadoAte()).isAfter(LocalDateTime.now());

        verify(repository).save(tentativa);
    }

    @Test
    @DisplayName("Deve ignorar registro de falha quando email nao for informado")
    void deveIgnorarRegistroDeFalhaQuandoEmailNaoForInformado() {
        service.registrarFalha(null);
        service.registrarFalha(" ");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve limpar falhas ao registrar sucesso")
    void deveLimparFalhasAoRegistrarSucesso() {
        var tentativa = new UsuarioLoginTentativaModel("usuario@teste.com");
        tentativa.registrarFalha(5, 15);
        tentativa.registrarFalha(5, 15);

        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.of(tentativa));

        service.registrarSucesso("usuario@teste.com");

        assertThat(tentativa.getQuantidadeFalhas()).isZero();
        assertThat(tentativa.getUltimaFalhaEm()).isNull();
        assertThat(tentativa.getBloqueadoAte()).isNull();

        verify(repository).save(tentativa);
    }

    @Test
    @DisplayName("Deve ignorar sucesso quando tentativa nao existir")
    void deveIgnorarSucessoQuandoTentativaNaoExistir() {
        when(repository.findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        service.registrarSucesso("usuario@teste.com");

        verify(repository).findByEmailIgnoreCaseAndStatus("usuario@teste.com", StatusEnum.ATIVO);
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Deve ignorar registro de sucesso quando email nao for informado")
    void deveIgnorarRegistroDeSucessoQuandoEmailNaoForInformado() {
        service.registrarSucesso(null);
        service.registrarSucesso(" ");

        verifyNoInteractions(repository);
    }
}