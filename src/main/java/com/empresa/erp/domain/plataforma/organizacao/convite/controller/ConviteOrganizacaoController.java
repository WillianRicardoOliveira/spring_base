package com.empresa.erp.domain.plataforma.organizacao.convite.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoNovoUsuarioRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoUsuarioExistenteRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.DetalheConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ListaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ResultadoAceiteConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.TokenConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.service.ConviteOrganizacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(
        "/plataforma/organizacao/convite"
)
@RequiredArgsConstructor
public class ConviteOrganizacaoController {

    private final ConviteOrganizacaoService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_CRIAR')"
    )
    public ResponseEntity<DetalheConviteOrganizacaoRecord>
            convidar(
                    @RequestBody
                    @Valid
                    ConviteOrganizacaoRecord dados,

                    UriComponentsBuilder uriBuilder
            ) {
        DetalheConviteOrganizacaoRecord convite =
                service.convidar(dados);

        var uri = uriBuilder
                .path(
                        "/plataforma/organizacao/"
                                + "convite/{id}"
                )
                .buildAndExpand(convite.id())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(convite);
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_LISTAR')"
    )
    public ResponseEntity<Page<ListaConviteOrganizacaoRecord>>
            listar(
                    @PageableDefault(
                            page = 0,
                            size = 10,
                            sort = "id",
                            direction = Sort.Direction.DESC
                    )
                    Pageable paginacao,

                    String filtro,

                    StatusConviteOrganizacaoEnum status
            ) {
        return ResponseEntity.ok(
                service.listar(
                        paginacao,
                        filtro,
                        status
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_DETALHAR')"
    )
    public ResponseEntity<DetalheConviteOrganizacaoRecord>
            detalhar(
                    @PathVariable
                    Long id
            ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_CRIAR')"
    )
    public ResponseEntity<Void> revogar(
            @PathVariable
            Long id
    ) {
        service.revogar(id);

        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/reenvio")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_CRIAR')"
    )
    public ResponseEntity<DetalheConviteOrganizacaoRecord>
            reenviar(
                    @PathVariable
                    Long id
            ) {
        return ResponseEntity.ok(
                service.reenviar(id)
        );
    }

    @PostMapping("/consulta")
    public ResponseEntity<ConsultaConviteOrganizacaoRecord>
            consultar(
                    @RequestBody
                    @Valid
                    TokenConsultaConviteOrganizacaoRecord dados
            ) {
        return ResponseEntity.ok(
                service.consultar(
                        dados.token()
                )
        );
    }

    @PostMapping("/aceite/usuario-existente")
    public ResponseEntity<ResultadoAceiteConviteOrganizacaoRecord>
            aceitarUsuarioExistente(
                    @RequestBody
                    @Valid
                    AceiteConviteOrganizacaoUsuarioExistenteRecord
                            dados
            ) {
        return ResponseEntity.ok(
                service.aceitarUsuarioExistente(
                        dados
                )
        );
    }

    @PostMapping("/aceite/novo-usuario")
    public ResponseEntity<ResultadoAceiteConviteOrganizacaoRecord>
            aceitarNovoUsuario(
                    @RequestBody
                    @Valid
                    AceiteConviteOrganizacaoNovoUsuarioRecord
                            dados
            ) {
        return ResponseEntity.ok(
                service.aceitarNovoUsuario(
                        dados
                )
        );
    }
}