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

    private UsuarioModel usuario;
    private EmpresaModel empresa;

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
    }

    @Test
    @DisplayName("Deve listar somente vinculos ativos")
    void deveListarSomenteVinculosAtivos() {
        var ativo = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        var outraEmpresa = empresaRepository.save(
                new EmpresaModel(
                        new EmpresaRecord("Outra Empresa")
                )
        );

        var inativo = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        outraEmpresa,
                        false
                )
        );

        inativo.inativar();
        repository.save(inativo);

        var resultado = repository.findAllByStatus(
                PageRequest.of(0, 10),
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent())
                .extracting(UsuarioEmpresaModel::getId)
                .contains(ativo.getId())
                .doesNotContain(inativo.getId());
    }

    @Test
    @DisplayName("Deve listar vinculos por usuario")
    void deveListarVinculosPorUsuario() {
        var outroUsuario = usuarioRepository.save(
                new UsuarioModel(
                        new UsuarioRecord(
                                "outro@teste.com",
                                "123456"
                        ),
                        "senha-criptografada"
                )
        );

        repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        repository.save(
                new UsuarioEmpresaModel(
                        outroUsuario,
                        empresa,
                        false
                )
        );

        var resultado =
                repository.findAllByUsuarioIdAndStatus(
                        PageRequest.of(0, 10),
                        usuario.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent()).hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getUsuario()
                        .getId()
        ).isEqualTo(usuario.getId());
    }

    @Test
    @DisplayName("Deve listar vinculos por empresa")
    void deveListarVinculosPorEmpresa() {
        var outraEmpresa = empresaRepository.save(
                new EmpresaModel(
                        new EmpresaRecord("Outra Empresa")
                )
        );

        repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        outraEmpresa,
                        false
                )
        );

        var resultado =
                repository.findAllByEmpresaIdAndStatus(
                        PageRequest.of(0, 10),
                        empresa.getId(),
                        StatusEnum.ATIVO
                );

        assertThat(resultado.getContent()).hasSize(1);

        assertThat(
                resultado.getContent()
                        .get(0)
                        .getEmpresa()
                        .getId()
        ).isEqualTo(empresa.getId());
    }

    @Test
    @DisplayName("Deve listar por usuario e empresa")
    void deveListarPorUsuarioEEmpresa() {
        repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        var resultado =
                repository
                        .findAllByUsuarioIdAndEmpresaIdAndStatus(
                                PageRequest.of(0, 10),
                                usuario.getId(),
                                empresa.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve verificar vinculo ativo duplicado")
    void deveVerificarVinculoAtivoDuplicado() {
        repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
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
            "Nao deve considerar vinculo inativo como ativo"
    )
    void naoDeveConsiderarVinculoInativoComoAtivo() {
        var usuarioEmpresa = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        usuarioEmpresa.inativar();
        repository.save(usuarioEmpresa);

        boolean existe =
                repository.existsByUsuarioAndEmpresaAndStatus(
                        usuario,
                        empresa,
                        StatusEnum.ATIVO
                );

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve buscar vinculo ativo por id")
    void deveBuscarVinculoAtivoPorId() {
        var usuarioEmpresa = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        var resultado = repository.findByIdAndStatus(
                usuarioEmpresa.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(usuarioEmpresa.getId());
    }

    @Test
    @DisplayName("Nao deve buscar vinculo inativo por id")
    void naoDeveBuscarVinculoInativoPorId() {
        var usuarioEmpresa = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        true
                )
        );

        usuarioEmpresa.inativar();
        repository.save(usuarioEmpresa);

        var resultado = repository.findByIdAndStatus(
                usuarioEmpresa.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName(
            "Deve buscar vinculo por usuario e empresa"
    )
    void deveBuscarVinculoPorUsuarioEEmpresa() {
        var usuarioEmpresa = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        false
                )
        );

        var resultado =
                repository
                        .findByUsuarioIdAndEmpresaIdAndStatus(
                                usuario.getId(),
                                empresa.getId(),
                                StatusEnum.ATIVO
                        );

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getId())
                .isEqualTo(usuarioEmpresa.getId());
    }

    @Test
    @DisplayName(
            "Deve verificar vinculo ativo por usuario e empresa"
    )
    void deveVerificarVinculoAtivoPorUsuarioEEmpresa() {
        var usuarioEmpresa = repository.save(
                new UsuarioEmpresaModel(
                        usuario,
                        empresa,
                        false
                )
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

        usuarioEmpresa.inativar();
        repository.save(usuarioEmpresa);

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
    }
}