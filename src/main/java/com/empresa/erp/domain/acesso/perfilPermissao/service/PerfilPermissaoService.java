package com.empresa.erp.domain.acesso.perfilPermissao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.DetalhePerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.ListaPerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilPermissaoService {

    private final PerfilPermissaoRepository repository;
    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;
    
    private final UsuarioLogadoService usuarioLogadoService;

    @Transactional
    public PerfilPermissaoModel cadastrar(PerfilPermissaoRecord dados) {
        PerfilModel perfil = perfilRepository.getReferenceById(dados.idPerfil());
        PermissaoModel permissao = permissaoRepository.getReferenceById(dados.idPermissao());
        if (repository.existsByPerfilAndPermissaoAndStatus(perfil, permissao, StatusEnum.ATIVO)) {
            throw new ValidacaoException("Permissao ja vinculada ao perfil.");
        }
        PerfilPermissaoModel perfilPermissao = new PerfilPermissaoModel(perfil, permissao);
        repository.save(perfilPermissao);
        return perfilPermissao;
    }

    @Transactional(readOnly = true)
    public Page<ListaPerfilPermissaoRecord> listarPorPerfil(Pageable paginacao, Long idPerfil) {
        return repository.findAllByPerfilIdAndStatus(paginacao, idPerfil, StatusEnum.ATIVO).map(ListaPerfilPermissaoRecord::new);
    }

    @Transactional
    public void excluir(Long id) {
        Long idUsuario = usuarioLogadoService.getId();

        PerfilPermissaoModel perfilPermissao = repository.getReferenceById(id);
        perfilPermissao.remover(idUsuario);
    }

    @Transactional(readOnly = true)
    public DetalhePerfilPermissaoRecord detalhar(Long id) {
        PerfilPermissaoModel perfilPermissao = repository.getReferenceById(id);
        return new DetalhePerfilPermissaoRecord(perfilPermissao);
    }
    
}