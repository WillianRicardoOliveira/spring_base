# Cadastro de Organização

> Este documento descreve o comportamento funcional esperado da organização. A implementação será realizada progressivamente conforme o planejamento técnico, preservando as regras aqui definidas.

## Objetivo

- A organização representa um cliente do ERP SaaS e constitui o limite principal de isolamento dos seus dados.
- Uma organização pode possuir várias empresas, subsidiárias, usuários, perfis e demais dados operacionais.
- Dados pertencentes a uma organização não podem ser acessados por usuários vinculados exclusivamente a outras organizações.
- O cadastro administrativo da organização pertence ao contexto da plataforma, não ao contexto operacional do cliente.
- Uma organização somente deve ser considerada provisionada quando possuir um administrador inicial válido.

## Campos

### Nome

- **Descrição:** Nome utilizado para identificar a organização no sistema.
- **Tipo:** Texto.
- **Preenchimento:** Manual.
- **Obrigatório:** Sim.
- **Tamanho máximo:** 100 caracteres.

**Regras:**

- Remover espaços no início e no final.
- Substituir múltiplos espaços internos por um único espaço.
- Não aceitar valor vazio ou composto somente por espaços.
- O nome não precisa ser único entre organizações.
- A comparação ou pesquisa pelo nome deve desconsiderar diferenças entre letras maiúsculas e minúsculas, quando aplicável.

### Status

- **Descrição:** Indica a situação atual da organização.
- **Tipo:** Controlado pelo sistema.
- **Preenchimento:** Automático.
- **Valor inicial:** Ativo.

**Valores:**

- Ativo.
- Inativo.
- Removido.

O status não deve ser informado livremente durante o cadastro ou edição da organização.

## Administração da plataforma

A administração de organizações pertence exclusivamente ao contexto global da plataforma.

As operações administrativas não dependem de uma organização ativa e não devem utilizar o header `X-Organizacao-Id`.

O envio indevido desse header em uma operação da plataforma não deve alterar seu comportamento nem exigir vínculo do administrador com uma organização.

### Funcionalidades administrativas

- Convidar o administrador inicial para criação de uma organização.
- Listar organizações.
- Detalhar uma organização.
- Editar o nome de uma organização.
- Inativar uma organização.
- Reativar uma organização inativa.
- Remover logicamente uma organização.
- Listar convites.
- Detalhar um convite.
- Revogar um convite pendente.
- Reenviar um convite pendente.

### Criação da organização

- A criação de uma organização ocorre por meio do fluxo de convite e provisionamento.
- Não deve existir, nesta primeira versão, um cadastro direto que crie uma organização sem administrador.
- O envio do convite ainda não cria a organização.
- A organização é criada somente quando o convite é aceito com sucesso.
- A organização, o administrador inicial, o vínculo organizacional, o perfil administrativo e suas permissões devem ser provisionados na mesma transação.
- Uma falha durante o provisionamento deve cancelar toda a criação.
- Não devem permanecer organizações parcialmente configuradas.

## Permissões da plataforma

- **Criar organizações e gerenciar convites:** `PLATAFORMA_ORGANIZACAO_CRIAR`
- **Listar organizações:** `PLATAFORMA_ORGANIZACAO_LISTAR`
- **Editar organizações:** `PLATAFORMA_ORGANIZACAO_EDITAR`
- **Detalhar organizações:** `PLATAFORMA_ORGANIZACAO_DETALHAR`
- **Alterar status:** `PLATAFORMA_ORGANIZACAO_STATUS`
- **Remover:** `PLATAFORMA_ORGANIZACAO_EXCLUIR`

## Regras das permissões

- As ações administrativas somente podem ser executadas por administradores da plataforma que possuam a respectiva permissão.
- As permissões de organização são exclusivas da administração da plataforma.
- As permissões da plataforma não podem ser atribuídas a perfis internos de uma organização.
- A consulta das organizações disponíveis para o próprio usuário não depende da permissão `PLATAFORMA_ORGANIZACAO_LISTAR`.
- A permissão de listar organizações administrativamente não concede automaticamente permissão para criar, editar, alterar status ou remover organizações.
- O administrador de uma organização não se torna administrador da plataforma.
- O administrador da plataforma não recebe automaticamente acesso operacional às organizações administradas.
- A criação, revogação e o reenvio de convites pertencem ao fluxo de criação de organizações e utilizam `PLATAFORMA_ORGANIZACAO_CRIAR`.

