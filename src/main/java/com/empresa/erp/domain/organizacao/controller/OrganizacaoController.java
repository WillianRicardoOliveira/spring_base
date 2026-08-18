package com.empresa.erp.domain.organizacao.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.domain.organizacao.record.OrganizacaoDisponivelRecord;
import com.empresa.erp.domain.organizacao.service.OrganizacaoDisponivelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/organizacao")
@RequiredArgsConstructor
public class OrganizacaoController {

    private final OrganizacaoDisponivelService service;

    @GetMapping("/disponiveis")
    public ResponseEntity<List<OrganizacaoDisponivelRecord>>
            listarDisponiveis() {
        return ResponseEntity.ok(
                service.listar()
        );
    }
}