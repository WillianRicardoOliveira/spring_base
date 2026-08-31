package com.empresa.erp.core.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.AutoridadesPlataformaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AutoridadesPlataformaFilter
        extends OncePerRequestFilter {

    private final AutoridadesPlataformaService
            autoridadesPlataformaService;

    public AutoridadesPlataformaFilter(
            AutoridadesPlataformaService
                    autoridadesPlataformaService
    ) {
        this.autoridadesPlataformaService =
                autoridadesPlataformaService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication =
                obterAutenticacao();

        if (!possuiUsuarioAutenticado(authentication)) {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        UsuarioAutenticado usuarioAutenticado =
                (UsuarioAutenticado)
                        authentication.getPrincipal();

        Collection<? extends GrantedAuthority>
                autoridadesPlataforma =
                autoridadesPlataformaService.buscar(
                        usuarioAutenticado.getId()
                );

        Collection<? extends GrantedAuthority>
                autoridadesCombinadas =
                combinarAutoridades(
                        authentication.getAuthorities(),
                        autoridadesPlataforma
                );

        substituirAutoridades(
                authentication,
                usuarioAutenticado,
                autoridadesCombinadas
        );

        filterChain.doFilter(
                request,
                response
        );
    }

    private Authentication obterAutenticacao() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }

    private boolean possuiUsuarioAutenticado(
            Authentication authentication
    ) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof UsuarioAutenticado;
    }

    private Collection<? extends GrantedAuthority>
            combinarAutoridades(
                    Collection<? extends GrantedAuthority>
                            autoridadesAtuais,
                    Collection<? extends GrantedAuthority>
                            autoridadesPlataforma
            ) {
        Set<GrantedAuthority> autoridades =
                new LinkedHashSet<>(
                        autoridadesAtuais
                );

        autoridades.addAll(
                autoridadesPlataforma
        );

        return autoridades;
    }

    private void substituirAutoridades(
            Authentication authenticationAtual,
            UsuarioAutenticado usuarioAutenticado,
            Collection<? extends GrantedAuthority> autoridades
    ) {
        var novaAutenticacao =
                new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado,
                        null,
                        autoridades
                );

        novaAutenticacao.setDetails(
                authenticationAtual.getDetails()
        );

        SecurityContextHolder
                .getContext()
                .setAuthentication(novaAutenticacao);
    }
}