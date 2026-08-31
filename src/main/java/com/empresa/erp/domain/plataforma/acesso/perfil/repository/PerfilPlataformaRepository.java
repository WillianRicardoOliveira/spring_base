package com.empresa.erp.domain.plataforma.acesso.perfil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.plataforma.acesso.perfil.model.PerfilPlataformaModel;

public interface PerfilPlataformaRepository
        extends JpaRepository<PerfilPlataformaModel, Long> {
}