package com.empresa.erp.domain.organizacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

@ExtendWith(MockitoExtension.class)
class OrganizacaoDisponivelServiceTest {

    @Mock
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Mock
    private UsuarioLogadoService
            usuarioLogadoService;

    @InjectMocks
    private OrganizacaoDisponivelService service;

    @Test
    @DisplayName("Deve listar organizações disponíveis do usuário")
    void deveListarOrganizacoesDisponiveisDoUsuario() {
        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findAllByUsuarioIdAndStatusAndOrganizacaoStatusOrderByOrganizacaoNomeAsc(
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(
                List.of(
                        criarVinculo(
                                20L,
                                "Organização A"
                        ),
                        criarVinculo(
                                30L,
                                "Organização B"
                        )
                )
        );

        var resultado = service.listar();

        assertThat(resultado).hasSize(2);

        assertThat(resultado)
                .extracting(
                        organizacao ->
                                organizacao.nome()
                )
                .containsExactly(
                        "Organização A",
                        "Organização B"
                );

        assertThat(resultado)
                .extracting(
                        organizacao ->
                                organizacao.id()
                )
                .containsExactly(
                        20L,
                        30L
                );

        verify(usuarioLogadoService).getId();

        verify(usuarioOrganizacaoRepository)
                .findAllByUsuarioIdAndStatusAndOrganizacaoStatusOrderByOrganizacaoNomeAsc(
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                );
    }

    @Test
    @DisplayName("Deve retornar lista vazia sem organizações disponíveis")
    void deveRetornarListaVaziaSemOrganizacoesDisponiveis() {
        when(usuarioLogadoService.getId())
                .thenReturn(10L);

        when(usuarioOrganizacaoRepository
                .findAllByUsuarioIdAndStatusAndOrganizacaoStatusOrderByOrganizacaoNomeAsc(
                        10L,
                        StatusEnum.ATIVO,
                        StatusEnum.ATIVO
                )
        ).thenReturn(List.of());

        var resultado = service.listar();

        assertThat(resultado).isEmpty();
    }

    private UsuarioOrganizacaoModel criarVinculo(
            Long idOrganizacao,
            String nomeOrganizacao
    ) {
        var usuario = new UsuarioModel(
                new UsuarioRecord(
                        "usuario@teste.com",
                        "123456"
                ),
                "senha-criptografada"
        );

        var organizacao = new OrganizacaoModel(
                nomeOrganizacao
        );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                idOrganizacao
        );

        return new UsuarioOrganizacaoModel(
                usuario,
                organizacao
        );
    }
}