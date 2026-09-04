package com.empresa.erp.domain.configuracao.inicial.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.domain.configuracao.inicial.record.EstadoConfiguracaoInicialRecord;
import com.empresa.erp.domain.configuracao.inicial.service.ConfiguracaoInicialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/configuracao/inicial")
@RequiredArgsConstructor
public class ConfiguracaoInicialController {

    private final ConfiguracaoInicialService service;

    @GetMapping
    public ResponseEntity<EstadoConfiguracaoInicialRecord>
            consultar() {

        return ResponseEntity.ok(
                service.consultar()
        );
    }
}