package com.empresa.erp.domain.fiscal.endereco.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;
import com.empresa.erp.domain.fiscal.endereco.record.AtualizaEnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.record.DetalheEnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.record.EnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.record.ListaEnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.repository.EnderecoRepository;
import com.google.gson.Gson;

import jakarta.transaction.Transactional;

@Service
public class EnderecoService {
	
	@Autowired
	private EnderecoRepository repository;
	
	@Transactional
	public EnderecoModel cadastrar(EnderecoRecord dados) {		
		try {
			var endereco = new EnderecoModel(dados);
			repository.save(endereco);		
			return endereco;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do Endereço.");
		}
	}
	
	public Page<ListaEnderecoRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByLogradouroContaining(paginacao, filtro).map(ListaEnderecoRecord::new);
			} else {			
				return repository.findAllByAtivoTrue(paginacao).map(ListaEnderecoRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos Endereços.");
		}
	}
	
	/*
	@Transactional
	public DetalheEnderecoRecord atualizar(AtualizaEnderecoRecord dados) {
		try {
			EnderecoModel endereco = repository.getReferenceById(dados.id());
			endereco.atualizar(dados);
			return new DetalheEnderecoRecord(endereco);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do Endereço.");
		}
	}
	*/
	@Transactional
	public void excluir(Long id, Boolean ativo) {
		try {
			repository.getReferenceById(id).ativo(ativo);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do Endereço.");
		}
	}
	
	public DetalheEnderecoRecord detalhar(Long id) {
		try {
			EnderecoModel endereco = repository.getReferenceById(id);
			return new DetalheEnderecoRecord(endereco);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do Endereço.");
		}
	}
	
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
