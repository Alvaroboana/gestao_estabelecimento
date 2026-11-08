# Gestao Estabelecimento

Base inicial do projeto para a arquitetura MVC apresentada nos slides.

## Estrutura

- `src/controller`: controladores (reservado para as proximas etapas)
- `src/view`: interfaces de apresentacao (reservado para as proximas etapas)
- `src/model/bo`: objetos de negocio (reservado para as proximas etapas)
- `src/tablemodel`: modelos de tabela Swing (reservado para as proximas etapas)
- `src/model/vo`: objetos de valor existentes
- `src/model/dao`: acesso a dados existente
- `src/infra`: infraestrutura existente
- `src/infra/DatabaseConfig.java`: configuracao JDBC centralizada
- `lib`: bibliotecas externas fornecidas com o projeto
- `bin`: classes compiladas

O diretorio legado `src/controler` foi preservado. O pacote novo e correto para
os controllers MVC e `src/controller`.

As views Swing ainda nao implementam regras de negocio, controllers funcionais,
autenticacao, transacoes ou funcionalidades de venda, estoque e relatorios.
Tambem nao utiliza DTO nem gerenciador de dependencias.

## Bibliotecas externas existentes

As bibliotecas abaixo ja fazem parte de `lib` e sao utilizadas diretamente pelo
classpath manual. Nenhuma dependencia nova e instalada nesta etapa.

| Arquivo | Finalidade |
| --- | --- |
| `mysql-connector-j-26.7.0.jar` | Driver JDBC do MySQL |
| `jbcrypt-0.4.jar` | Hash de palavras-passe existente nos VOs |
| `flatlaf-3.5.4.jar` | Look and feel Swing, reservado para as views |
| `ikonli-core-12.3.1.jar` | API base de icones, reservada para a interface |
| `ikonli-swing-12.3.1.jar` | Integracao Ikonli com Swing |
| `ikonli-materialdesign2-pack-12.3.1.jar` | Pack de icones Material Design 2 |

## Compilacao e execucao no Windows

Com um JDK instalado e `javac`/`java` disponiveis no `PATH`, abra o terminal na
raiz do projeto e execute manualmente:

```powershell
New-Item -ItemType Directory -Force bin
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -cp "lib\*" -d bin @fontes.txt
java -cp "bin;lib\*" App
del fontes.txt
```

O ponto de entrada abre o `LoginFrame` Swing. Após autenticação válida, o login
abre a janela principal. Em ambientes sem interface grafica, apenas informa a
limitacao e termina.

## Janela principal

`view.LoginFrame` e `view.MainFrame` seguem o fluxo visual do PDF. A janela
principal fornece marca, navegacao superior, menu do utilizador e o
`DashboardPanel`, com cartoes de indicadores, alertas de estoque e produtos
mais vendidos. Os botoes apresentam apenas avisos de modulos reservados; nao
executam operacoes de negocio nem substituem controllers. Os testes visuais
ficam em `src/testes/TesteLoginFrame.java` e
`src/testes/TesteMainFrame.java`.

## Infraestrutura JDBC

As credenciais e a URL nao sao repetidas pelos DAOs. A classe
`infra.DatabaseConfig` centraliza os valores existentes e permite sobrescreve-los
sem alterar o codigo, usando as propriedades `gestao.db.url`,
`gestao.db.username` e `gestao.db.password`, ou as variaveis
`GESTAO_DB_URL`, `GESTAO_DB_USERNAME` e `GESTAO_DB_PASSWORD`.

O script `docker/sql/00_privileges.sql` limita os privilegios do utilizador
`gestao` ao schema `gestao`; ele nao cria credenciais novas.

## Camada Business Object

Os BOs em `src/model/bo` centralizam validacoes de entrada, relacionamentos
obrigatorios, estados e permissoes ja existentes nos VOs. Eles delegam a
persistencia aos DAOs e nao contem SQL nem DTOs.

Foram implementados `UtilizadorBO`, `ProdutoBO`, `ClienteBO`, `FuncionarioBO`,
`EstoqueBO`, `VendaBO`, `ReembolsoBO` e, posteriormente, `RelatorioBO` com
consultas apoiadas pelas views SQL.

## Login

`LoginBO` centraliza o caso de uso de autenticação sem criar GUI ou sessão
global. Ele delega a pesquisa ao `UtilizadorBO`, que valida username, senha,
hash BCrypt e estado ativo do funcionário. O teste unitário correspondente fica
em `src/testes/TesteLoginBO.java`.

`TesteRelatorioDAO.java` cobre as consultas do `RelatorioDAO` contra o MySQL;
quando a base não estiver disponível, o teste informa explicitamente a
limitação de infraestrutura.

## Auditoria e relatorios

O banco possui scripts SQL para a tabela `auditoria`, com triggers para todas
as tabelas existentes (exceto a propria auditoria), indices, views, functions,
procedures e cursores administrativos. Os triggers usam `@id_utilizador_atual`
quando definida; caso contrario, a operacao fica sem utilizador associado.
`utilizador.password_hash` nao e gravado na auditoria.

As consultas Java ficam em `RelatorioDAO` e `RelatorioBO`. Como o projeto nao
usa DTO, os relatorios retornam `List<Map<String, Object>>`; o BO valida o
periodo e exige permissao de consulta de relatorios.
