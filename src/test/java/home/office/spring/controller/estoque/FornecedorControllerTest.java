package home.office.spring.controller.estoque;
/*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import home.office.spring.domain.fiscal.fornecedor.service.FornecedorService;
import home.office.spring.infra.security.DadosTokenJWT;

@SpringBootTest
@AutoConfigureMockMvc
class FornecedorControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private EntidadeService service;
	
	@DisplayName("Realiza o login")
	private String login() throws Exception {
		// ARRANGE
		String json = """
				{
				  "email": "admin@gmail.com",
				  "senha": "123456"
			    }
				""";
		// ACT
		var response = mvc.perform(
				post("/login")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
		).andReturn().getResponse();		
		DadosTokenJWT e = new Gson().fromJson(response.getContentAsString(), DadosTokenJWT.class);
		return e.token();				
	}
	
	@Test
	@DisplayName("Verica se o cadastro do fornecedor esta retornando erro 400")
	void cadastrar400() throws Exception {
		// ARRANGE
		String json = "{}";
		// ACT
		var response = mvc.perform(
				post("/fornecedor")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + this.login())
				
		).andReturn().getResponse();
		// ASSERT
		Assertions.assertEquals(400, response.getStatus());
	}
	
	@Test
	@DisplayName("Verica se o cadastro do fornecedor esta retornando 200")
	void cadastrar200() throws Exception {
		// ARRANGE
		String json = """
					{
						"cnpj": "18945626000121",
					 	"nome": "Google",
					 	"telefone": "00000000000",
					 	"descricao": ""
					 }
				""";
		// ACT
		var response = mvc.perform(
				post("/fornecedor")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + this.login())
				
		).andReturn().getResponse();
		// ASSERT
		Assertions.assertEquals(200, response.getStatus());
	}

}
*/