package com.empresa.erp.util;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.text.MaskFormatter;

import com.empresa.erp.core.exception.ValidacaoException;

public class Formatacao {
	
	public static String formataCnpj(String cnpj) {
        try {
            MaskFormatter mask = new MaskFormatter("##.###.###/####-##");
            mask.setValueContainsLiteralCharacters(false);
            return mask.valueToString(cnpj);
        } catch (ParseException e) {
            throw new ValidacaoException("Erro ao formatar CNPJ.");
        }
    }
	
	public static String formataTelefone(String telefone) {
        try {
            MaskFormatter mask = new MaskFormatter("(##) #####-####");
            mask.setValueContainsLiteralCharacters(false);
            return mask.valueToString(telefone);
        } catch (ParseException e) {
            throw new ValidacaoException("Erro ao formatar o telefone.");
        }
    }
	
	public static String formataData(String data) {
        LocalDateTime localDateTime = LocalDateTime.parse(data);
        DateTimeFormatter novoFormato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = localDateTime.format(novoFormato);
        return dataFormatada;
	}
		
}
