package com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.repository.SubsidiariaRepository;
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
class UsuarioSubsidiariaRepositoryTest {

    @Autowired
    private UsuarioSubsidiariaRepository repository;

    @Autowired
    private UsuarioEmpresaRepository
            usuarioEmpresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SubsidiariaRepository
            subsidiariaRepository;

    @Autowired
    private OrganizacaoRepository
            organizacaoRepository;

    private OrganizacaoModel organizacao;
    private OrganizacaoModel outraOrganizacao;
    private UsuarioModel usuario;
    private UsuarioEmpresaModel usuarioEmpresa;
    private UsuarioEmpresaModel
            usuarioEmpresaDeOutraOrganizacao;
    private SubsidiariaModel subsidiaria;
    private SubsidiariaModel
            subsidiariaDeOutraOrganizacao;

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
                                usuario,
                                empresa,
                                false
                        )
                );

        usuarioEmpresaDeOutraOrganizacao =
                usuarioEmpresaRepository.save(
                        new UsuarioEmpresaModel(
                                usuario,
                                empresaDeOutraOrganizacao,
                                false
                        )
                );

        subsidiaria = subsidiariaRepository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );

        subsidiariaDeOutraOrganizacao =
                subsidiariaRepository.save(
                        new SubsidiariaModel(
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
                subsidiaria
        );

        var outraSubsidiaria =
                subsidiariaRepository.save(
                        new SubsidiariaModel(
                                usuarioEmpresa.getEmpresa(),
                                "Filial Londrina"
                        )
                );

        var inativo = criarVinculo(
                usuarioEmpresa,
                outraSubsidiaria
        );

        inativo.inativar();
        repository.save(inativo);

        var externo = criarVinculo(
                usuarioEmpresaDeOutraOrganizacao,
                subsidiariaDeOutraOrganizacao
        );

        var resultado = repository
                .findAllByUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .extracting(
                        UsuarioSubsidiariaModel::getId
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
                subsidiaria
        );

        var resultadoCorreto = repository
                .findAllByUsuarioEmpresaIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        usuarioEmpresa.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findAllByUsuarioEmpresaIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        usuarioEmpresa.getId(),
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
            "Nao deve listar vinculo externo na organizacao atual"
    )
    void naoDeveListarVinculoExternoNaOrganizacaoAtual() {
        criarVinculo(
                usuarioEmpresaDeOutraOrganizacao,
                subsidiariaDeOutraOrganizacao
        );

        var resultado = repository
                .findAllByUsuarioEmpresaIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        PageRequest.of(0, 10),
                        usuarioEmpresaDeOutraOrganizacao.getId(),
                        organizacao.getId(),
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
                subsidiaria
        );

        boolean existe = repository
                .existsByUsuarioEmpresaAndSubsidiariaAndStatus(
                        usuarioEmpresa,
                        subsidiaria,
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
                subsidiaria
        );

        vinculo.inativar();
        repository.save(vinculo);

        boolean existe = repository
                .existsByUsuarioEmpresaAndSubsidiariaAndStatus(
                        usuarioEmpresa,
                        subsidiaria,
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
                subsidiaria
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
            "Deve verificar vinculo ativo por subsidiaria"
    )
    void deveVerificarVinculoAtivoPorSubsidiaria() {
        criarVinculo(
                usuarioEmpresa,
                subsidiaria
        );

        boolean existe = repository
                .existsBySubsidiariaIdAndStatus(
                        subsidiaria.getId(),
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
                subsidiaria
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
                repository.existsBySubsidiariaIdAndStatus(
                        subsidiaria.getId(),
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
                subsidiaria
        );

        var resultadoCorreto = repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        var resultadoOutraOrganizacao = repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
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
                subsidiaria
        );

        vinculo.inativar();
        repository.save(vinculo);

        var resultado = repository
                .findByIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                        vinculo.getId(),
                        organizacao.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado).isEmpty();
    }

    private UsuarioSubsidiariaModel criarVinculo(
            UsuarioEmpresaModel usuarioEmpresa,
            SubsidiariaModel subsidiaria
    ) {
        return repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );
    }
}