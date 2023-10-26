package home.office.spring.domain.endereco.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import home.office.spring.domain.endereco.model.EnderecoModel;
import home.office.spring.domain.endereco.record.EnderecoRecord;

@Service
public class EnderecoService {

	public EnderecoRecord buscaDadosEndereco(String cep) throws Exception {
		URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
		URLConnection con = url.openConnection();
		InputStream i = con.getInputStream();
		BufferedReader b = new BufferedReader(new InputStreamReader(i, "UTF-8"));
		StringBuilder jsonCep = new StringBuilder();
		while((cep = b.readLine()) != null) {
			jsonCep.append(cep);
		}
		EnderecoModel e = new Gson().fromJson(jsonCep.toString(), EnderecoModel.class); 
		return new EnderecoRecord(e);		
	}
	
}
