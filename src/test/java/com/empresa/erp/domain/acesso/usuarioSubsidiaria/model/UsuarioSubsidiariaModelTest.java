package com.empresa.erp.domain.acesso.usuarioSubsidiaria.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioSubsidiariaModelTest {

    @Test
    @DisplayName("Deve criar vinculo ativo")
    void deveCriarVinculoAtivo() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var subsidiaria = criarSubsidiaria(
                usuarioEmpresa.getEmpresa()
        );

        var usuarioSubsidiaria =
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                );

        assertThat(
                usuarioSubsidiaria.getUsuarioEmpresa()
        ).isSameAs(usuarioEmpresa);

        assertThat(
                usuarioSubsidiaria.getSubsidiaria()
        ).isSameAs(subsidiaria);

        assertThat(usuarioSubsidiaria.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve inativar vinculo")
    void deveInativarVinculo() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var usuarioSubsidiaria =
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        criarSubsidiaria(
                                usuarioEmpresa.getEmpresa()
                        )
                );

        usuarioSubsidiaria.inativar();

        assertThat(usuarioSubsidiaria.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo com auditoria")
    void deveRemoverVinculoComAuditoria() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var usuarioSubsidiaria =
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        criarSubsidiaria(
                                usuarioEmpresa.getEmpresa()
                        )
                );

        usuarioSubsidiaria.remover(10L);

        assertThat(usuarioSubsidiaria.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioSubsidiaria.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioSubsidiaria.getRemovidoEm())
                .isNotNull();
    }

    private UsuarioEmpresaModel criarUsuarioEmpresa() {
        var usuario = new UsuarioModel(
                new UsuarioRecord(
                        "usuario@teste.com",
                        "123456"
                ),
                "senha-criptografada"
        );

        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        var usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );

        return new UsuarioEmpresaModel(
                usuarioOrganizacao,
                empresa,
                false
        );
    }

    private SubsidiariaModel criarSubsidiaria(
            EmpresaModel empresa
    ) {
        return new SubsidiariaModel(
                empresa,
                "Filial Curitiba"
        );
    }
}