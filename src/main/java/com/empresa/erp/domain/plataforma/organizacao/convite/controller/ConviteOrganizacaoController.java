package com.empresa.erp.domain.plataforma.organizacao.convite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.DetalheConviteOrganizacaoRecord;
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

    @GetMapping("/consulta")
    public ResponseEntity<ConsultaConviteOrganizacaoRecord>
            consultar(
                    @RequestParam
                    String token
            ) {
        return ResponseEntity.ok(
                service.consultar(token)
        );
    }
}