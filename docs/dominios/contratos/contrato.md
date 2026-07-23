# Cadastro de Contrato

## Objetivo

Registrar e controlar os contratos de compra firmados com fornecedores, permitindo acompanhar sua vigência, condições comerciais, valores contratados, valores utilizados e documentos relacionados.

## Identificação

- **Número do contrato:**

Código único utilizado para identificar e consultar o contrato.
  
**Obrigatório:** 🔴 Sim<br>

**Preenchimento:** Automático<br>

**Exibição:** Exibido no cadastro e nas consultas<br>

**Editável:** Não
  
**Regras:**<br>
✓ Gerado automaticamente pelo sistema no momento do cadastro.<br>
✓ Deve possuir o formato `CTR-000001`.<br>
✓ A parte numérica deve possuir seis dígitos, preenchidos com zeros à esquerda.<br>
✓ A numeração deve ser sequencial e única para todo o sistema, independentemente da subsidiária.<br>
✓ O número não pode ser alterado após a criação do contrato.<br>
✓ Números de contratos removidos não podem ser reutilizados.<br>
✓ Deve ser utilizado nas pesquisas, listagens e referências ao contrato.
  
**Validações:**<br>
✓ Não permitir dois contratos com o mesmo número.<br>
✓ Não permitir a gravação do contrato sem a geração do número.

- **Subsidiária:**

Empresa do grupo responsável pelo contrato.
  
**Obrigatório:** 🔴 Sim
    
**Preenchimento:** Automático
  
**Exibição:** Não exibido no cadastro
  
**Editável:** Não
  
**Regras:**<br>
  ✓ Definida automaticamente com base na subsidiária vinculada ao usuário.<br>
  ✓ Armazenada no contrato no momento do cadastro.<br>
  ✓ Alterações posteriores na subsidiária do usuário não modificam contratos existentes.<br>
  ✓ Pode ser apresentada em consultas, relatórios e auditorias.
  
- **Fornecedor:**

Fornecedor com o qual o contrato foi firmado.

**Origem:** Cadastro de fornecedor<br>

**Obrigatório:** 🔴 Sim<br>

**Preenchimento:** Seleção<br>

**Exibição:** Exibido no cadastro e nas consultas<br>

**Editável:** Condicional

**Regras:**<br>
✓ Deve ser selecionado a partir do cadastro de fornecedor.<br>
✓ Somente fornecedores ativos podem ser selecionados.<br>
✓ O contrato deve possuir apenas um fornecedor.<br>
✓ O fornecedor deve estar vinculado à subsidiária do contrato.<br>
✓ Pode ser alterado enquanto o contrato não possuir Pedidos de Compra vinculados.<br>
✓ Não pode ser alterado depois que o contrato possuir Pedidos de Compra vinculados.

**Validações:**<br>
✓ Não permitir a gravação do contrato sem um fornecedor.<br>
✓ Não permitir a seleção de fornecedor inativo.<br>
✓ Não permitir fornecedor incompatível com a subsidiária do contrato.<br>
✓ Não permitir a alteração quando existirem Pedidos de Compra vinculados.























- **Centro de Custo:**

Identifica a área ou unidade à qual os custos do contrato estão vinculados para fins de controle gerencial.

**Obrigatório:** 🔴 Sim<br>
**Preenchimento:** Automático<br>
**Origem:** Configuração do processo de compra internacional<br>
**Exibição:** Não exibido no cadastro<br>
**Editável:** Não

**Regras:**<br>
✓ Definido automaticamente como Centro de Custo padrão do processo de compra internacional.<br>
✓ Atualmente, o Centro de Custo padrão é Supply.<br>
✓ Armazenado no contrato no momento do cadastro.<br>
✓ Alterações posteriores na configuração não modificam contratos existentes.<br>
✓ Utilizado como padrão nos Pedidos de Compra vinculados ao contrato.<br>
✓ Pode ser apresentado em consultas, relatórios e auditorias.

**Validações:**<br>
✓ Não permitir a gravação do contrato quando não houver um Centro de Custo padrão configurado.<br>
✓ Não permitir a utilização de um Centro de Custo inativo.
  
  
  
  
  
  
  

- **Localidade:** `Filial`
  
  Unidade ou filial à qual o contrato está vinculado.

- **Status:** `Em aberto` 
 
  Situação atual do contrato no sistema.
  
## Vigência e valores

- **Início da vigência:** `01/01/2026`  

  Data inicial em que o contrato pode ser utilizado.

- **Fim da vigência:** `31/12/2026`
  
  Data limite em que o contrato pode ser utilizado.

- **Moeda:** `USD - Dólar americano`
  
  Moeda negociada com o fornecedor.

- **Valor contratado:** `Calculado`
  
  Valor total dos itens previstos.

- **Valor utilizado:** `Calculado`
  
  Valor comprometido nos Pedidos de Compra.

- **Valor disponível:** `Calculado`
  
  Saldo disponível para novos Pedidos de Compra.
  
## Condições comerciais

- **Condição de pagamento:** `30 dias após a data de embarque`
  
  Prazo e evento de referência utilizados para calcular o vencimento das parcelas.

- **Incoterm:** `CIF`
  
  Condição comercial que define as responsabilidades, os custos e os riscos logísticos assumidos por cada parte.

- **Observações:** `Mapeamento dos dados`  

  Informações complementares relevantes para o contrato.
  
  **Obrigatório:** ⚪ Não
  
## Documentos

- **Contrato:** `OneDrive`
  
  Documento formal firmado entre a empresa e o fornecedor.

- **Proforma Invoice:** `OneDrive`
  
  Documento preliminar enviado pelo fornecedor com as condições comerciais da operação.
  
## Funcionalidades previstas

- Cadastrar contratos.
- Atualizar contratos.
- Remover contratos.
- Listar contratos.

## Funcionalidades disponíveis

-

## Mapeamento técnico

-
