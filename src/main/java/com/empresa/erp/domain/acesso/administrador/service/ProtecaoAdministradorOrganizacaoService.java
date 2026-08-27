package com.empresa.erp.domain.acesso.administrador.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.perfil.model.TipoPerfilSistemaEnum;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ProtecaoAdministradorOrganizacaoService {

    private final OrganizacaoRepository organizacaoRepository;

    private final UsuarioPerfilRepository usuarioPerfilRepository;

    public void validarInativacaoUsuario(
            UsuarioOrganizacaoModel vinculo,
            Long idOrganizacao
    ) {
        bloquearOrganizacao(idOrganizacao);

        validarSeUltimoAdministrador(
                vinculo.getId(),
                idOrganizacao
        );
    }

    public void validarRemocaoPerfil(
            UsuarioPerfilModel usuarioPerfil,
            Long idOrganizacao
    ) {
        if (!usuarioPerfil
                .getPerfil()
                .isAdministradorSistema()) {
            return;
        }

        bloquearOrganizacao(idOrganizacao);

        validarSeUltimoAdministrador(
                usuarioPerfil
                        .getUsuarioOrganizacao()
                        .getId(),
                idOrganizacao
        );
    }

    private void bloquearOrganizacao(Long idOrganizacao) {
        organizacaoRepository
                .buscarPorIdEStatusParaAtualizacao(
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Organizacao nao encontrada ou inativa."
                        )
                );
    }

    private void validarSeUltimoAdministrador(
            Long idUsuarioOrganizacao,
            Long idOrganizacao
    ) {
        boolean possuiPerfilAdministrador =
                usuarioPerfilRepository
                        .possuiPerfilAdministradorAtivo(
                                idUsuarioOrganizacao,
                                TipoPerfilSistemaEnum.ADMINISTRADOR,
                                StatusEnum.ATIVO
                        );

        if (!possuiPerfilAdministrador) {
            return;
        }

        boolean existeOutroAdministrador =
                usuarioPerfilRepository
                        .existeOutroAdministradorAtivo(
                                idOrganizacao,
                                idUsuarioOrganizacao,
                                TipoPerfilSistemaEnum.ADMINISTRADOR,
                                StatusEnum.ATIVO
                        );

        if (!existeOutroAdministrador) {
            throw new ValidacaoException(
                    "O ultimo administrador ativo da organizacao "
                            + "nao pode ser removido."
            );
        }
    }
}