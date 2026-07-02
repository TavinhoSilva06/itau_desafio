# ITAUtask - API de Transações e Estatísticas

## Sobre o Projeto

Este projeto foi desenvolvido como solução para o desafio de programação do Itaú.

A aplicação disponibiliza uma API REST capaz de:

* Registrar transações financeiras;
* Remover todas as transações registradas;
* Calcular estatísticas das transações realizadas nos últimos N segundos;
* Expor documentação automática da API;
* Disponibilizar endpoint de monitoramento (health check);
* Executar testes automatizados;
* Executar em container Docker.

---

## Tecnologias Utilizadas

* Java 21
* Spring Boot 3.5.0
* Maven
* Lombok
* Spring Validation
* Spring Actuator
* Springdoc OpenAPI (Swagger)
* JUnit 5
* Mockito
* Docker
* Render

---

## Estrutura do Projeto

```text
src
 ├── config
 ├── controller
 ├── dto
 ├── exception
 ├── model
 ├── repository
 ├── service
 └── resources
```

---

## Como Executar Localmente

### Clonar o Repositório

```bash
git clone https://github.com/SEU-USUARIO/SEU-REPOSITORIO.git
```

### Entrar na Pasta

```bash
cd ITAUtask
```

### Compilar o Projeto

```bash
mvn clean install
```

### Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

---

## Configuração

A aplicacao usa variaveis de ambiente para configurar banco, porta e intervalo inicial sem alterar o codigo:

```properties
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/itautask}
server.port=${PORT:8080}
estatistica.intervalo-segundos=${ESTATISTICA_INTERVALO_SEGUNDOS:3600}
```

Os valores depois de `:` sao apenas fallbacks locais. Quando a variavel existir no ambiente, como no Render, o Spring usa o valor da variavel.

---

## Endpoints

### Registrar Transação

```http
POST /transacao
```

Exemplo de requisição:

```json
{
  "valor": 100.50,
  "dataHora": "2026-06-10T15:30:00-03:00"
}
```

Resposta:

```http
201 Created
```

---

### Limpar Transações

```http
DELETE /transacao
```

Resposta:

```http
200 OK
```

---

### Consultar Estatísticas

```http
GET /estatistica
```

Exemplo de resposta:

```json
{
  "count": 2,
  "sum": 300.50,
  "avg": 150.25,
  "min": 100.00,
  "max": 200.50
}
```

---

### Consultar Intervalo Atual

```http
GET /estatistica/intervalo
```

Exemplo de resposta:

```json
{
  "intervaloSegundos": 3600
}
```

---

### Atualizar Intervalo

```http
PUT /estatistica/intervalo
```

Exemplo de requisicao:

```json
{
  "intervaloSegundos": 120
}
```

Resposta:

```json
{
  "intervaloSegundos": 120
}
```

Cada atualizacao cria um novo registro na collection `estatistica_intervalos` e desativa o intervalo anterior.

---

### Consultar Historico de Intervalos

```http
GET /estatistica/intervalo/historico
```

Exemplo de resposta:

```json
[
  {
    "intervaloSegundos": 3600,
    "dataHoraAlteracao": "2026-06-25T03:00:00Z",
    "ativo": false
  },
  {
    "intervaloSegundos": 120,
    "dataHoraAlteracao": "2026-06-25T03:10:00Z",
    "ativo": true
  }
]
```

---

## Documentação da API (Swagger)

Após iniciar a aplicação, a documentação pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

No ambiente publicado:

```text
https://itau-desafio.onrender.com/swagger-ui/index.html
```

---

## Health Check

A aplicação disponibiliza um endpoint para monitoramento de saúde:

```http
GET /actuator/health
```

Exemplo de resposta:

```json
{
  "status": "UP"
}
```

---

## Logs

A aplicação registra informações importantes durante sua execução, como:

* Recebimento de transações;
* Horário recebido na requisição;
* Horário atual do servidor;
* Limpeza de transações;
* Cálculo das estatísticas;
* Tempo gasto para cálculo;
* Tratamento de exceções.

Esses logs auxiliam na identificação e resolução de problemas.

---

## Testes Automatizados

Para executar todos os testes:

```bash
mvn test
```

Os testes cobrem:

