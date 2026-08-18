# Cadastro de Empresa

**Campo:** Nome

- **Descrição:** Nome utilizado para identificar a empresa no sistema.
- **Tipo:** Texto
- **Preenchimento:** Manual  

**Regras:**  

- Não se aplica.  

**Validações:**

- Obrigatório.
- Máximo de 100 caracteres.
- Não aceitar valor vazio ou somente espaços.

## Funcionalidades

- Cadastrar empresa.
- Listar empresas.
- Editar empresa.
- Detalhar empresa.
- Alterar status da empresa.
- Remover empresa.

## Permissões

- **Cadastrar:**            `ACESSO_EMPRESA_CRIAR`
- **Listar:**               `ACESSO_EMPRESA_LISTAR`
- **Editar:**               `ACESSO_EMPRESA_EDITAR`
- **Detalhar:**             `ACESSO_EMPRESA_DETALHAR`
- **Status:**               `ACESSO_EMPRESA_STATUS`
- **Remover:**              `ACESSO_EMPRESA_EXCLUIR`

## Segurança

- O acesso às funcionalidades deve respeitar as permissões do usuário.
- A empresa deve pertencer à organização do usuário.
- Usuários não devem acessar empresas pertencentes a outras organizações.

## Relacionamentos

### Organização

- Uma empresa deve pertencer a uma única organização.
- Uma organização pode possuir nenhuma, uma ou várias empresas.

### Subsidiária

- Uma empresa pode possuir nenhuma, uma ou várias subsidiárias.
- Uma subsidiária deve pertencer a uma única empresa.