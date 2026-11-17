# Sistema de Resgate Robótico

Aplicação Java de console para cadastrar áreas e robôs, planejar e simular missões, recomendar robôs e preservar os dados em arquivos.

## Executar

Requisito: JDK 17 ou superior, com `java` e `javac` disponíveis. Validado nesta revisão com JDK 26.0.1 e compilação compatível com Java 17.

No IntelliJ: abra a pasta, marque `src` como Sources Root e execute `Main.main`.

No PowerShell, na pasta do projeto: `powershell -ExecutionPolicy Bypass -File .\executar.ps1`.
Para os testes: `powershell -ExecutionPolicy Bypass -File .\testar.ps1`.

O programa salva `central.dat` na pasta atual ao sair. Uma segunda gravação preserva a versão anterior em `central.dat.bak`. Se a carga falhar, o programa encerra sem substituir o arquivo. Para restaurar um backup, feche o programa, preserve o arquivo inválido e copie `central.dat.bak` para `central.dat`.

## Fluxo de uma missão

1. Cadastre uma área (opção 4) e um robô (5).
2. Cadastre uma missão (6); ela entra na fila de pendentes.
3. Consulte a recomendação (8) e atribua o robô (7).
4. Inicie (9): a missão sai da fila e fica EM_ANDAMENTO.
5. Execute e conclua (10): o resultado é automático; bateria, contador e estados são atualizados.
6. Consulte os dados (12-14), gere o resumo (3) ou exporte (16).
7. Saia com 0 e reabra para demonstrar a recuperação.

Atribuir reserva o robô, mas a missão permanece PENDENTE até iniciar. Não existe execução em tempo real nem comunicação com equipamentos físicos.

## Demonstração sugerida

Crie uma área urbana de risco baixo, um drone com 100% de bateria e alcance 50 km e uma missão de reconhecimento. Ao executar, a bateria fica em 80%, o contador passa a 1 e a missão fica CONCLUIDA. Tente iniciar novamente: a ação é rejeitada. Crie um robô terrestre e tente atribuí-lo a reconhecimento: a incompatibilidade é rejeitada.

## Estrutura

- `src/ambiente`, `src/robos`, `src/missoes`, `src/controle`, `src/enums`, `src/persistencia`: domínio e serviços.
- `src/App.java`, `src/Main.java`: menu e inicialização.
- `tests/TestesSistema.java`: 85 verificações sem dependências externas.
- `docs/Relatorio-final.md` e PDF: descrição técnica, testes e uso de IA.
- `docs/UML.pdf`, `docs/UML.mmd`: diagramas e fonte editável.
- `docs/evidencias`: saídas reais dos testes e capturas dos registros.

## Antes de entregar

Preencher integrantes, turma e professor no relatório editável; revisar a reflexão individual sobre IA; cada integrante deve repetir a demonstração e conseguir explicar as regras e o código. As evidências foram produzidas na revisão técnica automatizada, não representam uma apresentação já realizada pelo grupo.

## Observações

Capacidade de carga e alcance são características próprias validadas no cadastro. A simulação atual não recebe peso ou distância por missão; esses atributos não limitam a execução. Prioridade é registrada e exibida; a fila usa ordem de chegada (FIFO). Persistência é serialização local; não há migração de arquivos de versões anteriores das classes. Os arquivos de dados devem ser gerados pelo próprio programa.
