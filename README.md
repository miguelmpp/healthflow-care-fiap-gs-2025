# HealthFlow Care – API de Bem-Estar no Futuro do Trabalho

Trabalho desenvolvido para a disciplina **Arquitetura Orientada a Serviços e Web Services (SOA & WebServices)**, alinhado ao tema **“O Futuro do Trabalho”**.

A **HealthFlow Care** é uma API REST que monitora **hábitos de bem-estar de colaboradores** em cenários de trabalho **remoto, híbrido e presencial**, registrando:

- pausas ao longo do dia,
- sono,
- exercícios físicos,
- foco,
- indicadores de cansaço/estresse,

e gerando um **resumo de bem-estar** com **recomendações enriquecidas por uma API externa REST**.

---

## 👥 Integrantes do Grupo

- **Matheus Farias – RM 554254**
- **Miguel Parrado – RM 554007**

---

## 🎯 Objetivo do Projeto

O objetivo da HealthFlow Care é oferecer um **serviço independente e reutilizável** (orientado a serviços) para:

- **Cadastrar colaboradores (usuários)** com informações básicas e endereço;
- **Registrar hábitos de saúde/rotina** (pausas, sono, exercício etc.);
- **Calcular um resumo de bem-estar** por colaborador (minutos por tipo de hábito, média de estresse e cansaço);
- **Consumir uma API REST externa** para sugerir uma **dica de bem-estar** complementar;
- Disponibilizar tudo isso através de **APIs RESTful**, em JSON, prontas para serem consumidas por aplicações web/mobile ou outros serviços.

Esse serviço pode ser integrado a plataformas corporativas, dashboards de RH, aplicações mobile de saúde ocupacional, entre outros.

---

## 🏛 Arquitetura e Tecnologias

### Stack Tecnológica

- **Linguagem:** Java 21  
- **Framework:** Spring Boot 3.5.8  
- **Módulos Spring:**
  - Spring Web (APIs REST)
  - Spring Data JPA (persistência)
  - Bean Validation (jakarta.validation)
- **Banco de Dados:** H2 (em memória, para desenvolvimento/testes)
- **Migrações:** Flyway (pasta `db/migration`)
- **Cliente HTTP:** RestTemplate (para consumo de API externa)
- **Outros:**
  - Lombok (getters, constructors etc.)
  - H2 Console (`/h2-console`)
  - CORS liberado para facilitar integração com front-ends

### Organização em Camadas (SOA / boas práticas)

O projeto segue uma organização por camadas, favorecendo **separação de responsabilidades** e **reutilização de serviços**:

- `controller`  
  Camada de **apresentação/serviço** (Web): expõe os endpoints REST (`/usuarios`, `/habitos-saude`, `/health-check`, `/usuarios/{id}/resumo-bem-estar`, `/usuarios/{id}/recomendacoes`).

- `domain`
  - `usuario` – Entity `Usuario`, DTOs (`DadosCadastroUsuario`, `DadosAtualizacaoUsuario`, `DadosListagemUsuario`), enum `Genero`.
  - `endereco` – Value Object `Endereco` e DTO `DadosEndereco`.
  - `habito` – Entity `HabitoSaude`, DTOs de cadastro/listagem/atualização, enum `TipoHabito`, DTOs de resumo e recomendações (`ResumoBemEstarUsuario`, `RecomendacaoBemEstarUsuario`).

- `repository`  
  Repositórios JPA (`UsuarioRepository`, `HabitoSaudeRepository`) – camada de **dados**.

- `infra`
  - `exception` – tratador global de erros (`TratadorDeErros`), DTO de erro de validação, exception customizada `UsuarioNaoEncontradoException`.
  - `config` – configuração de `RestTemplate` e CORS.
  - `external` – DTO para resposta da API Advice Slip.

Essa estrutura evidencia:

- **Arquitetura orientada a serviços** (módulos independentes e reutilizáveis);
- **Separação de camadas** (apresentação, domínio, dados, infraestrutura).

---

## 🗄 Modelo de Dados (Resumo)

### Entidade `Usuario`

Campos principais:

- `id` (Long) – chave primária
- `ativo` (Boolean) – soft delete
- `nome` (String)
- `email` (String) – validado com `@Email`
- `telefone` (String)
- `dataNascimento` (LocalDate)
- `genero` (Enum `MASCULINO`, `FEMININO`, `OUTRO`, etc.)
- `endereco` (Value Object `Endereco`):
  - logradouro, número, complemento, bairro, cidade, UF, CEP

