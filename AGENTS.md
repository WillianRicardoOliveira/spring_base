## Visão Geral do Sistema

Este projeto é um **ERP corporativo voltado para empresas do agronegócio**, desenvolvido para centralizar, organizar e automatizar os principais processos operacionais, administrativos, financeiros e comerciais da organização.

O sistema atende diferentes áreas da empresa, respeitando:

- A separação de responsabilidades entre os módulos;
- Os perfis de acesso;
- As permissões de cada usuário;
- As regras de negócio de cada área;
- A integridade e a rastreabilidade das informações.

## Áreas e Módulos

O ERP contempla módulos já desenvolvidos, módulos em desenvolvimento e módulos planejados para as seguintes áreas:

- Dashboard;
- Comercial;
- Compras;
- Contabilidade;
- Controladoria;
- Crédito;
- Estoque;
- Faturamento;
- Financeiro;
- Fiscal;
- Supply;
- Logística Nacional;
- Logística Internacional;
- Cadastros;
- Relatórios;
- Configurações.

O módulo de **Compras** é destinado principalmente às aquisições internas necessárias para o funcionamento da empresa.

O módulo de **Supply** é destinado principalmente à aquisição nacional e internacional de produtos para comercialização, incluindo contratos, pedidos de compra, custos, fretes, recebimentos, faturas, parcelas e pagamentos relacionados à operação.

Os módulos de **Logística Nacional** e **Logística Internacional** devem atender às particularidades de cada tipo de operação, mantendo integração com compras, Supply, estoque, faturamento, financeiro e fiscal.

## Integração entre os Módulos

Os módulos devem trabalhar de forma integrada, evitando:

- Duplicidade de informações;
- Regras de negócio repetidas;
- Processos isolados sem rastreabilidade;
- Inconsistências entre áreas;
- Dependências desnecessárias entre componentes.

As informações devem ser consistentes, rastreáveis e reutilizadas ao longo dos diferentes processos do negócio.

Uma alteração em determinado módulo pode afetar outros módulos relacionados. Por isso, todo impacto funcional, técnico e de segurança deve ser analisado antes da implementação.

## Prioridades do Projeto

Por se tratar de um sistema corporativo, o projeto deve priorizar:

- Segurança e controle de acesso;
- Perfis e permissões por funcionalidade;
- Integridade e rastreabilidade das informações;
- Padronização dos processos;
- Auditoria das operações realizadas;
- Escalabilidade;
- Facilidade de manutenção;
- Desempenho;
- Boa experiência de uso;
- Responsividade;
- Compatibilidade com diferentes dispositivos e resoluções;
- Cobertura adequada por testes automatizados.

## Arquitetura

O sistema possui uma arquitetura separada entre:

- Frontend;
- Backend;
- Banco de dados;
- Infraestrutura.

Cada camada deve manter responsabilidades bem definidas.

As regras de negócio e as validações de segurança devem permanecer centralizadas no backend. O frontend deve ser responsável principalmente pela apresentação das informações, interação com o usuário e validações relacionadas à experiência de uso.

Não devem ser criadas dependências desnecessárias entre as camadas ou implementadas regras de negócio importantes exclusivamente no frontend.

## Diretrizes para Implementações

Ao analisar, propor ou implementar alterações, deve-se:

1. Considerar o contexto de um ERP corporativo para o agronegócio;
2. Respeitar a arquitetura e os padrões existentes no projeto;
3. Avaliar impactos nos demais módulos;
4. Preservar as funcionalidades já implementadas;
5. Respeitar os perfis e as permissões dos usuários;
6. Evitar alterações desnecessárias ou refatorações fora do escopo;
7. Não remover funcionalidades sem uma justificativa técnica clara;
8. Manter o padrão visual e estrutural existente;
9. Implementar alterações de forma incremental e verificável;
10. Criar ou atualizar testes quando aplicável;
11. Não assumir que todos os módulos listados estão concluídos;
12. Confirmar o estado atual do código antes de propor uma solução.

## Contexto de Negócio

Este projeto não deve ser tratado como um sistema administrativo genérico.

Trata-se de uma plataforma corporativa preparada para atender processos específicos e complexos de empresas ligadas ao agronegócio, incluindo operações nacionais e internacionais, contratos, compras, Supply, logística, estoque, crédito, faturamento, financeiro, contabilidade e obrigações fiscais.

Toda nova funcionalidade deve considerar esse contexto antes da definição ou alteração de:

- Entidades;
- Relacionamentos;
- Fluxos;
- Telas;
- Menus;
- Permissões;
- Integrações;
- Validações;
- Regras de negócio.



# Projeto Spring Boot

## Tecnologias

- Java 17
- Spring Boot
- Maven
- Flyway

## Ler prioritariamente

- src/main/java
- src/main/resources
- src/test/java
- pom.xml

## Ignorar

- bin
- target
- .settings
- .classpath
- .project
- *.class
- *.jar
- *.war

## Estrutura

- Controllers em src/main/java/.../controller
- Services em src/main/java/.../service
- Repositories em src/main/java/.../repository
- Models em src/main/java/.../model
- Migrations Flyway em src/main/resources/db/migration

## Regras

- Nunca analisar arquivos compilados.
- Utilizar somente código-fonte.
- Priorizar soluções compatíveis com Spring Boot 4.1.0 e Java 17.
- Antes de sugerir alterações, identificar impacto em Controller, Service e Repository.