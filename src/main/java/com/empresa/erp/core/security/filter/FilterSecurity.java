package com.empresa.erp.core.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.domain.usuario.service.UsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FilterSecurity extends OncePerRequestFilter {

	private final TokenSecurity tokenService;
	
	private final UsuarioService usuarioService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		var tokenJWT = recuperarToken(request);
		if (tokenJWT != null) {
		    var subject = tokenService.getSubject(tokenJWT);
		    var usuarioAutenticado = usuarioService.loadUserByUsername(subject);

		    var authentication = new UsernamePasswordAuthenticationToken(
		            usuarioAutenticado,
		            null,
		            usuarioAutenticado.getAuthorities()
		    );

		    SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);		
	}

	private String recuperarToken(HttpServletRequest request) {
		var authorizationHeader = request.getHeader("Authorization");
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7).trim();
		}
		return null;
	}
	
}