## Ciclo de vida

- Uma organização é criada com status ativo.
- Uma organização ativa pode ser inativada.
- Inativar uma organização já inativa não deve causar erro.
- Uma organização inativa pode ser reativada.
- Reativar uma organização já ativa não deve causar erro.
- Uma organização ativa ou inativa pode ser removida logicamente.
- A alteração para removido ocorre exclusivamente pela funcionalidade de remoção.
- O status removido é terminal.
- Uma organização removida não pode ser reativada.
- Uma organização removida não pode ser editada.
- Uma organização removida não pode ser utilizada operacionalmente.
- A remoção não realiza exclusão física da organização ou dos seus dados.
- A inativação ou remoção não altera o status nem remove empresas, subsidiárias, usuários, perfis, arquivos ou outros registros vinculados.
- Os relacionamentos e dados devem ser preservados para histórico e auditoria.
- Organizações inativas e removidas continuam disponíveis para consulta administrativa da plataforma.
- Organizações removidas não devem aparecer para seleção de contexto.

## Segurança

- Uma organização inativa ou removida não permite acesso operacional aos seus dados e recursos.
- A inativação ou remoção deve impedir novas requisições operacionais imediatamente, inclusive para usuários com token ou sessão ainda válidos.
- Uma organização inativa ou removida continua disponível para as operações permitidas da administração da plataforma.
- Uma organização removida não pode ser apresentada para seleção de contexto.
- O usuário somente pode acessar organizações ativas com vínculo `UsuarioOrganizacao` ativo.
- A organização informada pelo frontend nunca deve ser considerada confiável sem validação no backend.
- O conhecimento do identificador de uma organização não concede acesso aos seus dados.
- Falhas de autorização não devem revelar desnecessariamente a existência de organizações pertencentes a outros clientes.
- Permissões funcionais não substituem a validação da organização ativa.
- Permissões da plataforma devem ser carregadas separadamente das permissões internas das organizações.

## Contexto organizacional

- O JWT identifica o usuário global e não determina uma organização fixa.
- A organização ativa é informada por meio do header `X-Organizacao-Id`.
- O usuário pode trocar de organização sem precisar realizar um novo login.
- O backend deve validar a organização e o vínculo `UsuarioOrganizacao` em cada requisição que informe contexto.
- Somente uma organização pode estar ativa durante uma requisição.
- Não é permitido trocar a organização ativa durante a execução da mesma requisição.
- O contexto validado deve ficar disponível aos services sem que eles precisem acessar diretamente o header HTTP.
- Operações organizacionais devem falhar quando o contexto obrigatório não estiver definido.
- Um identificador nulo, vazio, não numérico, igual a zero ou negativo deve ser considerado inválido.
- Uma organização inexistente, inativa, removida ou sem vínculo ativo deve resultar em acesso negado.
- Endpoints de login, renovação de token, administração da plataforma, convite e consulta de organizações disponíveis não dependem de contexto organizacional.
- Endpoints da plataforma devem ignorar `X-Organizacao-Id`.
- O filtro organizacional não deve adicionar permissões internas às operações administrativas da plataforma.

## Seleção de organização

- Todo usuário autenticado pode consultar suas organizações disponíveis sem possuir permissão funcional específica.
- A consulta retorna somente organizações ativas com vínculo `UsuarioOrganizacao` ativo.
- A consulta deve retornar apenas as informações necessárias para identificação e seleção.
- Inicialmente, devem ser retornados somente o identificador e o nome da organização.
- As organizações disponíveis devem ser ordenadas pelo nome.
- A organização selecionada deve ser validada novamente pelo backend antes de ser utilizada.
- O usuário sem organizações disponíveis não pode acessar os módulos operacionais.
- A consulta de organizações disponíveis deve funcionar sem o header `X-Organizacao-Id`.
- O frontend não deve enviar `X-Organizacao-Id` ao consultar as organizações disponíveis.
- A ausência do header em um endpoint que não depende de contexto não deve impedir a requisição.

## Isolamento de dados

