package com.empresa.erp.core.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class TratarErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroResponse> tratarErro404() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponse(404, "NAO_ENCONTRADO", "Recurso nao encontrado"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacaoResponse> tratarErro400(MethodArgumentNotValidException ex) {
        var campos = ex.getFieldErrors().stream()
                .map(DadosErroValidacao::new)
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErroValidacaoResponse(400, "VALIDACAO", "Dados invalidos", campos));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarErro400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ErroResponse(400, "REQUISICAO_INVALIDA", "Corpo da requisicao invalido"));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResponse> tratarErroRegraDeNegocio(ValidacaoException ex) {
        return ResponseEntity.badRequest()
                .body(new ErroResponse(400, "REGRA_DE_NEGOCIO", ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErroResponse> tratarErroRefreshToken(RefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResponse(401, "REFRESH_TOKEN_INVALIDO", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResponse(401, "NAO_AUTENTICADO", "Credenciais invalidas"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> tratarErroAuthentication() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResponse(401, "NAO_AUTENTICADO", "Falha na autenticacao"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarErroAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErroResponse(403, "ACESSO_NEGADO", "Acesso negado"));
    }

    @ExceptionHandler(SsoAuthenticationException.class)
    public ResponseEntity<ErroResponse> tratarErroSsoAuthentication(SsoAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroResponse(401, "SSO_INVALIDO", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErro500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponse(500, "ERRO_INTERNO", "Erro interno do servidor"));
    }

    private record ErroResponse(
            int status,
            String erro,
            String mensagem
    ) {
    }

    private record ErroValidacaoResponse(
            int status,
            String erro,
            String mensagem,
            List<DadosErroValidacao> campos
    ) {
    }

    private record DadosErroValidacao(
            String campo,
            String mensagem
    ) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}