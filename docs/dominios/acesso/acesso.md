# Acesso e Segurança

## Objetivo

O domínio de Acesso e Segurança controla a autenticação, autorização, sessões, perfis, permissões e rastreabilidade dos acessos ao ERP.

Esse domínio deve garantir que cada usuário acesse somente as funcionalidades permitidas conforme seus perfis e permissões, mantendo segurança, auditoria e controle corporativo.

## Escopo

Este domínio contempla:

- Usuário;
- Perfil;
- Permissão;
- Perfil x Permissão;
- Usuário x Perfil;
- Login com e-mail e senha;
- Login por SSO corporativo;
- Refresh token;
- Logout;
- Controle de sessões;
- Bloqueio por tentativas inválidas de login;
- Controle de acesso por permissão;
- Auditoria de eventos de segurança.

## Usuário

Representa uma pessoa autorizada a acessar o ERP.

### Regras

- O e-mail deve ser único.
- O e-mail deve ser armazenado normalizado em minúsculo.
- A senha deve ser armazenada criptografada.
- A senha deve seguir política de senha forte.
- Usuário removido não pode ser atualizado.
- Usuário removido não deve autenticar.
- Alteração de senha deve revogar sessões ativas.
- Remoção lógica do usuário deve revogar sessões ativas.

## Perfil

Representa um agrupador de permissões.

### Regras

- O nome do perfil deve ser obrigatório.
- O nome do perfil deve ser único entre registros ativos.
- Perfil removido não pode ser atualizado.
- Perfil crítico do sistema não pode ser editado ou removido.
- Perfis podem receber múltiplas permissões.
- Usuários podem possuir múltiplos perfis.

## Permissão

Representa uma ação permitida dentro do ERP.

Exemplos:

- `ACESSO_USUARIO_LISTAR`
- `ACESSO_USUARIO_CRIAR`
- `ACESSO_PERFIL_EDITAR`
- `ACESSO_PERMISSAO_EXCLUIR`

### Regras

- A chave da permissão deve ser única entre registros ativos.
- Permissão removida não pode ser atualizada.
- Permissão crítica do sistema não pode ser editada ou removida.
- A autorização dos endpoints deve usar permissões específicas.

## Perfil x Permissão

Representa o vínculo entre um perfil e uma permissão.

### Regras

- Um perfil pode possuir várias permissões.
- Uma permissão pode estar vinculada a vários perfis.
- Não deve existir vínculo ativo duplicado entre o mesmo perfil e a mesma permissão.
- Vínculos removidos devem manter auditoria.
- Vínculos removidos não devem conceder acesso.

## Usuário x Perfil

Representa o vínculo entre um usuário e um perfil.

### Regras

- Um usuário pode possuir vários perfis.
- Um perfil pode estar vinculado a vários usuários.
- Não deve existir vínculo ativo duplicado entre o mesmo usuário e o mesmo perfil.
- Vínculos removidos devem manter auditoria.
- Vínculos removidos não devem conceder permissões.

## Login comum

Fluxo de autenticação com e-mail e senha.

### Regras

- O login deve validar e-mail e senha.
- Credenciais inválidas devem retornar erro genérico.
- O sistema não deve informar se o e-mail existe.
- Login bem-sucedido deve gerar access token e refresh token.
- Login bem-sucedido deve limpar tentativas inválidas anteriores.
- Falha de login deve registrar tentativa inválida.
- Excesso de falhas deve bloquear temporariamente o login.

## Login SSO

Fluxo de autenticação por provedor corporativo.

### Regras

- O token SSO deve ser validado contra o provedor configurado.
- O issuer deve ser validado.
- A audience deve ser validada.
- O escopo autorizado deve ser validado.
- O e-mail deve ser extraído do claim configurado.
- O usuário interno deve existir e estar autorizado.
- Login SSO bem-sucedido deve gerar token interno do ERP.

## Access Token

Token JWT utilizado para autenticar requisições na API.

### Regras

- Deve possuir issuer configurado.
- Deve possuir tempo de expiração configurado.
- Deve possuir identificador único `jti`.
- Deve ser assinado com secret seguro.
- Token expirado ou inválido deve retornar `401`.
- Token revogado deve retornar `401`.
- O `jti` deve estar associado a uma sessão ativa.

## Refresh Token

Token utilizado para renovar a sessão do usuário.

