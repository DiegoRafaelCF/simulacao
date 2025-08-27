# Spring API --- Guia de Uso (Maven, Docker, Swagger, Cache)

## Requisitos

-   Java 17+ (para rodar localmente sem Docker)
-   Maven 3.9+
-   Docker Desktop (Windows/macOS) ou Docker Engine (Linux)

### Windows (passo-a-passo)

1.  Instale o **Docker Desktop**:
    https://www.docker.com/products/docker-desktop/

2. Abra o software e Habilite ou atualize o **WSL2** quando for solicitado.

3.  Após instalar, abra **PowerShell** e verifique:

    ``` powershell
    docker --version
    mvn -v
    ```

------------------------------------------------------------------------

## Estrutura de Pastas (resumo)

    src/
      main/
        java/...           # código fonte
        resources/
          application.properties  # configs (dev/prod)
      test/
        java/...           # testes de integração
        resources/
          application-test.properties  # configs (dev/prod)
    pom.xml
    Dockerfile
    docker-compose.yml
    .dockerignore
    .gitattributes
    .gitignore
    mvnw
    mvnw.cmd

-   **Contexto da API**: `/api/v1`
-   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
-   **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

------------------------------------------------------------------------

## Rodando localmente (sem Docker)

### 1) Executar a aplicação

``` bash
mvn spring-boot:run
```

### 2) Rodar os testes

``` bash
mvn clean test
```

------------------------------------------------------------------------

## Build do JAR (local)

``` bash
mvn -DskipTests clean package
```

O artefato ficará em `target/*.jar`.

------------------------------------------------------------------------

## Rodar com Docker (build e run)

### 1) Build da imagem

#### PowerShell (Windows) / bash (Linux/macOS)

``` bash
docker build -t spring-api:latest .
```

### 2) Subir o container

``` bash
docker run -d --name spring-api -p 8080:8080   -e SPRING_PROFILES_ACTIVE=prod   spring-api:latest
```

-   A API responderá em: `http://localhost:8080/api/v1`.
-   Swagger: `http://localhost:8080/swagger-ui.html`.

### Parar/Remover

``` bash
docker stop spring-api && docker rm spring-api
```

------------------------------------------------------------------------

## Rodar com docker-compose

``` bash
docker compose up --build
```

Isso irá: - Buildar a imagem conforme o `Dockerfile`. - Subir o serviço
`api` expondo a porta **8080**.

Para derrubar:

``` bash
docker compose down
```

------------------------------------------------------------------------

## Swagger / OpenAPI

-   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
-   **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

------------------------------------------------------------------------

## Cache com Caffeine (como otimiza as buscas)

### Configuração usada

    spring.cache.type=caffeine
    spring.cache.cache-names=produtos
    spring.cache.caffeine.spec=maximumSize=10,expireAfterWrite=10m

### Como funciona

-   Anotação `@EnableCaching` ativa o mecanismo de cache do Spring.
-   `@Cacheable("produtos")` no repositório/serviço faz o Spring
    armazenar o **resultado** das chamadas com mesmas chaves de método.
-   Primeira chamada: consulta o **SQL Server externo**; o resultado é
    **cacheado**.
-   Próximas chamadas (até 10 minutos ou até o cache encher): retornam
    **direto da memória**, evitando round-trip e reduzindo latência.

-   **Tamanho/TTL**: ajuste `maximumSize` e `expireAfterWrite` conforme
    tráfego real.

------------------------------------------------------------------------

## Comandos úteis (resumo)

``` bash
# Build jar local
mvn -DskipTests clean package

# Docker (build/run)
docker build -t spring-api:latest .
docker run -d --name spring-api -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod spring-api:latest

# docker-compose
docker compose up --build

# Testes
mvn clean test
```
