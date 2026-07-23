package com.empresa.erp.domain.acesso.usuarioLoginTentativa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.usuarioLoginTentativa.model.UsuarioLoginTentativaModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface UsuarioLoginTentativaRepository extends JpaRepository<UsuarioLoginTentativaModel, Long> {

    Optional<UsuarioLoginTentativaModel> findByEmailIgnoreCaseAndStatus(
            String email,
            StatusEnum status
    );
}