- Toda entidade pertencente ao cliente deve estar vinculada direta ou indiretamente a uma organização.
- Toda operação sobre dados organizacionais deve considerar a organização ativa.
- O identificador informado em `X-Organizacao-Id` somente pode ser utilizado depois da validação do vínculo ativo entre usuário e organização.
- Um usuário não pode acessar registros de outra organização, mesmo conhecendo seus identificadores.
- Controllers e records operacionais não devem receber livremente o identificador da organização quando ele puder ser obtido do contexto validado.
- As consultas aos dados organizacionais devem incluir a organização ativa como critério de isolamento.
- A validação somente por permissão funcional não substitui a validação da organização.
- Empresas, subsidiárias, perfis internos, acessos, arquivos e demais módulos operacionais devem respeitar o contexto organizacional.
- A organização ativa não deve ser obtida somente do frontend ou considerada confiável sem validação no backend.
- Operações de atualização e remoção também devem validar a organização proprietária do registro.
- Registros de organizações diferentes não podem ser relacionados entre si.

## Convite para criação de organização

### Objetivo

- O convite inicia o processo de criação de uma organização.
- Cada convite define o nome da futura organização e o e-mail do seu administrador inicial.
- O convite não concede acesso administrativo à plataforma.
- O convite não cria uma organização antes do aceite.

### Campos

- Identificador.
- Nome da organização.
- E-mail do administrador.
- Hash do token.
- Data de expiração.
- Data de aceite, quando aplicável.
- Status.
- Dados de auditoria.

### Status do convite

- Pendente.
- Aceito.
- Revogado.

A expiração é calculada a partir da data de validade e não constitui um status persistido separado.

### Criação do convite

- O nome da organização é obrigatório.
- O e-mail do administrador é obrigatório e deve possuir formato válido.
- Nome e e-mail devem ser normalizados antes da persistência.
- O e-mail deve ser armazenado em letras minúsculas.
- Não pode existir mais de um convite pendente para o mesmo e-mail.
- Um convite pendente expirado pode ser revogado automaticamente quando um novo convite for criado para o mesmo e-mail.
- Um usuário global ativo já existente pode receber convite para administrar uma nova organização.
- Um usuário global inativo ou removido não pode receber um novo convite enquanto sua situação não for resolvida.
- O token deve ser criptograficamente aleatório.
- Somente o hash do token deve ser armazenado no banco.
- O token original deve existir apenas durante o fluxo necessário para gerar o link e enviar o e-mail.
- O prazo de validade deve ser configurável por ambiente.
- O convite inicia com status pendente.

### Consulta pública

- A consulta pública deve receber o token no corpo da requisição.
- O token não deve ser enviado como parâmetro da URL da API do backend.
- Um token inexistente, inválido, expirado, aceito ou revogado deve produzir uma resposta genérica.
- A resposta não deve revelar o e-mail completo.
- O e-mail deve ser mascarado.
- A consulta pode informar se já existe uma conta global ativa para o e-mail, permitindo que o frontend apresente o fluxo correto.

### Aceite por usuário existente

- O usuário deve estar autenticado.
- O e-mail do usuário autenticado deve corresponder ao e-mail do convite.
- O convite deve estar pendente e dentro do prazo de validade.
- O usuário global deve estar ativo.
- O aceite deve provisionar a organização, o vínculo e os acessos administrativos iniciais.
- O usuário não deve receber permissões administrativas da plataforma.
- Após o provisionamento, o convite deve ser marcado como aceito.
- O token não pode ser utilizado novamente.

### Aceite por novo usuário

- O aceite pode ser realizado sem autenticação.
- O convite deve estar pendente e dentro do prazo de validade.
- O e-mail não pode estar cadastrado globalmente.
- A senha deve atender à política de segurança do sistema.
- A identidade global do usuário deve ser criada na mesma transação do provisionamento.
- A organização, o usuário, o vínculo e os acessos iniciais devem ser criados de forma transacional.
- Após o provisionamento, o convite deve ser marcado como aceito.
- Uma falha em qualquer parte deve cancelar toda a operação.
- O token não pode ser utilizado novamente.

### Revogação

- Somente convites pendentes podem ser revogados.
- Convites aceitos não podem ser revogados.
- Revogar novamente um convite já revogado não deve causar erro.
- Um convite revogado não pode ser aceito.
- A revogação deve liberar o e-mail para criação de um novo convite.
- O histórico do convite revogado deve ser preservado.

### Reenvio

