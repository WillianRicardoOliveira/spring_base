package com.empresa.erp.domain.organizacao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.record.OrganizacaoDisponivelRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizacaoDisponivelService {

    private final UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    private final UsuarioLogadoService
            usuarioLogadoService;

    @Transactional(readOnly = true)
    public List<OrganizacaoDisponivelRecord> listar() {
        Long idUsuario = usuarioLogadoService.getId();

        return usuarioOrganizacaoRepository
                .findAllByUsuarioIdAndStatusAndOrganizacaoStatusOrderByOrganizacaoNomeAsc(
                        idUsuario,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
                .stream()
                .map(OrganizacaoDisponivelRecord::new)
                .toList();
    }
}