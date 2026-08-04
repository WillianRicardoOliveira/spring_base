# Cadastro de Usuário

**Campo:** Nome

- **Descrição:** Nome utilizado para identificar a empresa no sistema.


- **Tipo:** Texto


- **Obrigatório:** Sim


- **Preenchimento:** Manual

  
**Regras:**
  
- [ x ] Regra implementada
  
**Validações:**

- [ x ] Validação implementada

## 

**Campo:** E-mail

- **Descrição:** E-mail utilizado para identificar o usuário e realizar autenticação no sistema.


- **Tipo:** Texto


- **Obrigatório:** Sim


- **Preenchimento:** Manual


**Regras:**

- [x] Deve ser único no sistema.
- [x] Deve ser normalizado para letras minúsculas e sem espaços nas extremidades.
- [x] É usado como identificador de login.
- [x] Pode ser utilizado como filtro na listagem.
- [x] Pode ser alterado apenas em usuários ativos.

**Validações:**

- [x] Campo obrigatório.
- [x] Deve possuir formato válido de e-mail.
- [x] Não pode duplicar e-mail de outro usuário.

## 

**Campo:** Senha

- **Descrição:** Senha de acesso do usuário ao sistema.


- **Tipo:** Texto


- **Obrigatório:** Sim


- **Preenchimento:** Manual


**Regras:**

- [x] Deve ser armazenada criptografada.
- [x] Deve seguir política de senha forte.
- [x] Na edição comum do usuário, a senha não é alterada.
- [x] A alteração de senha revoga as sessões ativas do usuário.

**Validações:**

- [x] Campo obrigatório no cadastro.
- [x] Campo obrigatório na alteração de senha.
- [x] Mínimo de 8 caracteres.
- [x] Deve conter letra maiúscula.
- [x] Deve conter letra minúscula.
- [x] Deve conter número.
- [x] Deve conter caractere especial.






















## Funcionalidades

- [x] Cadastrar usuário.
- [x] Listar usuários ativos com paginação.
- [x] Pesquisar usuários por e-mail.
- [x] Detalhar usuário.
- [x] Editar e-mail do usuário.
- [x] Alterar senha do usuário.
- [x] Remover usuário por remoção lógica.
- [x] Revogar sessões ao alterar senha.
- [x] Revogar sessões ao remover usuário.
- [x] Gerenciar perfis vinculados ao usuário por tela própria de Usuário x Perfil.
- [x] Controlar botões e rotas do frontend conforme permissões do usuário logado.

## Permissões


- **Consultar/Listar:** `ACESSO_USUARIO_LISTAR`
- **Cadastrar:** `ACESSO_USUARIO_CRIAR`
- **Detalhar:** `ACESSO_USUARIO_DETALHAR`
- **Editar:** `ACESSO_USUARIO_EDITAR`
- **Remover:** `ACESSO_USUARIO_EXCLUIR`
- **Alterar senha:** `ACESSO_USUARIO_SENHA_EDITAR`
- **Gerenciar perfis do usuário:** `ACESSO_USUARIO_PERFIL_LISTAR`