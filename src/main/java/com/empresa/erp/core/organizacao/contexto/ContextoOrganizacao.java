package com.empresa.erp.core.organizacao.contexto;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.empresa.erp.core.exception.ValidacaoException;

@Component
@RequestScope
public class ContextoOrganizacao {

    private Long idOrganizacao;

    public void definir(Long idOrganizacao) {
        validarId(idOrganizacao);

        if (this.idOrganizacao != null
                && !this.idOrganizacao.equals(idOrganizacao)) {
            throw new IllegalStateException(
                    "Organizacao da requisicao ja definida."
            );
        }

        this.idOrganizacao = idOrganizacao;
    }

    public Long getIdOrganizacao() {
        if (idOrganizacao == null) {
            throw new ValidacaoException(
                    "Organizacao nao informada."
            );
        }

        return idOrganizacao;
    }

    private void validarId(Long idOrganizacao) {
        if (idOrganizacao == null || idOrganizacao <= 0) {
            throw new ValidacaoException(
                    "Organizacao invalida."
            );
        }
    }
}