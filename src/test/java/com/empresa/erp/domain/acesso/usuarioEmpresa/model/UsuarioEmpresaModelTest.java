package com.empresa.erp.domain.acesso.usuarioEmpresa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioEmpresaModelTest {

    @Test
    @DisplayName("Deve criar vinculo ativo")
    void deveCriarVinculoAtivo() {
        var usuario = criarUsuario();
        var empresa = criarEmpresa();

        var usuarioEmpresa = new UsuarioEmpresaModel(
                usuario,
                empresa,
                true
        );

        assertThat(usuarioEmpresa.getUsuario())
                .isSameAs(usuario);

        assertThat(usuarioEmpresa.getEmpresa())
                .isSameAs(empresa);

        assertThat(usuarioEmpresa.getTodasSubsidiarias())
                .isTrue();

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar acesso a todas subsidiarias")
    void deveAtualizarAcessoATodasSubsidiarias() {
        var usuarioEmpresa = new UsuarioEmpresaModel(
                criarUsuario(),
                criarEmpresa(),
                false
        );

        usuarioEmpresa.atualizar(
                new AtualizaUsuarioEmpresaRecord(
                        1L,
                        true
                )
        );

        assertThat(usuarioEmpresa.getTodasSubsidiarias())
                .isTrue();
    }

    @Test
    @DisplayName("Deve inativar vinculo")
    void deveInativarVinculo() {
        var usuarioEmpresa = new UsuarioEmpresaModel(
                criarUsuario(),
                criarEmpresa(),
                false
        );

        usuarioEmpresa.inativar();

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo com auditoria")
    void deveRemoverVinculoComAuditoria() {
        var usuarioEmpresa = new UsuarioEmpresaModel(
                criarUsuario(),
                criarEmpresa(),
                false
        );

        usuarioEmpresa.remover(10L);

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioEmpresa.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioEmpresa.getRemovidoEm())
                .isNotNull();
    }

    private UsuarioModel criarUsuario() {
        return new UsuarioModel(
                new UsuarioRecord(
                        "usuario@teste.com",
                        "123456"
                ),
                "senha-criptografada"
        );
    }

    private EmpresaModel criarEmpresa() {
        return new EmpresaModel(
                new EmpresaRecord("Empresa Exemplo")
        );
    }
}