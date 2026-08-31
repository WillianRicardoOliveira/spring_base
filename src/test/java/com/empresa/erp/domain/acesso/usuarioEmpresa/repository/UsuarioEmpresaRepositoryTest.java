package com.empresa.erp.domain.acesso.usuarioEmpresa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
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
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

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
    private UsuarioOrganizacaoModel usuarioOrganizacao;
    private UsuarioOrganizacaoModel
            usuarioOrganizacaoDeOutraOrganizacao;
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

        usuarioOrganizacao =
                usuarioOrganizacaoRepository.save(
                        new UsuarioOrganizacaoModel(
                                usuario,
                                organizacao
                        )
                );

        usuarioOrganizacaoDeOutraOrganizacao =
                usuarioOrganizacaoRepository.save(
                        new UsuarioOrganizacaoModel(
                                usuario,
                                outraOrganizacao
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
                usuarioOrganizacao,
                empresa,
                true
        );

        var inativo = criarVinculo(
                usuarioOrganizacao,
                empresa,
                false
        );

        inativo.inativar();
        repository.save(inativo);

        var externo = criarVinculo(
                usuarioOrganizacaoDeOutraOrganizacao,
                empresaDeOutraOrganizacao,
                false
        );

        var resultado = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        null,
                        null,
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
                usuarioOrganizacao,
                empresa,
                true
        );

        criarVinculo(
                usuarioOrganizacaoDeOutraOrganizacao,
                empresaDeOutraOrganizacao,
                false
        );

        var resultado = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        usuarioOrganizacao.getId(),
                        null,
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
                usuarioOrganizacao,
                empresa,
                true
        );

        var resultadoCorreto = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        null,
                        empresa.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        outraOrganizacao.getId(),
                        null,
                        empresa.getId(),
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
                usuarioOrganizacao,
                empresa,
                true
        );

        var resultado = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        usuarioOrganizacao.getId(),
                        empresa.getId(),
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
                usuarioOrganizacao,
                empresa,
                true
        );

        var resultado = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        outraOrganizacao.getId(),
                        usuarioOrganizacao.getId(),
                        empresa.getId(),
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
                usuarioOrganizacao,
                empresa,
                true
        );

        var resultadoCorreto = repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        organizacao.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        outraOrganizacao.getId(),
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
                usuarioOrganizacao,
                empresa,
                true
        );

        vinculo.inativar();
        repository.save(vinculo);

        var resultado = repository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        organizacao.getId(),
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
                usuarioOrganizacao,
                empresa,
                true
        );

        boolean existe =
                repository
                        .existsByUsuarioOrganizacaoIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                                usuarioOrganizacao.getId(),
                                empresa.getId(),
                                organizacao.getId(),
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
                usuarioOrganizacao,
                empresa,
                true
        );

        vinculo.inativar();
        repository.save(vinculo);

        boolean existe =
                repository
                        .existsByUsuarioOrganizacaoIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                                usuarioOrganizacao.getId(),
                                empresa.getId(),
                                organizacao.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(existe).isFalse();
    }

    private UsuarioEmpresaModel criarVinculo(
            UsuarioOrganizacaoModel usuarioOrganizacao,
            EmpresaModel empresa,
            Boolean todasSubsidiarias
    ) {
        return repository.save(
                new UsuarioEmpresaModel(
                        usuarioOrganizacao,
                        empresa,
                        todasSubsidiarias
                )
        );
    }
}