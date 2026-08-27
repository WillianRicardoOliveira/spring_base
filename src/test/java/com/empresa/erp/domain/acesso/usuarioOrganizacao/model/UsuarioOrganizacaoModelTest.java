package com.empresa.erp.domain.acesso.usuarioOrganizacao.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioOrganizacaoModelTest {

    @Test
    @DisplayName("Deve criar vínculo ativo")
    void deveCriarVinculoAtivo() {
        var usuario = criarUsuario();
        var organizacao = criarOrganizacao();

        var vinculo = new UsuarioOrganizacaoModel(
                usuario,
                organizacao
        );

        assertThat(vinculo.getUsuario())
                .isSameAs(usuario);

        assertThat(vinculo.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve inativar vínculo")
    void deveInativarVinculo() {
        var vinculo = criarVinculo();

        vinculo.inativar();

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve reativar vínculo")
    void deveReativarVinculo() {
        var vinculo = criarVinculo();

        vinculo.inativar();
        vinculo.reativar();

        assertThat(vinculo.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    private UsuarioOrganizacaoModel criarVinculo() {
        return new UsuarioOrganizacaoModel(
                criarUsuario(),
                criarOrganizacao()
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
                "Organização Exemplo"
        );
    }
}