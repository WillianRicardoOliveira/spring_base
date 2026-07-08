package com.empresa.erp.domain.acesso.perfilPermissao.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.old.StatusEnum;

class PerfilPermissaoModelTest {

    @Test
    @DisplayName("Deve criar vinculo ativo entre perfil e permissao")
    void deveCriarVinculoAtivoEntrePerfilEPermissao() {
        var perfil = criarPerfil(1L, "Administrador");
        var permissao = criarPermissao(2L, "Listar usuarios", "ACESSO_USUARIO_LISTAR");

        var perfilPermissao = new PerfilPermissaoModel(perfil, permissao);

        assertThat(perfilPermissao.getPerfil()).isEqualTo(perfil);
        assertThat(perfilPermissao.getPermissao()).isEqualTo(permissao);
        assertThat(perfilPermissao.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo entre perfil e permissao registrando auditoria")
    void deveRemoverVinculoEntrePerfilEPermissaoRegistrandoAuditoria() {
        var perfil = criarPerfil(1L, "Administrador");
        var permissao = criarPermissao(2L, "Listar usuarios", "ACESSO_USUARIO_LISTAR");
        var perfilPermissao = new PerfilPermissaoModel(perfil, permissao);

        perfilPermissao.remover(10L);

        assertThat(perfilPermissao.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(perfilPermissao.getRemovidoPor()).isEqualTo(10L);
        assertThat(perfilPermissao.getRemovidoEm()).isNotNull();
    }

    private PerfilModel criarPerfil(Long id, String nome) {
        var perfil = new PerfilModel(new PerfilRecord(nome, "Perfil " + nome));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }

    private PermissaoModel criarPermissao(Long id, String nome, String chave) {
        var permissao = new PermissaoModel(new PermissaoRecord(nome, chave, "Permite " + nome));
        ReflectionTestUtils.setField(permissao, "id", id);
        return permissao;
    }
}