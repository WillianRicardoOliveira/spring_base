package home.office.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import home.office.spring.domain.endereco.record.EnderecoRecord;
import home.office.spring.domain.endereco.service.EnderecoService;
import home.office.spring.infra.exception.ValidacaoException;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {
	
	@Autowired
	private EnderecoService service;
		
	@GetMapping("/buscar/{cep}")
	public ResponseEntity<EnderecoRecord> buscaDadosEndereco(@PathVariable Long cep) throws Exception {
		try {
			return ResponseEntity.ok(service.buscaDadosEndereco(cep));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a busca do endereço.");
		}
	}

}
