package com.empresa.erp.core.organizacao.filter;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.service.ContextoOrganizacaoService;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ContextoOrganizacaoFilter
        extends OncePerRequestFilter {

    public static final String HEADER_ORGANIZACAO =
            "X-Organizacao-Id";

    private final ContextoOrganizacaoService
            contextoOrganizacaoService;

    private final AccessDeniedHandler
            acessoNegadoHandler;

    private final ObjectMapper objectMapper;

    public ContextoOrganizacaoFilter(
            ContextoOrganizacaoService contextoOrganizacaoService,
            AccessDeniedHandler acessoNegadoHandler,
            ObjectMapper objectMapper
    ) {
        this.contextoOrganizacaoService =
                contextoOrganizacaoService;
        this.acessoNegadoHandler =
                acessoNegadoHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String headerOrganizacao =
                request.getHeader(HEADER_ORGANIZACAO);

        if (headerOrganizacao == null
                || !possuiUsuarioAutenticado()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long idOrganizacao =
                    converterId(headerOrganizacao);

            contextoOrganizacaoService.definir(
                    idOrganizacao
            );
        } catch (AccessDeniedException exception) {
            acessoNegadoHandler.handle(
                    request,
                    response,
                    exception
            );
            return;
        } catch (ValidacaoException exception) {
            escreverErro400(
                    response,
                    exception.getMessage()
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean possuiUsuarioAutenticado() {
        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof UsuarioAutenticado;
    }

    private Long converterId(String valor) {
        if (valor.isBlank()) {
            throw organizacaoInvalida();
        }

        try {
            return Long.valueOf(valor.trim());
        } catch (NumberFormatException exception) {
            throw organizacaoInvalida();
        }
    }

    private ValidacaoException organizacaoInvalida() {
        return new ValidacaoException(
                "Organizacao invalida."
        );
    }

    private void escreverErro400(
            HttpServletResponse response,
            String mensagem
    ) throws IOException {
        response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
        );
        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
                response.getWriter(),
                new ErroResponse(
                        400,
                        "REGRA_DE_NEGOCIO",
                        mensagem
                )
        );
    }

    private record ErroResponse(
            int status,
            String erro,
            String mensagem
    ) {
    }
}