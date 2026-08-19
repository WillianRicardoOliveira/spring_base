package com.empresa.erp.domain.acesso.usuarioEmpresa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioEmpresaRepositoryTest {

    @Autowired
    private UsuarioEmpresaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private OrganizacaoRepository
            organizacaoRepository;

    private OrganizacaoModel organizacao;
    private OrganizacaoModel outraOrganizacao;
    private UsuarioModel usuario;
    private EmpresaModel empresa;
    private EmpresaModel empresaDeOutraOrganizacao;

    @BeforeEach
    void setUp() {
        organizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Organizacao Principal"
                )
        );

        outraOrganizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Outra Organizacao"
                )
        );

        usuario = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "usuario@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        empresa = empresaRepository.save(
                new EmpresaModel(
                        organizacao,
                        new EmpresaRecord(
                                "Empresa Exemplo"
                        )
                )
        );

        empresaDeOutraOrganizacao =
                empresaRepository.save(
                        new EmpresaModel(
                                outraOrganizacao,
                                new EmpresaRecord(
                                        "Empresa Externa"
                                )
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve listar somente vinculos ativos da organizacao"
    )
    void deveListarSomenteVinculosAtivosDaOrganizacao() {
        var ativo = criarVinculo(
                usuario,
                empresa,
                true
        );

        var inativo = criarVinculo(
                usuario,
                empresa,
                false
        );

        inativo.inativar();
        repository.save(inativo);

        var externo = criarVinculo(
                usuario,
                empresaDeOutraOrganizacao,
                false
        );

        var resultado = repository
                .findAllByEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(UsuarioEmpresaModel::getId)
                .containsExactly(ativo.getId())
                .doesNotContain(
                        inativo.getId(),
                        externo.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve listar vinculos por usuario somente na organizacao"
    )
    void deveListarVinculosPorUsuarioSomenteNaOrganizacao() {
        criarVinculo(
                usuario,
                empresa,
                true
        );

        criarVinculo(
                usuario,
                empresaDeOutraOrganizacao,
                false
        );

        var resultado = repository
                .findAllByUsuarioIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        usuario.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getEmpresa()
                        .getId()
        ).isEqualTo(empresa.getId());
    }

    @Test
    @DisplayName(
            "Deve listar por empresa somente na organizacao"
    )
    void deveListarPorEmpresaSomenteNaOrganizacao() {
        criarVinculo(
                usuario,
                empresa,
                true
        );

        var resultadoCorreto = repository
                .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto.getContent())
                .hasSize(1);

        assertThat(resultadoOutraOrganizacao.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve listar por usuario empresa e organizacao"
    )
    void deveListarPorUsuarioEmpresaEOrganizacao() {
        criarVinculo(
                usuario,
                empresa,
                true
        );

        var resultado = repository
                .findAllByUsuarioIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        usuario.getId(),
                        empresa.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .hasSize(1);
    }

    @Test
    @DisplayName(
            "Nao deve listar combinacao usando outra organizacao"
    )
    void naoDeveListarCombinacaoUsandoOutraOrganizacao() {
        criarVinculo(
                usuario,
                empresa,
                true
        );

        var resultado = repository
                .findAllByUsuarioIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        usuario.getId(),
                        empresa.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve buscar vinculo por id somente na organizacao"
    )
    void deveBuscarVinculoPorIdSomenteNaOrganizacao() {
        var vinculo = criarVinculo(
                usuario,
                empresa,
                true
        );

        var resultadoCorreto = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto).isPresent();

        assertThat(resultadoOutraOrganizacao)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Nao deve buscar vinculo inativo na organizacao"
    )
    void naoDeveBuscarVinculoInativoNaOrganizacao() {
        var vinculo = criarVinculo(
                usuario,
                empresa,
                true
        );

        vinculo.inativar();
        repository.save(vinculo);

        var resultado = repository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName(
            "Deve verificar vinculo ativo duplicado"
    )
    void deveVerificarVinculoAtivoDuplicado() {
        criarVinculo(
                usuario,
                empresa,
                true
        );

        boolean existe =
                repository.existsByUsuarioAndEmpresaAndStatus(
                        usuario,
                        empresa,
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Nao deve considerar vinculo inativo como duplicado"
    )
    void naoDeveConsiderarVinculoInativoComoDuplicado() {
        var vinculo = criarVinculo(
                usuario,
                empresa,
                true
        );

        vinculo.inativar();
        repository.save(vinculo);

        boolean existe =
                repository.existsByUsuarioAndEmpresaAndStatus(
                        usuario,
                        empresa,
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve manter consultas temporarias por usuario e empresa"
    )
    void deveManterConsultasTemporariasPorUsuarioEEmpresa() {
        var vinculo = criarVinculo(
                usuario,
                empresa,
                false
        );

        assertThat(
                repository.existsByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusEnum.ATIVO
                )
        ).isTrue();

        assertThat(
                repository.existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                )
        ).isTrue();

        assertThat(
                repository.findByIdAndStatus(
                        vinculo.getId(),
                        StatusEnum.ATIVO
                )
        ).isPresent();

        assertThat(
                repository
                        .findByUsuarioIdAndEmpresaIdAndStatus(
                                usuario.getId(),
                                empresa.getId(),
                                StatusEnum.ATIVO
                        )
        ).isPresent();
    }

    @Test
    @DisplayName(
            "Consultas temporarias devem ignorar vinculo inativo"
    )
    void consultasTemporariasDevemIgnorarVinculoInativo() {
        var vinculo = criarVinculo(
                usuario,
                empresa,
                false
        );

        vinculo.inativar();
        repository.save(vinculo);

        assertThat(
                repository.existsByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusEnum.ATIVO
                )
        ).isFalse();

        assertThat(
                repository.existsByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusEnum.ATIVO
                )
        ).isFalse();

        assertThat(
                repository.findByIdAndStatus(
                        vinculo.getId(),
                        StatusEnum.ATIVO
                )
        ).isEmpty();

        assertThat(
                repository
                        .findByUsuarioIdAndEmpresaIdAndStatus(
                                usuario.getId(),
                                empresa.getId(),
                                StatusEnum.ATIVO
                        )
        ).isEmpty();
    }

    private UsuarioEmpresaModel criarVinculo(
            UsuarioModel usuario,
            EmpresaModel empresa,
            Boolean todasSubsidiarias
    ) {
        return repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        todasSubsidiarias
                )
        );
    }
}