package com.empresa.erp.domain.usuario.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    @DisplayName("Deve buscar usuario por email ignorando caixa")
    void deveBuscarUsuarioPorEmailIgnorandoCaixa() {
        repository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var resultado = repository.findByEmailIgnoreCase("USUARIO@TESTE.COM");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("usuario@teste.com");
    }

    @Test
    @DisplayName("Deve listar usuarios ativos")
    void deveListarUsuariosAtivos() {
        var usuarioAtivo = repository.save(new UsuarioModel(
                new UsuarioRecord("ativo@teste.com", "123456"),
                "senha-criptografada"
        ));

        var usuarioInativo = repository.save(new UsuarioModel(
                new UsuarioRecord("inativo@teste.com", "123456"),
                "senha-criptografada"
        ));
        usuarioInativo.inativar();
        repository.save(usuarioInativo);

        var resultado = repository.findAllByStatus(PageRequest.of(0, 10), StatusEnum.ATIVO);

        assertThat(resultado.getContent())
                .extracting(UsuarioModel::getId)
                .contains(usuarioAtivo.getId())
                .doesNotContain(usuarioInativo.getId());
    }

    @Test
    @DisplayName("Deve filtrar usuarios ativos por email ignorando caixa")
    void deveFiltrarUsuariosAtivosPorEmailIgnorandoCaixa() {
        repository.save(new UsuarioModel(
                new UsuarioRecord("financeiro@teste.com", "123456"),
                "senha-criptografada"
        ));

        repository.save(new UsuarioModel(
                new UsuarioRecord("admin@teste.com", "123456"),
                "senha-criptografada"
        ));

        var resultado = repository.findByEmailContainingIgnoreCaseAndStatus(
                PageRequest.of(0, 10),
                "FIN",
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEmail()).isEqualTo("financeiro@teste.com");
    }

    @Test
    @DisplayName("Deve verificar existencia por email ignorando caixa")
    void deveVerificarExistenciaPorEmailIgnorandoCaixa() {
        repository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var existe = repository.existsByEmailIgnoreCase("USUARIO@TESTE.COM");
        var naoExiste = repository.existsByEmailIgnoreCase("outro@teste.com");

        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    @DisplayName("Deve verificar existencia por email excluindo id")
    void deveVerificarExistenciaPorEmailExcluindoId() {
        var usuario = repository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        repository.save(new UsuarioModel(
                new UsuarioRecord("outro@teste.com", "123456"),
                "senha-criptografada"
        ));

        var existeEmOutroRegistro = repository.existsByEmailIgnoreCaseAndIdNot(
                "OUTRO@TESTE.COM",
                usuario.getId()
        );

        var naoExisteNoMesmoRegistro = repository.existsByEmailIgnoreCaseAndIdNot(
                "USUARIO@TESTE.COM",
                usuario.getId()
        );

        assertThat(existeEmOutroRegistro).isTrue();
        assertThat(naoExisteNoMesmoRegistro).isFalse();
    }

    @Test
    @DisplayName("Deve buscar usuario por id e status")
    void deveBuscarUsuarioPorIdEStatus() {
        var usuario = repository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var resultado = repository.findByIdAndStatus(usuario.getId(), StatusEnum.ATIVO);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("usuario@teste.com");
    }
}