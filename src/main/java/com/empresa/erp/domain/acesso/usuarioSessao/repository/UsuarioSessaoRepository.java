package com.empresa.erp.domain.acesso.usuarioSessao.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.usuarioSessao.model.UsuarioSessaoModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioSessaoRepository extends JpaRepository<UsuarioSessaoModel, Long> {

    Optional<UsuarioSessaoModel> findByRefreshTokenHashAndStatus(
            String refreshTokenHash,
            StatusEnum status
    );

    List<UsuarioSessaoModel> findAllByUsuarioAndStatus(
            UsuarioModel usuario,
            StatusEnum status
    );

    List<UsuarioSessaoModel> findAllByUsuarioIdAndStatus(
            Long idUsuario,
            StatusEnum status
    );

    List<UsuarioSessaoModel> findAllByExpiraEmBeforeAndStatus(
            LocalDateTime dataHora,
            StatusEnum status
    );

    boolean existsByAccessTokenJtiAndStatus(
            String accessTokenJti,
            StatusEnum status
    );
}