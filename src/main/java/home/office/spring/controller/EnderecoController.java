package home.office.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import home.office.spring.domain.endereco.record.EnderecoRecord;
import home.office.spring.domain.endereco.service.EnderecoService;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {
	
	@Autowired
	private EnderecoService service;
		
	@GetMapping("/buscar/{cep}")
	public ResponseEntity<EnderecoRecord> buscaDadosEndereco(@PathVariable String cep) throws Exception {
		return ResponseEntity.ok(service.buscaDadosEndereco(cep));	
	}

}
