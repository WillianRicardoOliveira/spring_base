package com.empresa.erp.core.bootstrap.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.bootstrap.config.BootstrapProperties;
import com.empresa.erp.core.bootstrap.validation.BootstrapPropertiesValidator;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.provisionamento.service.ProvisionamentoOrganizacaoService;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.plataforma.acesso.perfil.model.PerfilPlataformaModel;
import com.empresa.erp.domain.plataforma.acesso.perfil.repository.PerfilPlataformaRepository;
import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.model.PerfilPlataformaPermissaoModel;
import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.repository.PerfilPlataformaPermissaoRepository;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.model.UsuarioPerfilPlataformaModel;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.repository.UsuarioPerfilPlataformaRepository;
import com.empresa.erp.domain.usuario.criacao.service.CriacaoUsuarioService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootstrapService {

    private static final EscopoPermissaoEnum
            ESCOPO_PLATAFORMA =
            EscopoPermissaoEnum.PLATAFORMA;

    private final BootstrapProperties properties;

    private final BootstrapPropertiesValidator
            propertiesValidator;

    private final ProvisionamentoOrganizacaoService
            provisionamentoOrganizacaoService;

    private final CriacaoUsuarioService
            criacaoUsuarioService;

    private final OrganizacaoRepository
            organizacaoRepository;

    private final UsuarioRepository
            usuarioRepository;

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

    private final PerfilPlataformaRepository
            perfilPlataformaRepository;

    private final PerfilPlataformaPermissaoRepository
            perfilPlataformaPermissaoRepository;

    private final UsuarioPerfilPlataformaRepository
            usuarioPerfilPlataformaRepository;

    @Transactional
    public boolean provisionar() {
        if (!properties.enabled()) {
            return false;
        }

        if (instalacaoJaProvisionada()) {
            return false;
        }

        propertiesValidator.validar(properties);

        List<PermissaoModel> permissoesPlataforma =
                buscarPermissoesPlataforma();

        UsuarioModel administradorOrganizacao =
                criacaoUsuarioService.criar(
                        properties.organizationAdminEmail(),
                        properties.organizationAdminPassword()
                );

        provisionamentoOrganizacaoService.provisionar(
                properties.organizationName(),
                administradorOrganizacao
        );

        UsuarioModel administradorPlataforma =
                criarAdministradorPlataforma(
                        administradorOrganizacao
                );

        PerfilPlataformaModel perfilAdministradorPlataforma =
                criarPerfilAdministradorPlataforma();

        vincularPermissoesPlataforma(
                perfilAdministradorPlataforma,
                permissoesPlataforma
        );

        vincularPerfilAoAdministradorPlataforma(
                administradorPlataforma,
                perfilAdministradorPlataforma
        );

        return true;
    }

    private boolean instalacaoJaProvisionada() {
        long quantidadeOrganizacoes =
                organizacaoRepository.count();

        long quantidadeUsuarios =
                usuarioRepository.count();

        long quantidadeVinculosOrganizacao =
                usuarioOrganizacaoRepository.count();

        long quantidadePerfisOrganizacao =
                perfilRepository.count();

        long quantidadePermissoesPerfisOrganizacao =
                perfilPermissaoRepository.count();

        long quantidadePerfisUsuariosOrganizacao =
                usuarioPerfilRepository.count();

        long quantidadePerfisPlataforma =
                perfilPlataformaRepository.count();

        long quantidadePermissoesPerfisPlataforma =
                perfilPlataformaPermissaoRepository.count();

        long quantidadePerfisUsuariosPlataforma =
                usuarioPerfilPlataformaRepository.count();

        boolean instalacaoVazia =
                quantidadeOrganizacoes == 0
                        && quantidadeUsuarios == 0
                        && quantidadeVinculosOrganizacao == 0
                        && quantidadePerfisOrganizacao == 0
                        && quantidadePermissoesPerfisOrganizacao == 0
                        && quantidadePerfisUsuariosOrganizacao == 0
                        && quantidadePerfisPlataforma == 0
                        && quantidadePermissoesPerfisPlataforma == 0
                        && quantidadePerfisUsuariosPlataforma == 0;

        if (instalacaoVazia) {
            return false;
        }

        boolean instalacaoCompleta =
                quantidadeOrganizacoes > 0
                        && quantidadeUsuarios > 0
                        && quantidadeVinculosOrganizacao > 0
                        && quantidadePerfisOrganizacao > 0
                        && quantidadePermissoesPerfisOrganizacao > 0
                        && quantidadePerfisUsuariosOrganizacao > 0
                        && quantidadePerfisPlataforma > 0
                        && quantidadePermissoesPerfisPlataforma > 0
                        && quantidadePerfisUsuariosPlataforma > 0;

        if (instalacaoCompleta) {
            return true;
        }

        throw new IllegalStateException(
                "Bootstrap nao pode continuar: "
                        + "estado inicial do banco inconsistente"
        );
    }

    private List<PermissaoModel>
            buscarPermissoesPlataforma() {
        List<PermissaoModel> permissoes =
                permissaoRepository
                        .findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                                ESCOPO_PLATAFORMA,
                                StatusEnum.ATIVO
                        );

        if (permissoes.isEmpty()) {
            throw new IllegalStateException(
                    "Bootstrap nao pode continuar: "
                            + "nenhuma permissao ativa do sistema "
                            + "encontrada para o escopo plataforma"
            );
        }

        return permissoes;
    }

    private UsuarioModel criarAdministradorPlataforma(
            UsuarioModel administradorOrganizacao
    ) {
        if (administradoresUtilizamMesmaIdentidade()) {
            return administradorOrganizacao;
        }

        return criacaoUsuarioService.criar(
                properties.platformAdminEmail(),
                properties.platformAdminPassword()
        );
    }

    private boolean administradoresUtilizamMesmaIdentidade() {
        String emailOrganizacao =
                normalizarEmail(
                        properties.organizationAdminEmail()
                );

        String emailPlataforma =
                normalizarEmail(
                        properties.platformAdminEmail()
                );

        return emailOrganizacao.equals(emailPlataforma);
    }

    private String normalizarEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private PerfilPlataformaModel
            criarPerfilAdministradorPlataforma() {
        PerfilPlataformaModel perfil =
                PerfilPlataformaModel
                        .criarAdministradorSistema();

        return perfilPlataformaRepository.save(perfil);
    }

    private void vincularPermissoesPlataforma(
            PerfilPlataformaModel perfil,
            List<PermissaoModel> permissoes
    ) {
        List<PerfilPlataformaPermissaoModel> vinculos =
                permissoes.stream()
                        .map(permissao ->
                                new PerfilPlataformaPermissaoModel(
                                        perfil,
                                        permissao
                                )
                        )
                        .toList();

        perfilPlataformaPermissaoRepository
                .saveAll(vinculos);
    }

    private void vincularPerfilAoAdministradorPlataforma(
            UsuarioModel administrador,
            PerfilPlataformaModel perfilAdministrador
    ) {
        UsuarioPerfilPlataformaModel usuarioPerfil =
                new UsuarioPerfilPlataformaModel(
                        administrador,
                        perfilAdministrador
                );

        usuarioPerfilPlataformaRepository.save(
                usuarioPerfil
        );
    }
}