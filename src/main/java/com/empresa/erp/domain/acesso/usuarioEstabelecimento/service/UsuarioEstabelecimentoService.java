package com.empresa.erp.domain.acesso.usuarioEstabelecimento.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.repository.UsuarioEmpresaRepository;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.DetalheUsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.ListaUsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.UsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.repository.UsuarioEstabelecimentoRepository;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.repository.EstabelecimentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioEstabelecimentoService {

    private final UsuarioEstabelecimentoRepository repository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioLogadoService usuarioLogadoService;
    private final ContextoOrganizacao contextoOrganizacao;

    @Transactional
    public UsuarioEstabelecimentoModel cadastrar(
            UsuarioEstabelecimentoRecord dados
    ) {
        Long idOrganizacao = contextoOrganizacao.getIdOrganizacao();

        UsuarioEmpresaModel usuarioEmpresa =
                buscarUsuarioEmpresaAtivo(
                        dados.idUsuarioEmpresa(),
                        idOrganizacao
                );

        if (Boolean.TRUE.equals(
                usuarioEmpresa.getTodosEstabelecimentos()
        )) {
            throw new ValidacaoException(
                    "O usuario possui acesso a todos os estabelecimentos desta empresa."
            );
        }

        EstabelecimentoModel estabelecimento =
                buscarEstabelecimentoAtivo(
                        dados.idEstabelecimento(),
                        idOrganizacao
                );

        validarEmpresa(usuarioEmpresa, estabelecimento);

        if (repository.existsByUsuarioEmpresaAndEstabelecimentoAndStatus(
                usuarioEmpresa,
                estabelecimento,
                StatusEnum.ATIVO
        )) {
            throw new ValidacaoException(
                    "Usuario ja vinculado a este estabelecimento."
            );
        }

        UsuarioEstabelecimentoModel usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        estabelecimento
                );

        return repository.save(usuarioEstabelecimento);
    }

    @Transactional(readOnly = true)
    public Page<ListaUsuarioEstabelecimentoRecord> listar(
            Pageable paginacao,
            Long idUsuarioEmpresa
    ) {
        Long idOrganizacao = contextoOrganizacao.getIdOrganizacao();

        Long idUsuarioEmpresaValidado = null;

        if (idUsuarioEmpresa != null) {
            UsuarioEmpresaModel usuarioEmpresa =
                    buscarUsuarioEmpresaAtivo(
                            idUsuarioEmpresa,
                            idOrganizacao
                    );

            idUsuarioEmpresaValidado = usuarioEmpresa.getId();
        }

        return repository
                .buscarAtivosDaOrganizacao(
                        paginacao,
                        idOrganizacao,
                        idUsuarioEmpresaValidado,
                        StatusEnum.ATIVO
                )
                .map(ListaUsuarioEstabelecimentoRecord::new);
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioEstabelecimentoRecord detalhar(Long id) {
        return new DetalheUsuarioEstabelecimentoRecord(
                buscarVinculoAtivo(id)
        );
    }

    @Transactional
    public void excluir(Long id) {
        UsuarioEstabelecimentoModel usuarioEstabelecimento =
                buscarVinculoAtivo(id);

        usuarioEstabelecimento.remover(usuarioLogadoService.getId());
    }

    private void validarEmpresa(
            UsuarioEmpresaModel usuarioEmpresa,
            EstabelecimentoModel estabelecimento
    ) {
        Long idEmpresaDoVinculo =
                usuarioEmpresa.getEmpresa().getId();

        Long idEmpresaDoEstabelecimento =
                estabelecimento.getEmpresa().getId();

        if (!Objects.equals(
                idEmpresaDoVinculo,
                idEmpresaDoEstabelecimento
        )) {
            throw new ValidacaoException(
                    "O estabelecimento nao pertence a empresa vinculada ao usuario."
            );
        }
    }

    private UsuarioEmpresaModel buscarUsuarioEmpresaAtivo(
            Long idUsuarioEmpresa,
            Long idOrganizacao
    ) {
        return usuarioEmpresaRepository
                .findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                        idUsuarioEmpresa,
                        idOrganizacao,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo entre usuario e empresa nao encontrado ou removido."
                        )
                );
    }

    private EstabelecimentoModel buscarEstabelecimentoAtivo(
            Long idEstabelecimento,
            Long idOrganizacao
    ) {
        return estabelecimentoRepository
                .findByIdAndEmpresaOrganizacaoIdAndStatus(
                        idEstabelecimento,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Estabelecimento nao encontrado ou removido."
                        )
                );
    }

    private UsuarioEstabelecimentoModel buscarVinculoAtivo(Long id) {
        Long idOrganizacao = contextoOrganizacao.getIdOrganizacao();

        return repository
                .buscarAtivoDaOrganizacaoPorId(
                        id,
                        idOrganizacao,
                        StatusEnum.ATIVO
                )
                .orElseThrow(() ->
                        new ValidacaoException(
                                "Vinculo entre usuario e estabelecimento nao encontrado ou removido."
                        )
                );
    }
}