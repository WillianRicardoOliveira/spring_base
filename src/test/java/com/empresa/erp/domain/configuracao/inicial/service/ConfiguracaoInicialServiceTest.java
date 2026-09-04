package com.empresa.erp.domain.configuracao.inicial.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.inicial.model.ProximaEtapaConfiguracaoEnum;

@ExtendWith(MockitoExtension.class)
class ConfiguracaoInicialServiceTest {

    private static final Long ID_ORGANIZACAO =
            1L;

    @Mock
    private EmpresaRepository
            empresaRepository;

    @Mock
    private ContextoOrganizacao
            contextoOrganizacao;

    @InjectMocks
    private ConfiguracaoInicialService
            service;

    @BeforeEach
    void setUp() {
        when(
                contextoOrganizacao
                        .getIdOrganizacao()
        ).thenReturn(ID_ORGANIZACAO);
    }

    @Test
    @DisplayName(
            "Deve indicar cadastro de empresa como proxima etapa"
    )
    void deveIndicarCadastroDeEmpresaComoProximaEtapa() {

        when(
                empresaRepository
                        .existsByOrganizacaoIdAndStatus(
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(false);

        var resultado =
                service.consultar();

        assertThat(
                resultado.empresaCadastrada()
        ).isFalse();

        assertThat(
                resultado.proximaEtapa()
        ).isEqualTo(
                ProximaEtapaConfiguracaoEnum
                        .EMPRESA
        );

        verify(
                empresaRepository
        ).existsByOrganizacaoIdAndStatus(
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        );
    }

    @Test
    @DisplayName(
            "Nao deve indicar proxima etapa quando empresa ja existe"
    )
    void naoDeveIndicarProximaEtapaQuandoEmpresaJaExiste() {

        when(
                empresaRepository
                        .existsByOrganizacaoIdAndStatus(
                                ID_ORGANIZACAO,
                                StatusEnum.ATIVO
                        )
        ).thenReturn(true);

        var resultado =
                service.consultar();

        assertThat(
                resultado.empresaCadastrada()
        ).isTrue();

        assertThat(
                resultado.proximaEtapa()
        ).isNull();

        verify(
                empresaRepository
        ).existsByOrganizacaoIdAndStatus(
                ID_ORGANIZACAO,
                StatusEnum.ATIVO
        );
    }
}