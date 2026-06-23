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