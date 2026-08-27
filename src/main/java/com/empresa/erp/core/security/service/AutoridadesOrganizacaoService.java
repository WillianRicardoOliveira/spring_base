package com.empresa.erp.core.security.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.base.model.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutoridadesOrganizacaoService {

    private static final EscopoPermissaoEnum ESCOPO_ORGANIZACAO =
            EscopoPermissaoEnum.ORGANIZACAO;

    private final UsuarioPerfilRepository
            usuarioPerfilRepository;

    @Transactional(readOnly = true)
    public List<GrantedAuthority> buscar(
            Long idUsuario,
            Long idOrganizacao
    ) {
        return usuarioPerfilRepository
                .buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                        idUsuario,
                        idOrganizacao,
                        ESCOPO_ORGANIZACAO,
                        StatusEnum.ATIVO
                )
                .stream()
                .map(chave ->
                        (GrantedAuthority)
                                new SimpleGrantedAuthority(chave)
                )
                .toList();
    }
}