* Services;
* Controllers;
* Cenários de sucesso;
* Cenários de erro;
* Validações de negócio.

---

## Docker

### Executar API + MongoDB com Docker Compose

Para subir a aplicaÃ§Ã£o e o MongoDB localmente:

```bash
docker compose up --build
```

A aplicaÃ§Ã£o ficarÃ¡ disponÃ­vel em:

```text
http://localhost:8080
```

O MongoDB ficarÃ¡ disponÃ­vel para ferramentas locais em:

```text
mongodb://localhost:27017/itautask
```

Para parar os containers:

```bash
docker compose down
```

Para parar os containers e apagar os dados locais do MongoDB:

```bash
docker compose down -v
```

### Rodar a API fora do Docker

Se quiser rodar a aplicaÃ§Ã£o via Maven ou pela IDE, suba somente o MongoDB com o Compose:

```bash
docker compose up mongodb
```

A configuraÃ§Ã£o padrÃ£o usa:

```properties
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/itautask}
```

Dentro do Docker Compose, a variÃ¡vel `SPRING_DATA_MONGODB_URI` aponta a API para:

```text
mongodb://mongodb:27017/itautask
```

### Gerar Imagem Manualmente

```bash
docker build -t itautask .
```

### Executar Container Manualmente

```bash
docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/itautask -e ESTATISTICA_INTERVALO_SEGUNDOS=3600 -e PORT=8080 itautask
```

---

## Variaveis de ambiente, Docker Compose, Render e Atlas

A aplicacao usa variaveis de ambiente para funcionar localmente e em producao sem alterar o codigo:

```properties
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/itautask}
server.port=${PORT:8080}
estatistica.intervalo-segundos=${ESTATISTICA_INTERVALO_SEGUNDOS:3600}
```

Variaveis suportadas:

| Variavel | Uso | Observacao |
| --- | --- | --- |
| `SPRING_DATA_MONGODB_URI` | URL de conexao do MongoDB | Fallback local: `mongodb://localhost:27017/itautask`. Use MongoDB Atlas em producao. |
| `PORT` | Porta HTTP da aplicacao | Fallback local: `8080`. No Render, normalmente e definida automaticamente. |
| `ESTATISTICA_INTERVALO_SEGUNDOS` | Janela inicial usada no calculo das estatisticas | Fallback local: `3600`. Pode ser alterada em runtime pelo endpoint `PUT /estatistica/intervalo`. |

Use `.env.example` como modelo para criar um `.env` local. O arquivo `.env` real nao deve ser versionado, principalmente quando contiver usuario, senha ou URI do MongoDB Atlas.

Para criar o `.env` local:

```bash
cp .env .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Para rodar via Maven no PowerShell:

```powershell
$env:SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/itautask"
$env:ESTATISTICA_INTERVALO_SEGUNDOS="3600"
$env:PORT="8080"
.\mvnw spring-boot:run
```

Com Docker Compose, a API usa `mongodb://mongodb:27017/itautask` dentro da rede Docker e o banco local salva os dados no volume `mongodb_data`. `docker compose down` para os containers sem apagar os dados; `docker compose down -v` remove tambem o volume e apaga o MongoDB local.

No Render, crie um Web Service usando o `Dockerfile` e configure:

```env
SPRING_DATA_MONGODB_URI=mongodb+srv://USUARIO:SENHA@CLUSTER.mongodb.net/itautask?retryWrites=true&w=majority
ESTATISTICA_INTERVALO_SEGUNDOS=3600
```

O Render define `PORT` automaticamente. No MongoDB Atlas, crie um usuario de banco, libere acesso de rede para o Render e use a connection string `mongodb+srv` na variavel `SPRING_DATA_MONGODB_URI`.

Depois de alterar uma variavel no painel do Render, use `Save and deploy` ou `Save, rebuild, and deploy`. Se usar `Save only`, o servico continua rodando com os valores antigos ate o proximo deploy. O arquivo `render.example.yaml` e apenas uma documentacao de exemplo para Blueprint; o servico publicado foi configurado manualmente no Dashboard do Render.

---

## Deploy

A aplicação encontra-se publicada no Render:

```text
https://itau-desafio.onrender.com
```

---

## Autor

Projeto desenvolvido por Tavinho Augusto como solução para o desafio técnico Itaú.
