package home.office.spring.util;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

import home.office.spring.infra.exception.ValidacaoException;

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
	
}
