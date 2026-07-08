package com.empresa.erp.domain.acesso.perfilPermissao.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.old.StatusEnum;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PerfilPermissaoRepositoryTest {

    @Autowired
    private PerfilPermissaoRepository repository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Test
    @DisplayName("Deve listar vinculos ativos por perfil")
    void deveListarVinculosAtivosPorPerfil() {
        var perfil = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var permissaoAtiva = permissaoRepository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        var permissaoRemovida = permissaoRepository.save(new PermissaoModel(new PermissaoRecord(
                "Editar usuarios",
                "ACESSO_USUARIO_EDITAR",
                "Permite editar usuarios"
        )));

        var vinculoAtivo = repository.save(new PerfilPermissaoModel(perfil, permissaoAtiva));

        var vinculoRemovido = repository.save(new PerfilPermissaoModel(perfil, permissaoRemovida));
        vinculoRemovido.remover(10L);
        repository.save(vinculoRemovido);

        var resultado = repository.findAllByPerfilIdAndStatus(
                PageRequest.of(0, 10),
                perfil.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado.getContent())
                .extracting(PerfilPermissaoModel::getId)
                .contains(vinculoAtivo.getId())
                .doesNotContain(vinculoRemovido.getId());
    }

    @Test
    @DisplayName("Deve verificar existencia por perfil permissao e status")
    void deveVerificarExistenciaPorPerfilPermissaoEStatus() {
        var perfil = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var permissao = permissaoRepository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        repository.save(new PerfilPermissaoModel(perfil, permissao));

        var existeAtivo = repository.existsByPerfilAndPermissaoAndStatus(
                perfil,
                permissao,
                StatusEnum.ATIVO
        );

        var naoExisteRemovido = repository.existsByPerfilAndPermissaoAndStatus(
                perfil,
                permissao,
                StatusEnum.REMOVIDO
        );

        assertThat(existeAtivo).isTrue();
        assertThat(naoExisteRemovido).isFalse();
    }

    @Test
    @DisplayName("Deve buscar vinculo por perfil permissao e status")
    void deveBuscarVinculoPorPerfilPermissaoEStatus() {
        var perfil = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var permissao = permissaoRepository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        repository.save(new PerfilPermissaoModel(perfil, permissao));

        var resultado = repository.findByPerfilAndPermissaoAndStatus(
                perfil,
                permissao,
                StatusEnum.ATIVO
        );

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPerfil()).isEqualTo(perfil);
        assertThat(resultado.get().getPermissao()).isEqualTo(permissao);
    }
}