# Cadastro de Organização

> Este documento descreve o comportamento funcional esperado da organização. A implementação será realizada progressivamente conforme o planejamento técnico, preservando as regras aqui definidas.

## Objetivo

- A organização representa um cliente do ERP SaaS e constitui o limite principal de isolamento dos seus dados.
- Uma organização pode possuir várias empresas e usuários.
- Dados pertencentes a uma organização não podem ser acessados por usuários vinculados exclusivamente a outras organizações.
- O cadastro administrativo da organização pertence ao contexto da plataforma, não ao contexto operacional do cliente.

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

## Funcionalidades administrativas

- Cadastrar organização.
- Listar organizações.
- Editar organização.
- Detalhar organização.
- Alterar status da organização.
- Remover organização logicamente.

As funcionalidades administrativas são exclusivas da administração da plataforma.

## Funcionalidades do usuário

- Consultar as organizações disponíveis para seleção de contexto.
- Selecionar uma organização ativa.
- Trocar a organização ativa sem realizar um novo login.

Essas funcionalidades não concedem acesso ao cadastro administrativo da organização.

## Permissões da plataforma

- **Cadastrar:** `PLATAFORMA_ORGANIZACAO_CRIAR`
- **Listar:** `PLATAFORMA_ORGANIZACAO_LISTAR`
- **Editar:** `PLATAFORMA_ORGANIZACAO_EDITAR`
- **Detalhar:** `PLATAFORMA_ORGANIZACAO_DETALHAR`
- **Alterar status:** `PLATAFORMA_ORGANIZACAO_STATUS`
- **Remover:** `PLATAFORMA_ORGANIZACAO_EXCLUIR`

## Regras das permissões

- As ações administrativas somente podem ser executadas por administradores da plataforma que possuam a respectiva permissão.
- As permissões de organização são exclusivas da administração da plataforma.
- As permissões da plataforma não podem ser atribuídas a perfis internos de uma organização.
- A consulta das organizações disponíveis para o próprio usuário não depende da permissão `PLATAFORMA_ORGANIZACAO_LISTAR`.
- A permissão de listar organizações administrativamente não concede automaticamente permissão para criar, editar, alterar status ou remover organizações.
- O administrador de uma organização não se torna administrador da plataforma.

## Ciclo de vida

- Uma organização é criada com status ativo.
- Uma organização ativa pode ser inativada.
- Uma organização inativa pode ser reativada.
- Uma organização ativa ou inativa pode ser removida logicamente.
- A alteração para removido ocorre exclusivamente pela funcionalidade de remoção.
- O status removido é terminal e não permite reativação.
- Uma organização removida não pode ser editada ou utilizada operacionalmente.
- A remoção não realiza exclusão física da organização ou dos seus dados.
- A inativação ou remoção não altera o status nem remove empresas, usuários ou outros registros vinculados.
- Os relacionamentos e dados devem ser preservados para histórico e auditoria.

## Segurança

- Uma organização inativa ou removida não permite acesso operacional aos seus dados e recursos.
- A inativação ou remoção deve impedir novas requisições operacionais imediatamente, inclusive para usuários com token ou sessão ainda válidos.
- Uma organização inativa ou removida continua disponível para as operações permitidas da administração da plataforma.
- Uma organização removida não pode ser apresentada para seleção de contexto.
- O usuário somente pode acessar organizações ativas com vínculo `UsuarioOrganizacao` ativo.
- A organização informada pelo frontend nunca deve ser considerada confiável sem validação no backend.
- O conhecimento do identificador de uma organização não concede acesso aos seus dados.
- Falhas de autorização não devem revelar desnecessariamente a existência de organizações pertencentes a outros clientes.

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
- Endpoints de login, renovação de token, administração da plataforma e consulta de organizações disponíveis não dependem de contexto organizacional.

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

## Provisionamento inicial

- A criação de uma organização deve definir seu primeiro usuário administrador.
- A organização, o vínculo `UsuarioOrganizacao` e o acesso administrativo inicial devem ser criados de forma transacional.
- Uma falha em qualquer parte do provisionamento deve impedir a conclusão de toda a operação.
- O administrador inicial da organização não se torna administrador da plataforma.
- Um usuário global já existente pode ser definido como administrador inicial de uma nova organização.
- Quando o e-mail ainda não existir, o fluxo de provisionamento poderá criar ou convidar o usuário conforme a política de acesso adotada pelo sistema.
- A organização provisionada somente deve ser considerada operacional quando possuir um administrador inicial válido.
- Dados criados automaticamente durante uma migração ou provisionamento técnico devem respeitar as mesmas relações organizacionais.

## Auditoria

- A criação da organização deve registrar a data e o usuário responsável, quando existir.
- A atualização da organização deve registrar a data e o usuário responsável.
- A remoção lógica deve registrar a data e o usuário responsável.
- Operações executadas automaticamente pela plataforma podem não possuir usuário responsável.
- Os dados de auditoria não podem ser alterados manualmente pelas funcionalidades comuns do cadastro.
- A inativação, reativação e remoção devem ser passíveis de auditoria.
- A remoção lógica deve preservar os dados necessários para rastreabilidade e histórico.

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
- Um vínculo ativo permite que a organização ativa apareça para seleção.
- Um vínculo inativo impede acesso à organização.
- Um vínculo inativo pode ser reativado sem criar um novo registro.
- Somente vínculos ativos com organizações ativas aparecem para seleção de contexto.
- A inativação do vínculo não inativa nem remove o usuário global.
- A inativação do usuário global impede seu acesso a todas as organizações.
- Uma organização pode não possuir usuários ativos, mas somente será operacional quando possuir ao menos um administrador interno válido.

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