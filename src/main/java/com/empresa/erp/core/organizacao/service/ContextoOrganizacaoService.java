package com.empresa.erp.core.organizacao.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContextoOrganizacaoService {

    private final ContextoOrganizacao contextoOrganizacao;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    @Transactional(readOnly = true)
    public void definir(Long idOrganizacao) {
        validarId(idOrganizacao);

        Long idUsuario = usuarioLogadoService.getId();

        validarOrganizacaoAtiva(idOrganizacao);

        validarVinculoAtivo(
                idUsuario,
                idOrganizacao
        );

        contextoOrganizacao.definir(idOrganizacao);
    }

    private void validarId(Long idOrganizacao) {
        if (idOrganizacao == null || idOrganizacao <= 0) {
            throw new ValidacaoException(
                    "Organizacao invalida."
            );
        }
    }

    private void validarOrganizacaoAtiva(
            Long idOrganizacao
    ) {
        boolean organizacaoAtiva =
                organizacaoRepository.existsByIdAndStatus(
                        idOrganizacao,
                        StatusEnum.ATIVO
                );

        if (!organizacaoAtiva) {
            throw acessoNegado();
        }
    }

    private void validarVinculoAtivo(
            Long idUsuario,
            Long idOrganizacao
    ) {
        boolean vinculoAtivo =
                usuarioOrganizacaoRepository
                        .existsByUsuarioIdAndOrganizacaoIdAndStatus(
                                idUsuario,
                                idOrganizacao,
                                StatusEnum.ATIVO
                        );

        if (!vinculoAtivo) {
            throw acessoNegado();
        }
    }

    private AccessDeniedException acessoNegado() {
        return new AccessDeniedException(
                "Acesso negado."
        );
    }
}