- Somente convites pendentes podem ser reenviados.
- Um convite pendente expirado pode ser reenviado.
- Convites aceitos ou revogados não podem ser reenviados.
- O reenvio deve manter o mesmo registro.
- O reenvio deve gerar um token novo.
- O hash anterior deve ser substituído.
- O link anterior deve deixar de funcionar.
- O prazo de validade deve ser reiniciado.
- O reenvio deve preservar o histórico e os dados de auditoria do convite.

### Envio do e-mail

- O envio deve ocorrer somente depois da confirmação da transação que criou ou renovou o convite.
- O e-mail deve conter o link do frontend para aceite.
- O endereço do frontend, o remetente, o prazo e os dados SMTP devem ser configuráveis por ambiente.
- Token, e-mail e nome da organização não devem ser expostos nos logs.
- Uma falha do SMTP não deve desfazer uma transação já confirmada.
- A falha deve ser registrada sem informações sensíveis.
- O convite deve permanecer pendente para permitir reenvio administrativo.
- Filas, retentativas automáticas e outbox poderão ser avaliados futuramente, sem fazer parte da V1.

## Provisionamento inicial

- A criação de uma organização deve definir seu primeiro usuário administrador.
- A organização, o vínculo `UsuarioOrganizacao` e o acesso administrativo inicial devem ser criados de forma transacional.
- Uma falha em qualquer parte do provisionamento deve impedir a conclusão de toda a operação.
- O administrador inicial da organização não se torna administrador da plataforma.
- Um usuário global já existente pode ser definido como administrador inicial de uma nova organização.
- Quando o e-mail ainda não existir, o aceite deve criar o usuário global.
- A organização provisionada somente deve ser considerada operacional quando possuir um administrador inicial válido.
- O vínculo inicial `UsuarioOrganizacao` deve ser criado como ativo.
- O perfil administrativo inicial deve pertencer à organização criada.
- O perfil administrativo inicial deve receber as permissões organizacionais de sistema previstas para administração.
- Permissões da plataforma não podem ser adicionadas ao perfil interno.
- Dados criados automaticamente durante uma migração ou provisionamento técnico devem respeitar as mesmas relações organizacionais.

## Auditoria

- A criação da organização deve registrar a data e o usuário responsável, quando existir.
- A atualização da organização deve registrar a data e o usuário responsável.
- A remoção lógica deve registrar a data e o usuário responsável.
- Operações executadas automaticamente pela plataforma podem não possuir usuário responsável.
- Os dados de auditoria não podem ser alterados manualmente pelas funcionalidades comuns do cadastro.
- A inativação, reativação e remoção devem ser passíveis de auditoria.
- A remoção lógica deve preservar os dados necessários para rastreabilidade e histórico.
- Convites devem possuir auditoria de criação e atualização.
- O aceite, a revogação e o reenvio não devem apagar o histórico do convite.

## Relacionamentos

### Empresa

- Uma organização pode não possuir empresas ou possuir uma ou várias empresas.
- Uma empresa pertence obrigatoriamente a uma única organização.
- Uma empresa não pode ser transferida livremente para outra organização.
- Empresas de organizações diferentes não podem compartilhar dados operacionais.
- O cadastro e as consultas de empresas devem utilizar a organização ativa.
- O nome da empresa deve ser validado dentro da organização quando existir regra de duplicidade.

### Subsidiária

- Uma subsidiária pertence obrigatoriamente a uma empresa.
- A organização da subsidiária é determinada pela organização da sua empresa.
- Uma subsidiária não pode pertencer a uma organização diferente da empresa.
- O acesso à subsidiária deve validar a organização da empresa à qual ela pertence.

### Usuário

- O usuário é global e identificado de forma única no sistema.
- Um usuário pode não estar vinculado a nenhuma organização ou estar vinculado a várias organizações.
- O vínculo entre usuário e organização é representado por `UsuarioOrganizacao`.
- O mesmo usuário não pode possuir mais de um vínculo com a mesma organização.
- O vínculo possui status próprio.
- Um vínculo ativo permite que a organização apareça para seleção.
- Um vínculo inativo impede acesso à organização.
- Um vínculo inativo pode ser reativado sem criar um novo registro.
- Somente vínculos ativos com organizações ativas aparecem para seleção de contexto.
- A inativação do vínculo não inativa nem remove o usuário global.
- A inativação do usuário global impede seu acesso a todas as organizações.
- Uma organização pode não possuir usuários ativos, mas somente será operacional quando possuir ao menos um administrador interno válido.

### Gestão de usuários da organização

