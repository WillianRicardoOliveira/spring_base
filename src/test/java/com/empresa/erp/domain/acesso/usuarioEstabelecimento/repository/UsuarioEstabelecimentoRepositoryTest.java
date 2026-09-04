package com.empresa.erp.domain.acesso.usuarioEstabelecimento.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.repository.UsuarioOrganizacaoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.repository.EstabelecimentoRepository;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.repository.OrganizacaoRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioEstabelecimentoRepositoryTest {

    @Autowired
    private UsuarioEstabelecimentoRepository repository;

    @Autowired
    private UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    @Autowired
    private UsuarioOrganizacaoRepository
            usuarioOrganizacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EstabelecimentoRepository
            estabelecimentoRepository;

    @Autowired
    private OrganizacaoRepository
            organizacaoRepository;

    private OrganizacaoModel organizacao;
    private OrganizacaoModel outraOrganizacao;
    private UsuarioModel usuario;
    private UsuarioOrganizacaoModel usuarioOrganizacao;
    private UsuarioOrganizacaoModel
            usuarioOrganizacaoDeOutraOrganizacao;
    private UsuarioEmpresaModel usuarioEmpresa;
    private UsuarioEmpresaModel
            usuarioEmpresaDeOutraOrganizacao;
    private EstabelecimentoModel estabelecimento;
    private EstabelecimentoModel
            estabelecimentoDeOutraOrganizacao;

    @BeforeEach
    void setUp() {
        organizacao = organizacaoRepository.save(
                new OrganizacaoModel(
                        "Organizacao Principal"
                )
        );

        outraOrganizacao =
                organizacaoRepository.save(
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

        var empresa = empresaRepository.save(
                new EmpresaModel(
                        organizacao,
                        new EmpresaRecord(
                                "Empresa Exemplo"
                        )
                )
        );

        var empresaDeOutraOrganizacao =
                empresaRepository.save(
                        new EmpresaModel(
                                outraOrganizacao,
                                new EmpresaRecord(
                                        "Empresa Externa"
                                )
                        )
                );

        usuarioEmpresa =
                usuarioEmpresaRepository.save(
                        new UsuarioEmpresaModel(
                                usuarioOrganizacao,
                                empresa,
                                false
                        )
                );

        usuarioEmpresaDeOutraOrganizacao =
                usuarioEmpresaRepository.save(
                        new UsuarioEmpresaModel(
                                usuarioOrganizacaoDeOutraOrganizacao,
                                empresaDeOutraOrganizacao,
                                false
                        )
                );

        estabelecimento = estabelecimentoRepository.save(
                new EstabelecimentoModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        estabelecimentoDeOutraOrganizacao =
                estabelecimentoRepository.save(
                        new EstabelecimentoModel(
                                empresaDeOutraOrganizacao,
                                "Filial Externa"
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve listar somente vinculos ativos da organizacao"
    )
    void deveListarSomenteVinculosAtivosDaOrganizacao() {
        var ativo = criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        var outroEstabelecimento =
                estabelecimentoRepository.save(
                        new EstabelecimentoModel(
                                usuarioEmpresa.getEmpresa(),
                                "Filial Londrina"
                        )
                );

        var inativo = criarVinculo(
                usuarioEmpresa,
                outroEstabelecimento
        );

        inativo.inativar();
        repository.save(inativo);

        var externo = criarVinculo(
                usuarioEmpresaDeOutraOrganizacao,
                estabelecimentoDeOutraOrganizacao
        );

        var resultado = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        null,
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(
                        UsuarioEstabelecimentoModel::getId
                )
                .containsExactly(ativo.getId())
                .doesNotContain(
                        inativo.getId(),
                        externo.getId()
                );
    }

    @Test
    @DisplayName(
            "Deve listar por usuario empresa somente na organizacao"
    )
    void deveListarPorUsuarioEmpresaSomenteNaOrganizacao() {
        criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        var resultadoCorreto = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        outraOrganizacao.getId(),
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto.getContent())
                .hasSize(1);

        assertThat(resultadoOutraOrganizacao.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Nao deve listar vinculo externo na organizacao atual"
    )
    void naoDeveListarVinculoExternoNaOrganizacaoAtual() {
        criarVinculo(
                usuarioEmpresaDeOutraOrganizacao,
                estabelecimentoDeOutraOrganizacao
        );

        var resultado = repository
                .buscarAtivosDaOrganizacao(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        usuarioEmpresaDeOutraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName("Deve verificar vinculo duplicado")
    void deveVerificarVinculoDuplicado() {
        criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        boolean existe = repository
                .existsByUsuarioEmpresaAndEstabelecimentoAndStatus(
                        usuarioEmpresa,
                        estabelecimento,
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
                usuarioEmpresa,
                estabelecimento
        );

        vinculo.inativar();
        repository.save(vinculo);

        boolean existe = repository
                .existsByUsuarioEmpresaAndEstabelecimentoAndStatus(
                        usuarioEmpresa,
                        estabelecimento,
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName(
            "Deve verificar vinculo ativo por usuario empresa"
    )
    void deveVerificarVinculoAtivoPorUsuarioEmpresa() {
        criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        boolean existe = repository
                .existsByUsuarioEmpresaIdAndStatus(
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Deve verificar vinculo ativo por estabelecimento"
    )
    void deveVerificarVinculoAtivoPorEstabelecimento() {
        criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        boolean existe = repository
                .existsByEstabelecimentoIdAndStatus(
                        estabelecimento.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName(
            "Metodos exists devem ignorar vinculo inativo"
    )
    void metodosExistsDevemIgnorarVinculoInativo() {
        var vinculo = criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        vinculo.inativar();
        repository.save(vinculo);

        assertThat(
                repository.existsByUsuarioEmpresaIdAndStatus(
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                )
        ).isFalse();

        assertThat(
                repository.existsByEstabelecimentoIdAndStatus(
                        estabelecimento.getId(),
                        StatusEnum.ATIVO
                )
        ).isFalse();
    }

    @Test
    @DisplayName(
            "Deve buscar vinculo por id somente na organizacao"
    )
    void deveBuscarVinculoPorIdSomenteNaOrganizacao() {
        var vinculo = criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        var resultadoCorreto = repository
                .buscarAtivoDaOrganizacaoPorId(
                        vinculo.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .buscarAtivoDaOrganizacaoPorId(
                        vinculo.getId(),
                        outraOrganizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultadoCorreto).isPresent();

        assertThat(resultadoCorreto.get().getId())
                .isEqualTo(vinculo.getId());

        assertThat(resultadoOutraOrganizacao)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Nao deve buscar vinculo inativo como ativo"
    )
    void naoDeveBuscarVinculoInativoComoAtivo() {
        var vinculo = criarVinculo(
                usuarioEmpresa,
                estabelecimento
        );

        vinculo.inativar();
        repository.save(vinculo);

        var resultado = repository
                .buscarAtivoDaOrganizacaoPorId(
                        vinculo.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado).isEmpty();
    }

    private UsuarioEstabelecimentoModel criarVinculo(
            UsuarioEmpresaModel usuarioEmpresa,
            EstabelecimentoModel estabelecimento
    ) {
        return repository.save(
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        estabelecimento
                )
        );
    }
}