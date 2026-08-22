package com.empresa.erp.core.security.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.repository.UsuarioPerfilPlataformaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutoridadesPlataformaService {

    private static final EscopoPermissaoEnum ESCOPO_PLATAFORMA =
            EscopoPermissaoEnum.PLATAFORMA;

    private final UsuarioPerfilPlataformaRepository
            usuarioPerfilPlataformaRepository;

    @Transactional(readOnly = true)
    public List<GrantedAuthority> buscar(
            Long idUsuario
    ) {
        return usuarioPerfilPlataformaRepository
                .buscarChavesPermissoesAtivasPorUsuario(
                        idUsuario,
                        ESCOPO_PLATAFORMA,
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