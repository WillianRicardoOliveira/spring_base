package com.empresa.erp.domain.plataforma.acesso.perfilPermissao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.plataforma.acesso.perfilPermissao.model.PerfilPlataformaPermissaoModel;

public interface PerfilPlataformaPermissaoRepository
        extends JpaRepository<
                PerfilPlataformaPermissaoModel,
                Long
        > {
}