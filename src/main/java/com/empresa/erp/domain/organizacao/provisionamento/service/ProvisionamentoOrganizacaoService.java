package com.empresa.erp.domain.organizacao.provisionamento.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProvisionamentoOrganizacaoService {

    private static final EscopoPermissaoEnum
            ESCOPO_ORGANIZACAO =
            EscopoPermissaoEnum.ORGANIZACAO;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    private final PerfilRepository
            perfilRepository;

    private final PermissaoRepository
            permissaoRepository;

    private final PerfilPermissaoRepository
            perfilPermissaoRepository;

    private final UsuarioPerfilRepository
            usuarioPerfilRepository;

    @Transactional
    public OrganizacaoModel provisionar(
            String nomeOrganizacao,
            UsuarioModel administrador
    ) {
        validarDados(
                nomeOrganizacao,
                administrador
        );

        List<PermissaoModel> permissoes =
                buscarPermissoesOrganizacao();

        OrganizacaoModel organizacao =
                criarOrganizacao(
                        nomeOrganizacao
                );

        UsuarioOrganizacaoModel vinculoOrganizacao =
                criarVinculoOrganizacao(
                        administrador,
                        organizacao
                );

        PerfilModel perfilAdministrador =
                criarPerfilAdministrador(
                        organizacao
                );

        vincularPermissoes(
                perfilAdministrador,
                permissoes
        );

        vincularPerfilAdministrador(
                vinculoOrganizacao,
                perfilAdministrador
        );

        return organizacao;
    }

    private void validarDados(
            String nomeOrganizacao,
            UsuarioModel administrador
    ) {
        if (nomeOrganizacao == null
                || nomeOrganizacao.isBlank()) {
            throw new ValidacaoException(
                    "Nome da organizacao obrigatorio."
            );
        }

        if (administrador == null
                || administrador.getId() == null
                || !administrador.isEnabled()) {
            throw new ValidacaoException(
                    "Administrador da organizacao invalido."
            );
        }
    }

    private List<PermissaoModel>
            buscarPermissoesOrganizacao() {
        List<PermissaoModel> permissoes =
                permissaoRepository
                        .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                                ESCOPO_ORGANIZACAO,
                                StatusEnum.ATIVO
                        );

        if (permissoes.isEmpty()) {
            throw new IllegalStateException(
                    "Provisionamento da organizacao "
                            + "nao pode continuar: "
                            + "nenhuma permissao ativa "
                            + "do sistema foi encontrada."
            );
        }

        return permissoes;
    }

    private OrganizacaoModel criarOrganizacao(
            String nomeOrganizacao
    ) {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        nomeOrganizacao
                );

        return organizacaoRepository.save(
                organizacao
        );
    }

    private UsuarioOrganizacaoModel
            criarVinculoOrganizacao(
                    UsuarioModel administrador,
                    OrganizacaoModel organizacao
            ) {
        UsuarioOrganizacaoModel vinculo =
                new UsuarioOrganizacaoModel(
                        administrador,
                        organizacao
                );

        return usuarioOrganizacaoRepository.save(
                vinculo
        );
    }

    private PerfilModel criarPerfilAdministrador(
            OrganizacaoModel organizacao
    ) {
        PerfilModel perfil =
                PerfilModel.criarAdministradorSistema(
                        organizacao
                );

        return perfilRepository.save(perfil);
    }

    private void vincularPermissoes(
            PerfilModel perfil,
            List<PermissaoModel> permissoes
    ) {
        List<PerfilPermissaoModel> vinculos =
                permissoes.stream()
                        .map(permissao ->
                                new PerfilPermissaoModel(
                                        perfil,
                                        permissao
                                )
                        )
                        .toList();

        perfilPermissaoRepository.saveAll(vinculos);
    }

    private void vincularPerfilAdministrador(
            UsuarioOrganizacaoModel vinculoOrganizacao,
            PerfilModel perfilAdministrador
    ) {
        UsuarioPerfilModel usuarioPerfil =
                new UsuarioPerfilModel(
                        vinculoOrganizacao,
                        perfilAdministrador
                );

        usuarioPerfilRepository.save(usuarioPerfil);
    }
}