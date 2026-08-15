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

    private UsuarioModel usuario;
    private EmpresaModel empresa;
    private UsuarioEmpresaModel usuarioEmpresa;
    private SubsidiariaModel subsidiaria;

    @BeforeEach
    void setUp() {
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
                        new EmpresaRecord("Empresa Exemplo")
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

        subsidiaria = subsidiariaRepository.save(
                new SubsidiariaModel(
                        empresa,
                        "Filial Curitiba"
                )
        );
    }

    @Test
    @DisplayName("Deve listar somente vinculos ativos")
    void deveListarSomenteVinculosAtivos() {
        var ativo = repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );

        var outraSubsidiaria =
                subsidiariaRepository.save(
                        new SubsidiariaModel(
                                empresa,
                                "Filial Londrina"
                        )
                );

        var inativo = repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        outraSubsidiaria
                )
        );

        inativo.inativar();
        repository.save(inativo);

        var resultado = repository.findAllByStatus(
                PageRequest.of(0, 10),
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent())
                .extracting(UsuarioSubsidiariaModel::getId)
                .contains(ativo.getId())
                .doesNotContain(inativo.getId());
    }

    @Test
    @DisplayName(
            "Deve listar vinculos por usuario empresa"
    )
    void deveListarVinculosPorUsuarioEmpresa() {
        repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );

        var resultado = repository
                .findAllByUsuarioEmpresaIdAndStatus(
                        PageRequest.of(0, 10),
                        usuarioEmpresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getUsuarioEmpresa()
                        .getId()
        ).isEqualTo(usuarioEmpresa.getId());
    }

    @Test
    @DisplayName("Deve verificar vinculo duplicado")
    void deveVerificarVinculoDuplicado() {
        repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
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
        var usuarioSubsidiaria = repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );

        usuarioSubsidiaria.inativar();
        repository.save(usuarioSubsidiaria);

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
        repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
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
        repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );

        boolean existe = repository
                .existsBySubsidiariaIdAndStatus(
                        subsidiaria.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve buscar vinculo ativo por id")
    void deveBuscarVinculoAtivoPorId() {
        var usuarioSubsidiaria = repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );

        var resultado = repository.findByIdAndStatus(
                usuarioSubsidiaria.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(usuarioSubsidiaria.getId());
    }

    @Test
    @DisplayName(
            "Nao deve buscar vinculo inativo como ativo"
    )
    void naoDeveBuscarVinculoInativoComoAtivo() {
        var usuarioSubsidiaria = repository.save(
                new UsuarioSubsidiariaModel(
                        usuarioEmpresa,
                        subsidiaria
                )
        );

        usuarioSubsidiaria.inativar();
        repository.save(usuarioSubsidiaria);

        var resultado = repository.findByIdAndStatus(
                usuarioSubsidiaria.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isEmpty();
    }
}