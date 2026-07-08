package com.empresa.erp.domain.acesso.permissao.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PermissaoRepositoryTest {

    @Autowired
    private PermissaoRepository repository;

    @Test
    @DisplayName("Deve listar permissoes ativas")
    void deveListarPermissoesAtivas() {
        var permissaoAtiva = repository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        var permissaoInativa = repository.save(new PermissaoModel(new PermissaoRecord(
                "Editar usuarios",
                "ACESSO_USUARIO_EDITAR",
                "Permite editar usuarios"
        )));
        permissaoInativa.inativar();
        repository.save(permissaoInativa);

        var resultado = repository.findAllByStatus(PageRequest.of(0, 10), StatusEnum.ATIVO);

        assertThat(resultado.getContent())
                .extracting(PermissaoModel::getId)
                .contains(permissaoAtiva.getId())
                .doesNotContain(permissaoInativa.getId());
    }

    @Test
    @DisplayName("Deve filtrar permissoes ativas por nome ignorando caixa")
    void deveFiltrarPermissoesAtivasPorNomeIgnorandoCaixa() {
        repository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        repository.save(new PermissaoModel(new PermissaoRecord(
                "Editar perfis",
                "ACESSO_PERFIL_EDITAR",
                "Permite editar perfis"
        )));

        var resultado = repository.findByNomeContainingIgnoreCaseAndStatus(
                PageRequest.of(0, 10),
                "LISTAR",
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Listar usuarios");
    }

    @Test
    @DisplayName("Deve verificar existencia por chave ignorando caixa e status")
    void deveVerificarExistenciaPorChaveIgnorandoCaixaEStatus() {
        repository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        var existe = repository.existsByChaveIgnoreCaseAndStatus(
                "acesso_usuario_listar",
                StatusEnum.ATIVO
        );

        var naoExiste = repository.existsByChaveIgnoreCaseAndStatus(
                "acesso_usuario_listar",
                StatusEnum.REMOVIDO
        );

        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    @DisplayName("Deve verificar existencia por chave excluindo id")
    void deveVerificarExistenciaPorChaveExcluindoId() {
        var permissao = repository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        repository.save(new PermissaoModel(new PermissaoRecord(
                "Editar usuarios",
                "ACESSO_USUARIO_EDITAR",
                "Permite editar usuarios"
        )));

        var existeEmOutroRegistro = repository.existsByChaveIgnoreCaseAndStatusAndIdNot(
                "acesso_usuario_editar",
                StatusEnum.ATIVO,
                permissao.getId()
        );

        var naoExisteNoMesmoRegistro = repository.existsByChaveIgnoreCaseAndStatusAndIdNot(
                "acesso_usuario_listar",
                StatusEnum.ATIVO,
                permissao.getId()
        );

        assertThat(existeEmOutroRegistro).isTrue();
        assertThat(naoExisteNoMesmoRegistro).isFalse();
    }
}