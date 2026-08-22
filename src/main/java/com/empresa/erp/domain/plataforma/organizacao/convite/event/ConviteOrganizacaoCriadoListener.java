package com.empresa.erp.domain.plataforma.organizacao.convite.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.empresa.erp.domain.plataforma.organizacao.convite.port.EnvioConviteOrganizacaoPort;

@Component
public class ConviteOrganizacaoCriadoListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ConviteOrganizacaoCriadoListener.class);

    private final EnvioConviteOrganizacaoPort envioConviteOrganizacaoPort;

    public ConviteOrganizacaoCriadoListener(
            EnvioConviteOrganizacaoPort envioConviteOrganizacaoPort
    ) {
        this.envioConviteOrganizacaoPort = envioConviteOrganizacaoPort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processar(ConviteOrganizacaoCriadoEvent evento) {
        try {
            envioConviteOrganizacaoPort.enviar(
                    evento.emailDestino(),
                    evento.nomeOrganizacao(),
                    evento.token(),
                    evento.expiraEm()
            );
        } catch (RuntimeException exception) {
            LOGGER.error(
                    "Não foi possível enviar o convite da organização. "
                            + "idConvite={}, tipoErro={}",
                    evento.idConvite(),
                    exception.getClass().getSimpleName()
            );
        }
    }
}