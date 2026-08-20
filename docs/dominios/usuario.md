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

- [x] Cadastrar nova identidade de usuário.
- [x] Vincular automaticamente o novo usuário à organização atual.
- [x] Listar usuários ativos vinculados à organização atual com paginação.
- [x] Pesquisar usuários da organização atual por e-mail.
- [x] Detalhar usuário ativo vinculado à organização atual.
- [x] Remover o acesso do usuário à organização por inativação do vínculo.
- [x] Preservar a identidade global e os acessos do usuário em outras organizações.
- [x] Gerenciar perfis vinculados ao usuário por tela própria de Usuário x Perfil.
- [x] Gerenciar empresas e subsidiárias acessíveis ao usuário por telas próprias.
- [x] Controlar botões e rotas do frontend conforme as permissões do usuário logado.
- [ ] Disponibilizar futuramente a alteração do próprio e-mail em Minha Conta.
- [ ] Disponibilizar futuramente a alteração da própria senha em Minha Conta.
- [ ] Substituir futuramente a senha inicial definida pelo administrador por um fluxo de convite.

## Permissões

- **Consultar/Listar:** `ACESSO_USUARIO_LISTAR`
- **Cadastrar:** `ACESSO_USUARIO_CRIAR`
- **Detalhar:** `ACESSO_USUARIO_DETALHAR`
- **Remover acesso à organização:** `ACESSO_USUARIO_EXCLUIR`
- **Gerenciar perfis do usuário:** `ACESSO_USUARIO_PERFIL_LISTAR`
- **Gerenciar empresas do usuário:** `ACESSO_USUARIO_EMPRESA_LISTAR`
- **Gerenciar subsidiárias do usuário:** `ACESSO_USUARIO_SUBSIDIARIA_LISTAR`