- A tela operacional de usuários representa os membros da organização atual, e não todas as identidades globais da plataforma.
- A listagem deve retornar somente usuários que possuam vínculo `UsuarioOrganizacao` ativo com a organização atual.
- O detalhamento deve exigir vínculo ativo entre o usuário solicitado e a organização atual.
- O conhecimento do identificador global de um usuário não permite que outra organização o consulte ou administre.
- O cadastro realizado por uma organização deve criar a identidade `Usuario` e o vínculo `UsuarioOrganizacao` na mesma transação.
- Uma falha na criação da identidade ou do vínculo deve cancelar toda a operação.
- Nesta primeira etapa, o cadastro operacional aceita somente e-mails ainda não existentes na plataforma.
- Quando o e-mail já existir globalmente, o cadastro deve ser recusado sem criar outro usuário.
- A associação de um usuário global já existente a outra organização deverá ser realizada futuramente por um fluxo específico de convite ou vinculação.
- A limitação temporária para e-mails existentes não altera a regra de que um usuário pode pertencer a várias organizações.
- A remoção operacional de um usuário deve inativar somente o vínculo `UsuarioOrganizacao` da organização atual.
- A remoção operacional não deve inativar nem remover a identidade global `Usuario`.
- A inativação do vínculo deve impedir imediatamente o acesso do usuário à organização correspondente.
- A identidade global deve permanecer disponível para outras organizações com vínculos ativos.
- A alteração do e-mail global não pertence à administração operacional comum de uma organização.
- A alteração da senha global não pertence à administração operacional comum de uma organização.
- A alteração da própria senha deverá ser tratada por um fluxo pessoal de conta.
- A administração e a remoção da identidade global pertencem ao contexto administrativo da plataforma.
- Antes de inativar um vínculo `UsuarioOrganizacao`, o sistema deve validar dependências organizacionais que impeçam a operação.
- As validações de empresas, subsidiárias, perfis e demais acessos devem considerar somente a organização atual.

### Perfil

- Um perfil interno pertence obrigatoriamente a uma única organização.
- Um perfil de uma organização não pode ser atribuído a usuários de outra organização.
- As permissões funcionais são definidas pelo sistema e podem ser associadas aos perfis internos da organização.
- O perfil do usuário deve ser considerado dentro do vínculo `UsuarioOrganizacao`.
- O mesmo usuário pode possuir perfis diferentes em organizações diferentes.
- Perfis internos não concedem poderes de administração da plataforma.
- As permissões carregadas para o usuário devem considerar somente a organização ativa.

### Permissão

- As permissões funcionais são globais e definidas pelo sistema.
- Uma permissão funcional pode ser utilizada por perfis internos de várias organizações.
- A associação entre perfil e permissão deve respeitar a organização proprietária do perfil.
- Permissões exclusivas da plataforma não podem ser associadas a perfis internos.
- As permissões efetivas do usuário devem ser carregadas conforme seus perfis na organização ativa.

### Administrador da organização

- O administrador da organização administra os recursos internos do cliente conforme suas permissões.
- O administrador da organização depende de vínculo `UsuarioOrganizacao` ativo.
- O administrador da organização não pode administrar outras organizações sem possuir vínculo e permissões correspondentes.
- O administrador da organização não possui acesso automático às funções administrativas da plataforma.

### Administrador da plataforma

- O administrador da plataforma é global e não depende de `UsuarioOrganizacao` para administrar organizações.
- Seus acessos não devem ser representados por perfis internos de uma organização.
- A administração da plataforma deve possuir permissões separadas das permissões funcionais dos clientes.
- O administrador da plataforma não deve depender de contexto organizacional para executar funções administrativas da plataforma.
- O acesso administrativo da plataforma não concede automaticamente acesso operacional aos dados internos das organizações.

## Configuração por ambiente

### Desenvolvimento

- Pode utilizar banco local.
- Pode utilizar frontend em `localhost`.
- Pode disponibilizar Swagger.
- Pode utilizar servidor SMTP local.
- Não deve utilizar credenciais de produção.

### Testes

- Deve utilizar banco isolado.
- Não deve executar migrations de produção quando o schema for criado automaticamente pelo Hibernate.
- Não deve enviar e-mails reais.
- Não deve executar bootstrap.
- Não deve depender de variáveis ou serviços externos.

### Produção

