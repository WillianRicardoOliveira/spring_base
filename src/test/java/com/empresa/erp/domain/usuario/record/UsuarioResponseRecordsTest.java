package com.empresa.erp.domain.usuario.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

class UsuarioResponseRecordsTest {

    @Test
    @DisplayName("Deve criar DetalheUsuarioRecord a partir do vinculo da organizacao")
    void deveCriarDetalheUsuarioRecordAPartirDoVinculoDaOrganizacao() {
        var vinculo =
                criarVinculoUsuarioOrganizacao(
                        1L,
                        "usuario@teste.com"
                );

        var detalhe =
                new DetalheUsuarioRecord(vinculo);

        assertThat(detalhe.id())
                .isEqualTo(1L);

        assertThat(detalhe.email())
                .isEqualTo("usuario@teste.com");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
        assertThat(detalhe.auditoria())
        	.isNotNull();
    }

    @Test
    @DisplayName("Deve criar ListaUsuarioRecord a partir do vinculo da organizacao")
    void deveCriarListaUsuarioRecordAPartirDoVinculoDaOrganizacao() {
        var vinculo =
                criarVinculoUsuarioOrganizacao(
                        2L,
                        "financeiro@teste.com"
                );

        var lista =
                new ListaUsuarioRecord(vinculo);

        assertThat(lista.id())
                .isEqualTo(2L);

        assertThat(lista.email())
                .isEqualTo("financeiro@teste.com");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo do vinculo no record de detalhe")
    void deveRefletirStatusInativoDoVinculoNoRecordDeDetalhe() {
        var vinculo =
                criarVinculoUsuarioOrganizacao(
                        1L,
                        "usuario@teste.com"
                );

        vinculo.inativar();

        var detalhe =
                new DetalheUsuarioRecord(vinculo);

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private UsuarioOrganizacaoModel criarVinculoUsuarioOrganizacao(
            Long idUsuario,
            String email
    ) {
        var usuario =
                criarUsuario(
                        idUsuario,
                        email
                );

        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        return new UsuarioOrganizacaoModel(
                usuario,
                organizacao
        );
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email
    ) {
        var usuario =
                new UsuarioModel(
                        id,
                        email,
                        "senha-criptografada",
                        StatusEnum.ATIVO
                );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                id
        );

        return usuario;
    }
}