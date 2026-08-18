package com.empresa.erp.domain.organizacao.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class OrganizacaoDisponivelRecordTest {

    @Test
    @DisplayName("Deve criar resposta da organização disponível")
    void deveCriarRespostaDaOrganizacaoDisponivel() {
        var organizacao = new OrganizacaoModel(
                "Organização Exemplo"
        );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                20L
        );

        var vinculo = new UsuarioOrganizacaoModel(
                criarUsuario(),
                organizacao
        );

        var resultado =
                new OrganizacaoDisponivelRecord(
                        vinculo
                );

        assertThat(resultado.id())
                .isEqualTo(20L);

        assertThat(resultado.nome())
                .isEqualTo("Organização Exemplo");
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
}