### Regras

- Deve ser gerado com valor aleatório seguro.
- Deve ser armazenado somente em hash.
- Deve possuir expiração configurada.
- Deve ser rotacionado a cada renovação.
- Refresh token inválido deve retornar `401`.
- Refresh token revogado não deve renovar sessão.

## Logout

Encerra uma sessão ativa.

### Regras

- Deve revogar a sessão vinculada ao refresh token.
- Após logout, o refresh token não pode mais ser usado.
- Após logout, o access token vinculado deve ser considerado revogado pelo `jti`.

## Controle de tentativas de login

Controla falhas consecutivas de autenticação.

### Regras

- O número máximo de falhas deve ser configurável.
- O tempo de bloqueio deve ser configurável.
- Falhas devem ser registradas por e-mail.
- Login bloqueado deve retornar mensagem genérica.
- Login com sucesso deve limpar falhas anteriores.

## Controle de acesso

O backend deve centralizar todas as regras de autorização.

### Regras

- Endpoints protegidos devem exigir autenticação.
- Endpoints funcionais devem exigir permissão específica.
- A autorização deve ser feita por permissões, não apenas por perfil.
- O frontend não deve ser responsável por garantir segurança.
- Ausência de autenticação deve retornar `401`.
- Acesso sem permissão deve retornar `403`.

## Swagger

### Regras

- Em produção, o Swagger não deve ficar público.
- O acesso ao Swagger deve ser controlado por profile/configuração.
- Em desenvolvimento, pode ficar público para facilitar testes.

## CORS

### Regras

- As origens permitidas devem ser configuráveis por profile.
- Produção não deve depender de origem localhost.
- A política deve ser revisada quando o domínio final do frontend estiver definido.

## Auditoria de segurança

Eventos de segurança devem ser rastreáveis.

### Eventos mínimos

- Login com sucesso;
- Falha de login;
- Login bloqueado;
- Logout;
- Refresh token;
- Token inválido;
- Acesso negado;
- Acesso sem autenticação;
- Alteração de senha;
- Revogação de sessões.

### Dados recomendados

- Tipo do evento;
- Data e hora;
- Usuário, quando identificado;
- E-mail informado, quando aplicável;
- IP;
- User-Agent;
- Resultado;
- Motivo;
- Endpoint;
- Método HTTP.

## Regras de status

Todos os registros do sistema devem seguir o padrão:

- `ATIVO`;
- `INATIVO`;
- `REMOVIDO`.

Registros removidos logicamente devem manter rastreabilidade e não devem participar das regras ativas do sistema.

## Critérios de conclusão

O domínio de Acesso e Segurança será considerado concluído quando:

- Login comum estiver funcional;
- Login SSO estiver funcional;
- Refresh token estiver funcional;
- Logout revogar sessão;
- Access token revogado for bloqueado;
- Perfis e permissões controlarem endpoints;
- Tentativas inválidas de login forem controladas;
- Swagger estiver protegido em produção;
- CORS estiver externalizado;
- Erros de segurança forem padronizados;
- Eventos de segurança forem auditados;
- Testes automatizados cobrirem os fluxos principais.

## Situação atual da implementação

### Implementado

- Usuário;
- Perfil;
- Permissão;
- Perfil x Permissão;
- Usuário x Perfil;
- Login com e-mail e senha;
- Login por SSO corporativo;
- JWT com issuer, expiração e jti;
- Refresh token com hash;
- Rotação de refresh token;
- Logout com revogação de sessão;
- Bloqueio de access token revogado por jti;
- Revogação de sessões ao alterar senha;
- Revogação de sessões ao remover usuário;
- Política de senha forte;
- Controle de tentativas inválidas de login;
- Bloqueio temporário por excesso de falhas;
- Swagger controlado por profile;
- CORS externalizado por configuração;
- Erros de segurança padronizados em JSON;
- Controle de endpoints com @PreAuthorize;
- Testes automatizados dos principais fluxos de segurança.

### Pendente

- Auditoria específica de eventos de segurança;
- Logs estruturados de eventos sensíveis;
- Métricas/observabilidade de segurança;
- Refinamento final de CORS com domínio real de produção, quando definido.

### Não bloqueante nesta etapa

- Métricas integradas com ferramenta externa;
- Dashboards de segurança;
- Alertas automáticos de comportamento suspeito.