### Entidade `HabitoSaude`

Campos principais:

- `id` (Long)
- `ativo` (Boolean)
- `usuario` (ManyToOne → `Usuario`)
- `tipoHabito` (Enum `TipoHabito`):
  - `PAUSA`, `SONO`, `EXERCICIO`, `ALIMENTACAO`, `FOCUS`
- `dataRegistro` (LocalDate)
- `duracaoMinutos` (Integer)
- `nivelCansaco` (Integer)
- `nivelEstresse` (Integer)
- `observacoes` (String)

---

## 🌐 Integração com API Externa (REST)

Para enriquecer o cenário do **Futuro do Trabalho** com recomendações de bem-estar, a API consome um serviço REST público:

- **API:** Advice Slip  
- **Endpoint:** `https://api.adviceslip.com/advice`  
- **Formato:** JSON:

  ```json
  {
    "slip": {
      "slip_id": "2",
      "advice": "Alguma dica em inglês..."
    }
  }

Uso na aplicação:

* No endpoint `GET /usuarios/{usuarioId}/recomendacoes`, a API:

  1. Calcula o `ResumoBemEstarUsuario` (minutos por tipo de hábito e médias de estresse/cansaço).
  2. Gera uma mensagem textual (`mensagemResumo`) com base nesses indicadores.
  3. Faz uma requisição à API externa via `RestTemplate`.
  4. Combina tudo na resposta `RecomendacaoBemEstarUsuario`, contendo:

     * `resumo`
     * `mensagemResumo`
     * `dicaExterna` (advise da API ou mensagem de fallback em caso de erro de conexão).

---

## 🧱 Banco de Dados e Migrações (Flyway)

* Banco **H2 em memória**, URL: `jdbc:h2:mem:healthflowdb`
* Controle de esquema via **Flyway**, com scripts versionados em:
  `src/main/resources/db/migration`

Exemplos (nomes ilustrativos):

* `V1__create-table-usuarios.sql` – criação da tabela `usuarios`
* `V2__create-table-habitos-saude.sql` – criação da tabela `habitos_saude`

Isso garante:

* Reprodutibilidade do ambiente;
* Controle de evolução do modelo de dados;
* Boa aderência ao critério de **migrações versionadas** da disciplina.

---

## 🛡 Segurança, Validação e Tratamento de Erros

### Validações de entrada

* DTOs de cadastro/atualização usam **Bean Validation**, por exemplo:

  * `@NotBlank`, `@NotNull`, `@Email`, `@Pattern` (CEP, UF etc.)
* Isso protege contra:

  * dados obrigatórios ausentes,
  * formatos inválidos,
  * entrada “suja” que poderia gerar inconsistências ou falhas.

### Tratador global de erros (`@RestControllerAdvice`)

Classe `TratadorDeErros` trata:

* `MethodArgumentNotValidException` → **400 Bad Request**

  * Retorna uma lista de `DadosErroValidacao` com:

    * `campo`
    * `mensagem`
* `UsuarioNaoEncontradoException` → **404 Not Found**

  * Retorna JSON com: `timestamp`, `status`, `erro`, `mensagem`.
* `EntityNotFoundException` (JPA) → **404 Not Found**.

### Boas práticas adicionais

* Uso de JPA/Hibernate em vez de concatenar SQL manualmente;
* DTOs (`record`) isolando o que entra/sai da API;
* CORS configurado para permitir acesso de front-ends em outros domínios.

---

## 🔗 Principais Endpoints

### Health Check

* **GET** `/health-check`
  Verifica se a API está de pé.

```txt
Resposta: "HealthFlow Care API está online e saudável!"
```

---

### Usuários

#### 1. Criar usuário

* **POST** `/usuarios`

Exemplo de body:

```json
{
  "nome": "Maria Silva",
  "email": "maria.silva@example.com",
  "telefone": "11999999999",
  "dataNascimento": "1995-05-10",
  "genero": "FEMININO",
  "endereco": {
    "logradouro": "Rua Exemplo",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01001000"
  }
}
```

#### 2. Listar usuários

* **GET** `/usuarios`

  * Página default: `size=10`, ordenado por `nome`.

#### 3. Atualizar usuário

* **PUT** `/usuarios`

Body (exemplo):

```json
{
  "id": 1,
  "nome": "Maria S. Silva",
  "telefone": "11988887777",
  "endereco": {
    "logradouro": "Rua Nova",
    "numero": "500",
    "complemento": null,
    "bairro": "Jardins",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01415000"
  }
}
```

#### 4. Remover usuário (soft delete)

* **DELETE** `/usuarios/{id}`

Marca `ativo = false` e passa a ser ignorado nas listagens.

---

### Hábitos de Saúde

#### 1. Criar hábito

* **POST** `/habitos-saude`

Exemplo de body:

```json
{
  "usuarioId": 1,
  "tipoHabito": "PAUSA",
  "dataRegistro": "2025-11-24",
  "duracaoMinutos": 15,
  "nivelCansaco": 6,
  "nivelEstresse": 4,
  "observacoes": "Pausa rápida para caminhar e beber água."
}
```

#### 2. Listar todos os hábitos

* **GET** `/habitos-saude`

#### 3. Listar hábitos de um usuário

* **GET** `/usuarios/{usuarioId}/habitos-saude`

#### 4. Atualizar hábito

* **PUT** `/habitos-saude`

Body (exemplo):

```json
{
  "id": 1,
  "duracaoMinutos": 20,
  "nivelCansaco": 5,
  "nivelEstresse": 3,
  "observacoes": "Pausa um pouco mais longa."
}
```

#### 5. Remover hábito (soft delete)

* **DELETE** `/habitos-saude/{id}`

---

### Resumo de Bem-Estar

#### GET `/usuarios/{usuarioId}/resumo-bem-estar`

Retorna:

```json
{
  "usuarioId": 1,
  "nome": "Maria Silva",
  "totalHabitos": 3,
  "totalMinutosPausa": 30,
  "totalMinutosSono": 480,
  "totalMinutosExercicio": 45,
  "totalMinutosAlimentacao": 0,
  "totalMinutosFocus": 0,
  "mediaEstresse": 4.5,
  "mediaCansaco": 6.0
}
```

---

### Recomendações com API Externa

#### GET `/usuarios/{usuarioId}/recomendacoes`

Combina:

* resumo de bem-estar,
* mensagem interpretando o resumo,
* dica vinda da API Advice Slip (ou mensagem de erro amigável).

Exemplo:

```json
{
  "resumo": {
    "usuarioId": 1,
    "nome": "Maria Silva",
    "totalHabitos": 1,
    "totalMinutosPausa": 15,
    "totalMinutosSono": 0,
    "totalMinutosExercicio": 0,
    "totalMinutosAlimentacao": 0,
    "totalMinutosFocus": 0,
    "mediaEstresse": 4.0,
    "mediaCansaco": 6.0
  },
  "mensagemResumo": "Com base em 1 hábitos registrados, o nível médio de estresse está em 4.0 de 10 e o cansaço médio em 6.0 de 10. Busque equilibrar sono, pausas e exercícios ao longo da semana.",
  "dicaExterna": "Falha ao se conectar ao serviço externo de dicas. Verifique sua conexão e tente novamente."
}
```

Quando a API externa responde corretamente, `dicaExterna` traz um conselho em inglês.

---

## ▶️ Como Rodar o Projeto

### Pré-requisitos

* JDK 21 instalado
* Maven configurado (ou uso do Maven embutido do IntelliJ)
* IntelliJ IDEA (Ultimate) – projeto Spring Boot

### Passos

1. **Clonar o repositório**

   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd healthflow-care
   ```

2. **Abrir no IntelliJ**

   * `File > Open...` → selecione a pasta do projeto.
   * Aguarde o download das dependências Maven.

3. **Rodar a aplicação**

   * Localize a classe `HealthFlowCareTrackerApplication`.
   * Clique em **Run**.

4. **Testar os endpoints**

   * Usar Postman, Insomnia ou outro cliente HTTP.
   * Base URL: `http://localhost:8080`
   * Endpoints: conforme descrito acima.

5. **Acessar H2 Console (opcional)**

   * `http://localhost:8080/h2-console`
   * JDBC URL: `jdbc:h2:mem:healthflowdb`
   * Usuário padrão: `SA` (sem senha, se assim configurado em `application.properties`).



::contentReference[oaicite:0]{index=0}
```
