package com.empresa.erp.domain.acesso.usuarioPerfil.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.repository.PerfilPermissaoRepository;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.repository.PermissaoRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UsuarioPerfilRepositoryTest {

    @Autowired
    private UsuarioPerfilRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private PerfilPermissaoRepository perfilPermissaoRepository;

    @Test
    @DisplayName("Deve listar vinculos ativos por usuario")
    void deveListarVinculosAtivosPorUsuario() {
        var usuario = usuarioRepository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var perfilAtivo = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var perfilRemovido = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Financeiro", "Perfil financeiro")
        ));

        var vinculoAtivo = repository.save(new UsuarioPerfilModel(usuario, perfilAtivo));

        var vinculoRemovido = repository.save(new UsuarioPerfilModel(usuario, perfilRemovido));
        vinculoRemovido.remover(10L);
        repository.save(vinculoRemovido);

        var resultado = repository.findAllByUsuarioIdAndStatus(usuario.getId(), StatusEnum.ATIVO);

        assertThat(resultado)
                .extracting(UsuarioPerfilModel::getId)
                .contains(vinculoAtivo.getId())
                .doesNotContain(vinculoRemovido.getId());
    }

    @Test
    @DisplayName("Deve verificar existencia por usuario perfil e status")
    void deveVerificarExistenciaPorUsuarioPerfilEStatus() {
        var usuario = usuarioRepository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var perfil = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        repository.save(new UsuarioPerfilModel(usuario, perfil));

        var existeAtivo = repository.existsByUsuarioAndPerfilAndStatus(
                usuario,
                perfil,
                StatusEnum.ATIVO
        );

        var naoExisteRemovido = repository.existsByUsuarioAndPerfilAndStatus(
                usuario,
                perfil,
                StatusEnum.REMOVIDO
        );

        assertThat(existeAtivo).isTrue();
        assertThat(naoExisteRemovido).isFalse();
    }

    @Test
    @DisplayName("Deve buscar vinculo por usuario perfil e status")
    void deveBuscarVinculoPorUsuarioPerfilEStatus() {
        var usuario = usuarioRepository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var perfil = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        repository.save(new UsuarioPerfilModel(usuario, perfil));

        var resultado = repository.findByUsuarioAndPerfilAndStatus(
                usuario,
                perfil,
                StatusEnum.ATIVO
        );

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuario()).isEqualTo(usuario);
        assertThat(resultado.get().getPerfil()).isEqualTo(perfil);
    }

    @Test
    @DisplayName("Deve buscar chaves de permissoes ativas por usuario")
    void deveBuscarChavesDePermissoesAtivasPorUsuario() {
        var usuario = usuarioRepository.save(new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        ));

        var perfil = perfilRepository.save(new PerfilModel(
                new PerfilRecord("Administrador", "Perfil administrador")
        ));

        var permissao = permissaoRepository.save(new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        )));

        repository.save(new UsuarioPerfilModel(usuario, perfil));
        perfilPermissaoRepository.save(new PerfilPermissaoModel(perfil, permissao));

        var resultado = repository.buscarChavesPermissoesAtivasPorUsuario(
                usuario.getId(),
                StatusEnum.ATIVO
        );

        assertThat(resultado).containsExactly("ACESSO_USUARIO_LISTAR");
    }
}