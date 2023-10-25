package home.office.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import home.office.spring.domain.usuario.model.UsuarioModel;
import home.office.spring.domain.usuario.record.UsuarioRecord;
import home.office.spring.infra.security.DadosTokenJWT;
import home.office.spring.infra.security.TokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity efetuarLogin(@RequestBody @Valid UsuarioRecord dados) {
		try {
			var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
			var authentication = manager.authenticate(authenticationToken);
			var tokenJWT = tokenService.gerarToken((UsuarioModel) authentication.getPrincipal());
			return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
		} catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	
}
