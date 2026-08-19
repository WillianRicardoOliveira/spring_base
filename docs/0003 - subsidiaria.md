# Cadastro de Subsidiária

**Campo:** Empresa

- **Descrição:** Empresa à qual a subsidiária pertence.
- **Tipo:** Seleção
- **Preenchimento:** Manual  

**Regras:**  

- A subsidiária deve estar vinculada a uma empresa.
- A empresa selecionada deve pertencer à mesma organização do usuário.

**Validações:**

- Obrigatório.
- A empresa informada deve existir.
- A empresa deve pertencer à organização do usuário.

**Campo:** Nome

- **Descrição:** Nome utilizado para identificar a subsidiária no sistema.
- **Tipo:** Texto
- **Preenchimento:** Manual  

**Regras:**  

- Não se aplica.

**Validações:**

- Obrigatório.
- Máximo de 100 caracteres.
- Não aceitar valor vazio ou somente espaços.

## Funcionalidades

- Cadastrar subsidiária.
- Listar subsidiárias.
- Editar subsidiária.
- Detalhar subsidiária.
- Alterar status da subsidiária.
- Remover subsidiária.

## Permissões

- **Cadastrar:**            `ACESSO_SUBSIDIARIA_CRIAR`
- **Listar:**               `ACESSO_SUBSIDIARIA_LISTAR`
- **Editar:**               `ACESSO_SUBSIDIARIA_EDITAR`
- **Detalhar:**             `ACESSO_SUBSIDIARIA_DETALHAR`
- **Status:**               `ACESSO_SUBSIDIARIA_STATUS`
- **Remover:**              `ACESSO_SUBSIDIARIA_EXCLUIR`

## Segurança

- O acesso às funcionalidades deve respeitar as permissões do usuário.
- O usuário não pode acessar subsidiárias pertencentes a outras organizações.
- Toda consulta, alteração ou remoção deve validar a organização da subsidiária no backend.

## Relacionamentos

###

### 
