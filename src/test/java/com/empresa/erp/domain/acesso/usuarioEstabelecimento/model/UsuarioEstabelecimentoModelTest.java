package com.empresa.erp.domain.acesso.usuarioEstabelecimento.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioEstabelecimentoModelTest {

    @Test
    @DisplayName("Deve criar vinculo ativo")
    void deveCriarVinculoAtivo() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var estabelecimento = criarEstabelecimento(
                usuarioEmpresa.getEmpresa()
        );

        var usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        estabelecimento
                );

        assertThat(
                usuarioEstabelecimento.getUsuarioEmpresa()
        ).isSameAs(usuarioEmpresa);

        assertThat(
                usuarioEstabelecimento.getEstabelecimento()
        ).isSameAs(estabelecimento);

        assertThat(usuarioEstabelecimento.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve inativar vinculo")
    void deveInativarVinculo() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        criarEstabelecimento(
                                usuarioEmpresa.getEmpresa()
                        )
                );

        usuarioEstabelecimento.inativar();

        assertThat(usuarioEstabelecimento.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo com auditoria")
    void deveRemoverVinculoComAuditoria() {
        var usuarioEmpresa = criarUsuarioEmpresa();

        var usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        criarEstabelecimento(
                                usuarioEmpresa.getEmpresa()
                        )
                );

        usuarioEstabelecimento.remover(10L);

        assertThat(usuarioEstabelecimento.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioEstabelecimento.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioEstabelecimento.getRemovidoEm())
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

    private EstabelecimentoModel criarEstabelecimento(
            EmpresaModel empresa
    ) {
        return new EstabelecimentoModel(
                empresa,
                "Filial Curitiba"
        );
    }
}