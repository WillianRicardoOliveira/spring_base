# Cadastro de Organização
**Campo:** Nome
- **Descrição:** Nome utilizado para identificar a organização no sistema.
- **Tipo:** Texto
- **Preenchimento:** Manual  
**Regras:**  
- Não se aplica.  
**Validações:**
- Obrigatório.
- Máximo de 100 caracteres.
- Não aceitar valor vazio ou somente espaços.
## Funcionalidades
- Cadastrar organização.
- Listar organizações.
- Editar organização.
- Detalhar organização.
- Alterar status da organização.
- Remover organização.
## Permissões
- **Cadastrar:**            `ACESSO_ORGANIZACAO_CRIAR`
- **Listar:**               `ACESSO_ORGANIZACAO_LISTAR`
- **Editar:**               `ACESSO_ORGANIZACAO_EDITAR`
- **Detalhar:**             `ACESSO_ORGANIZACAO_DETALHAR`
- **Status:**               `ACESSO_ORGANIZACAO_STATUS`
- **Remover:**              `ACESSO_ORGANIZACAO_EXCLUIR`
## Segurança
- As ações administrativas de organização somente podem ser executadas por administradores da plataforma que possuam a respectiva permissão.
- As permissões de organização são exclusivas da administração da plataforma e não podem ser atribuídas a perfis internos de uma organização.
- Uma organização inativa não permite acesso operacional aos seus dados e recursos por usuários vinculados, permanecendo disponível para administração pelo administrador da plataforma que possua as respectivas permissões.
- O usuário pode consultar as organizações ativas às quais possui acesso exclusivamente para identificação e seleção de contexto, sem acesso às funcionalidades administrativas do cadastro de organização.
- A remoção de uma organização é lógica e não realiza exclusão física dos dados.
- Uma organização removida não pode ser utilizada ou selecionada por seus usuários, permanecendo apenas para fins de histórico e auditoria.
- A inativação ou remoção da organização não altera o status nem remove empresas, usuários ou outros registros vinculados. Os relacionamentos e dados devem ser preservados.
- A alteração de status permite somente as transições entre ativo e inativo. A alteração para removido ocorre exclusivamente pela funcionalidade de remoção.
- A consulta de organizações para seleção de contexto não depende da permissão `ACESSO_ORGANIZACAO_LISTAR`.
## Relacionamentos
### Empresa
- Uma organização pode não possuir empresas ou possuir uma ou várias empresas.
- Uma empresa pertence a uma única organização.
### Usuário
- Uma organização pode não possuir usuários ou possuir um ou vários usuários.
- Um usuário pode não estar vinculado a nenhuma organização ou estar vinculado a uma ou várias organizações.
- O usuário somente pode acessar dados das organizações ativas às quais possui acesso.
- O administrador da plataforma não depende desse vínculo para administrar organizações.