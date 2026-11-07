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
- `lib`: bibliotecas externas fornecidas com o projeto
- `bin`: classes compiladas

Esta etapa nao implementa regras de negocio, controllers funcionais, views Swing,
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
raiz do projeto e execute:

```bat
compile.bat
run.bat
```

`compile.bat` compila todos os fontes de `src` para `bin`, usando os JARs de
`lib` no classpath. `run.bat` recompila e executa o ponto de entrada minimo
`App`; nesta etapa ele apenas confirma que a estrutura inicial foi carregada,
sem abrir GUI ou iniciar login.

Os mesmos comandos podem ser executados manualmente:

```bat
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -cp "lib\*" -d bin @fontes.txt
java -cp "bin;lib\*" App
del fontes.txt
```
