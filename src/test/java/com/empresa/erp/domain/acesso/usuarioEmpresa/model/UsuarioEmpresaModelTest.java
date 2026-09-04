package com.empresa.erp.domain.acesso.usuarioEmpresa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioEmpresaModelTest {

    @Test
    @DisplayName("Deve criar vinculo ativo")
    void deveCriarVinculoAtivo() {
        var organizacao = criarOrganizacao();
        var usuario = criarUsuario();
        var usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        usuario,
                        organizacao
                );
        var empresa = criarEmpresa(organizacao);

        var usuarioEmpresa = new UsuarioEmpresaModel(
                usuarioOrganizacao,
                empresa,
                true
        );

        assertThat(usuarioEmpresa.getUsuarioOrganizacao())
                .isSameAs(usuarioOrganizacao);

        assertThat(
                usuarioEmpresa
                        .getUsuarioOrganizacao()
                        .getUsuario()
        ).isSameAs(usuario);

        assertThat(usuarioEmpresa.getEmpresa())
                .isSameAs(empresa);

        assertThat(usuarioEmpresa.getTodosEstabelecimentos())
                .isTrue();

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar acesso a todos estabelecimentos")
    void deveAtualizarAcessoATodosEstabelecimentos() {
        var usuarioEmpresa = criarUsuarioEmpresa(false);

        usuarioEmpresa.atualizar(
                new AtualizaUsuarioEmpresaRecord(
                        1L,
                        true
                )
        );

        assertThat(usuarioEmpresa.getTodosEstabelecimentos())
                .isTrue();
    }

    @Test
    @DisplayName("Deve inativar vinculo")
    void deveInativarVinculo() {
        var usuarioEmpresa = criarUsuarioEmpresa(false);

        usuarioEmpresa.inativar();

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo com auditoria")
    void deveRemoverVinculoComAuditoria() {
        var usuarioEmpresa = criarUsuarioEmpresa(false);

        usuarioEmpresa.remover(10L);

        assertThat(usuarioEmpresa.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(usuarioEmpresa.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(usuarioEmpresa.getRemovidoEm())
                .isNotNull();
    }

    private UsuarioEmpresaModel criarUsuarioEmpresa(
            Boolean todosEstabelecimentos
    ) {
        var organizacao = criarOrganizacao();

        return new UsuarioEmpresaModel(
                criarUsuarioOrganizacao(
                        criarUsuario(),
                        organizacao
                ),
                criarEmpresa(organizacao),
                todosEstabelecimentos
        );
    }

    private UsuarioOrganizacaoModel
            criarUsuarioOrganizacao(
                    UsuarioModel usuario,
                    OrganizacaoModel organizacao
            ) {
        return new UsuarioOrganizacaoModel(
                usuario,
                organizacao
        );
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

    private OrganizacaoModel criarOrganizacao() {
        return new OrganizacaoModel(
                "Organizacao Principal"
        );
    }

    private EmpresaModel criarEmpresa(
            OrganizacaoModel organizacao
    ) {
        return new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );
    }
}