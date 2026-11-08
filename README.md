# Gestão de Estabelecimento

Aplicação desktop de gestão de estabelecimento, desenvolvida em Java com
Swing. O projecto organiza a interface, os controladores, as regras de negócio
e o acesso a dados em camadas, usando MySQL para persistência.

## Funcionalidades

- Autenticação de utilizadores e controlo de acesso por perfil.
- Gestão de clientes, fornecedores, funcionários, utilizadores e produtos.
- Gestão de estoque, vendas, itens vendidos, anulações e reembolsos.
- Painel inicial com indicadores e alertas.
- Relatórios, consultas SQL e exportação para PDF.
- Geração de documentos PDF e leitura de códigos de barras.
- Base de dados MySQL com dados iniciais, índices, auditoria, views,
  functions, procedures e cursores.

## Tecnologias e dependências

- Java (JDK, com `javac` e `java` disponíveis no `PATH`).
- MySQL 8.0; opcionalmente, Docker Compose para iniciar a base de dados.
- Bibliotecas fornecidas no directório `lib`: MySQL Connector/J, FlatLaf,
  Ikonli, jBCrypt, iText 5 e ZXing.

As dependências já estão incluídas no projecto; não é necessário um gestor de
dependências para compilar.

## Estrutura do projecto

| Caminho | Conteúdo |
| --- | --- |
| `src/App.java` | Ponto de entrada da aplicação |
| `src/view` | Interface gráfica Swing |
| `src/controller` | Controladores que ligam a interface aos casos de uso |
| `src/model/bo` | Regras de negócio |
| `src/model/dao` | Acesso JDBC e consultas à base de dados |
| `src/model/vo` | Entidades e objectos de valor |
| `src/infra` | Configuração e utilitários de infraestrutura da base de dados |
| `src/util` | Validadores, tarefas assíncronas e geração de documentos |
| `src/testes` | Testes e verificações, incluindo testes que dependem do MySQL |
| `docker/sql` | Scripts de criação, configuração e dados iniciais da base |
| `docs` | Diagramas e documentação do modelo |
| `lib` | Bibliotecas Java utilizadas pelo projecto |

## Preparar a base de dados

É necessário ter Docker Desktop com o comando `docker compose` disponível. Na
raiz do projecto, inicie os serviços:

```powershell
docker compose -f docker\docker-compose.yml up -d
```

O serviço MySQL fica disponível na porta `3306`; o Adminer, para administração
da base de dados, fica em <http://localhost:8080>. No Adminer, use o servidor
`db`, a base de dados `gestao` e as credenciais de desenvolvimento `gestao` /
`gestao`.

Na primeira inicialização de um volume MySQL vazio, o Docker executa os scripts
de `docker/sql` pela ordem dos nomes: schema, restrições, índices, dados
iniciais, triggers, functions, procedures, views e cursores. O volume
`mysql_data` preserva os dados entre reinicializações; por isso, os scripts de
inicialização não são novamente executados quando a base já foi criada.

O `docker-compose.yml` contém credenciais padrão destinadas a desenvolvimento
local. Altere-as antes de utilizar o sistema num ambiente partilhado ou de
produção.

## Configuração da ligação JDBC

Por padrão, a aplicação liga-se à base `gestao` em
`jdbc:mysql://localhost:3306/gestao`, usando o utilizador e a palavra-passe
`gestao`. Pode substituir cada valor com uma propriedade Java ou uma variável
de ambiente:

| Propriedade Java | Variável de ambiente |
| --- | --- |
| `gestao.db.url` | `GESTAO_DB_URL` |
| `gestao.db.username` | `GESTAO_DB_USERNAME` |
| `gestao.db.password` | `GESTAO_DB_PASSWORD` |

As propriedades Java têm precedência sobre as variáveis de ambiente. Por
exemplo, para definir valores na sessão actual do PowerShell:

```powershell
$env:GESTAO_DB_URL = "jdbc:mysql://localhost:3306/gestao?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:GESTAO_DB_USERNAME = "gestao"
$env:GESTAO_DB_PASSWORD = "gestao"
```

## Compilar e executar no Windows

Com o MySQL em execução e o terminal aberto na raiz do projecto, compile todos
os ficheiros Java e inicie a aplicação:

```powershell
New-Item -ItemType Directory -Force bin | Out-Null
$fontes = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp "lib\*" -d bin $fontes
if ($LASTEXITCODE -ne 0) { throw "A compilação falhou." }
java -cp "bin;lib\*" App
```

A aplicação abre a janela de autenticação. É necessária uma sessão gráfica
para utilizar a interface Swing.

## Testes

As verificações encontram-se em `src/testes`. Algumas são testes de integração
e precisam de uma base MySQL disponível e configurada. Os testes de interface
Swing também requerem um ambiente gráfico.