- Deve utilizar o profile `prod`.
- Credenciais e segredos devem ser fornecidos por variáveis de ambiente.
- A origem permitida pelo CORS deve ser configurada explicitamente.
- A URL de aceite não pode apontar para `localhost`.
- O SMTP deve ser configurado explicitamente.
- Swagger e documentação pública devem permanecer desativados.
- Flyway deve controlar as migrations.
- Hibernate deve apenas validar o schema.
- O bootstrap deve permanecer desativado, exceto durante uma operação inicial controlada.
- Segredos, senhas e tokens não devem ser registrados em logs nem armazenados no repositório.

## Implantação da V1

### Profile obrigatório

A aplicação deve ser iniciada em produção com:

```text
SPRING_PROFILES_ACTIVE=prod
```

O profile `prod` é obrigatório para:

- exigir configurações externas de produção;
- impedir origens CORS locais;
- exigir URL pública para aceite de convites;
- exigir configuração SMTP;
- manter Swagger e API Docs desabilitados;
- executar migrations com Flyway;
- manter o Hibernate somente validando o schema.

A ausência do profile `prod` deve ser tratada como erro de implantação.

### Variáveis obrigatórias

#### Banco de dados

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

- A URL deve apontar para o banco de produção.
- O usuário deve permitir a execução das migrations.
- As credenciais não devem ser armazenadas no repositório.

#### JWT e sessões

```text
JWT_SECRET
JWT_ISSUER
JWT_EXPIRATION_MINUTES
REFRESH_TOKEN_EXPIRATION_DAYS
REFRESH_TOKEN_CLEANUP_CRON
```

- O segredo deve ser forte, aleatório e exclusivo do ambiente.
- O segredo não deve ser compartilhado com desenvolvimento ou testes.
- A alteração do segredo invalida os tokens emitidos anteriormente.
- O issuer deve identificar a API de produção.
- As validades devem seguir a política de segurança da plataforma.

#### Proteção de login

```text
LOGIN_MAX_FAILED_ATTEMPTS
LOGIN_LOCK_MINUTES
```

- A quantidade de tentativas deve ser limitada.
- O tempo de bloqueio deve ser adequado à política de segurança.

#### CORS

```text
CORS_ALLOWED_ORIGINS
```

- Deve conter somente origens confiáveis.
- Não deve utilizar `*` em produção.
- Não deve apontar para `localhost`.
- Deve utilizar a origem HTTPS real do frontend.

#### Microsoft Entra ID e SSO

```text
AZURE_ISSUER_URI
AZURE_API_AUDIENCE
AZURE_API_SCOPE
```

- Os valores devem pertencer ao Tenant e ao registro de aplicação corretos.
- Audience, issuer e scope devem corresponder à API de produção.

#### Convite de organização

```text
CONVITE_ORGANIZACAO_URL_ACEITE
CONVITE_ORGANIZACAO_REMETENTE
```

- A URL deve ser HTTP ou HTTPS absoluta.
- Em produção, deve utilizar HTTPS.
- Deve apontar para a tela pública de aceite do frontend.
- Não deve apontar para `localhost`.
- O remetente deve ser autorizado pelo provedor de e-mail.

#### SMTP

```text
MAIL_HOST
MAIL_USERNAME
MAIL_PASSWORD
```

- As credenciais devem ser fornecidas por mecanismo seguro.
- O remetente deve ser aceito pelo servidor SMTP.
- O servidor deve suportar a configuração de autenticação e TLS.

### Variáveis opcionais e valores padrão

```text
SSO_EMAIL_CLAIM=preferred_username

BOOTSTRAP_ENABLED=false

CONVITE_ORGANIZACAO_VALIDADE=48h

MAIL_PORT=587
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
MAIL_SMTP_STARTTLS_REQUIRED=true

MAIL_CONNECTION_TIMEOUT=5000
MAIL_TIMEOUT=5000
MAIL_WRITE_TIMEOUT=5000
```

Os valores padrão devem ser revisados antes da implantação. A existência de um valor padrão não garante que ele seja adequado para todo ambiente de produção.

### Bootstrap inicial

O bootstrap deve ser utilizado somente para criar a estrutura administrativa inicial de uma instalação vazia.

Para executá-lo, devem ser configuradas temporariamente:

