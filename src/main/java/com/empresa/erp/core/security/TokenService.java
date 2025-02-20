package com.empresa.erp.core.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.empresa.erp.domain.pessoa.model.PessoaModel;
import com.empresa.erp.domain.pessoa.repository.PessoaRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;
	
	@Value("${api.security.token.issuer}")
	private String issuer ;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public String gerarToken(UsuarioModel usuario) {
		PessoaModel pessoa = pessoaRepository.buscaPessoaUsuario(usuario.getId());
		try {
		    Algorithm algoritimo = Algorithm.HMAC256(secret);
		    return JWT.create()
		        .withIssuer(issuer)
		        .withSubject(usuario.getEmail())
		        .withClaim("id", pessoa.getId())
		        .withExpiresAt(dataExpiracao())
		        .sign(algoritimo);
		} catch (JWTCreationException exception){
		    throw new RuntimeException("erro ao gerar token jwt", exception);
		}		
	}
	
	public String getSubject(String tokenJWT) {
	    try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                            .withIssuer(issuer)
                            .build()
                            .verify(tokenJWT)
                            .getSubject();
	    } catch (JWTVerificationException exception) {
	    	throw new RuntimeException("Token JWT inválido ou expirado: " + tokenJWT);
	    }	
	}

	private Instant dataExpiracao() {	
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
	
}
