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
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class EnderecoService {

	public EnderecoRecord buscaDadosEndereco(Long cep) throws Exception {
		try {
			String valor = Long.valueOf(cep).toString();
			URL url = new URL("https://viacep.com.br/ws/" + valor + "/json/");
			URLConnection con = url.openConnection();
			InputStream i = con.getInputStream();
			BufferedReader b = new BufferedReader(new InputStreamReader(i, "UTF-8"));
			StringBuilder jsonCep = new StringBuilder();
			String dados = "";
			while((dados = b.readLine()) != null) {
				jsonCep.append(dados);
			}
			EnderecoModel e = new Gson().fromJson(jsonCep.toString(), EnderecoModel.class); 
			return new EnderecoRecord(e);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível buscar os dados do endereço.");
		}
	}
	
}