```text
BOOTSTRAP_ENABLED=true

BOOTSTRAP_ORGANIZATION_NAME
BOOTSTRAP_ORGANIZATION_ADMIN_EMAIL
BOOTSTRAP_ORGANIZATION_ADMIN_PASSWORD

BOOTSTRAP_PLATFORM_ADMIN_EMAIL
BOOTSTRAP_PLATFORM_ADMIN_PASSWORD
```

Regras:

- O banco deve estar vazio, exceto pelos dados de sistema criados pela migration.
- O nome da organização deve possuir no máximo 100 caracteres.
- Os e-mails devem ser válidos e possuir no máximo 100 caracteres.
- As senhas devem atender à política de senha forte.
- Senhas padrão ou previsíveis não são permitidas.
- As senhas devem respeitar o limite de 72 bytes do BCrypt.
- O administrador da organização e o administrador da plataforma podem utilizar a mesma identidade.
- Quando utilizarem o mesmo e-mail, devem utilizar a mesma senha durante o bootstrap.
- A criação deve ocorrer de forma transacional.
- Uma instalação parcialmente provisionada deve bloquear o bootstrap.
- O bootstrap não deve ser utilizado para criar organizações adicionais.

Após a conclusão:

1. Interromper a aplicação.
2. Alterar `BOOTSTRAP_ENABLED` para `false`.
3. Remover as credenciais do bootstrap do ambiente.
4. Iniciar novamente a aplicação.
5. Confirmar que o bootstrap não foi executado novamente.

As credenciais do bootstrap não devem permanecer no ambiente após o provisionamento inicial.

### Ordem de inicialização

A implantação inicial deve seguir esta ordem:

1. Criar o banco de dados.
2. Configurar credenciais e segredos.
3. Definir `SPRING_PROFILES_ACTIVE=prod`.
4. Configurar CORS, SSO, convite e SMTP.
5. Configurar o bootstrap somente se a instalação estiver vazia.
6. Iniciar a aplicação.
7. Permitir que o Flyway execute a migration.
8. Permitir que o Hibernate valide o schema.
9. Permitir a execução do bootstrap, quando habilitado.
10. Confirmar que a inicialização terminou sem erros.
11. Desabilitar o bootstrap e remover suas credenciais.
12. Reiniciar a aplicação com o bootstrap desabilitado.

### Verificações após a implantação

Antes de liberar o ambiente, deve ser confirmado:

- a aplicação inicia com o profile `prod`;
- o Flyway concluiu as migrations;
- o Hibernate validou o schema;
- Swagger e API Docs não estão disponíveis publicamente;
- o bootstrap está desabilitado;
- as credenciais do bootstrap foram removidas;
- o login do administrador da plataforma funciona;
- o login do administrador da organização funciona;
- o administrador consegue selecionar sua organização;
- endpoints operacionais exigem contexto organizacional;
- endpoints da plataforma funcionam sem contexto organizacional;
- usuários sem vínculo ativo não acessam a organização;
- permissões de uma organização não são utilizadas em outra;
- o CORS aceita somente o frontend configurado;
- convites geram e-mails com URL pública válida;
- tokens de convite não aparecem em logs;
- erros HTTP não expõem stack trace;
- segredos e senhas não aparecem em logs;
- o banco possui backup e procedimento de restauração;
- logs e métricas mínimas estão disponíveis para operação.


## Escopo da V1

A V1 inclui:

- organização como limite principal do Tenant;
- vínculo global entre usuário e organização;
- seleção e troca de organização ativa;
- validação do contexto em cada requisição operacional;
- permissões separadas entre plataforma e organização;
- provisionamento transacional do administrador inicial;
- convite, consulta, aceite, revogação e reenvio;
- administração da organização pela plataforma;
- inativação, reativação e remoção lógica;
- auditoria básica;
- configuração separada por ambiente;
- isolamento de empresas, subsidiárias, usuários, perfis e acessos.

Não fazem parte da V1:

- filas para envio de convite;
- outbox transacional;
- retentativas automáticas de e-mail;
- transferência de organização entre clientes;
- restauração de organização removida;
- exclusão física de Tenant;
- associação operacional de usuário global existente a outra organização;
- múltiplos administradores iniciais durante o provisionamento;
- personalização avançada do e-mail;
- painel avançado de auditoria;
- recursos de cobrança, plano ou assinatura do Tenant.

Essas funcionalidades poderão ser implementadas posteriormente sem alterar os princípios centrais de isolamento, identidade global, vínculo organizacional e permissões já definidos.