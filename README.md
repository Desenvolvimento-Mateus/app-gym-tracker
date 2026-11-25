# Gym Tracker

App Android para acompanhar o treino de academia do dia. A tela inicial mostra o resumo do treino (exercícios, séries e grupos musculares), o progresso geral e a sequência de exercícios com o progresso de cada um. Ao tocar em um exercício, abre o detalhe:

- registro das séries, em círculos numerados;
- ajuste da carga;
- forma de execução;
- dica opcional.

Ao **finalizar o treino**, ele é salvo no **histórico**, que continua lá depois de fechar e reabrir o app.

Projeto individual da **Avaliação Final** da disciplina de Programação Mobile: interface 100% em **Jetpack Compose**, **Navigation 3** em uma única Activity, **ViewModel + StateFlow**, **coroutines** e **kotlinx.serialization**.

O app não usa API externa nem tem chaves, senhas ou qualquer dado sensível. O treino vem de um JSON empacotado no app, e o histórico fica num arquivo JSON no armazenamento interno.

> A entrega da **Avaliação Parcial** (Android Views + XML + Intent) está preservada na tag [`avaliacao-parcial`](https://github.com/Desenvolvimento-Mateus/app-gym-tracker/tree/avaliacao-parcial): `git checkout avaliacao-parcial`.

## Pré-requisitos

- **Android Studio** com suporte ao Android Gradle Plugin 9.3 (o JDK embutido, JBR, já basta).
- **Android SDK 37** (Android Studio → *SDK Manager* → *Android SDK Platform 37*).
- **Emulador ou dispositivo com Android 13 (API 33) ou superior.**
- Internet na primeira compilação, para o Gradle baixar as dependências. O Gradle também baixa sozinho o JDK do daemon (`gradle/gradle-daemon-jvm.properties`).

## Como compilar e executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/Desenvolvimento-Mateus/app-gym-tracker.git
   ```
2. No Android Studio, **File → Open** e selecione a pasta do projeto.
3. Aguarde o **Gradle Sync** terminar. O Android Studio cria o `local.properties` com o caminho do SDK.
4. Inicie um emulador (ou conecte um dispositivo) com **API 33 ou superior**.
5. Selecione a configuração **`app`** e clique em **Run ▶**.

Pela linha de comando (com o `local.properties` apontando para o SDK):

```powershell
.\gradlew.bat assembleDebug   # gera o APK em app/build/outputs/apk/debug/
.\gradlew.bat test            # testes de unidade
.\gradlew.bat lint            # lint (deve terminar com "No issues found")
```

No Linux/macOS, use `./gradlew` no lugar de `.\gradlew.bat`.

## Fluxo do app

1. **Treino de hoje** carrega o treino de forma assíncrona e mostra *Carregando treino* enquanto isso.
2. Tocar em um exercício abre o **detalhe**. Ali dá para:
   - marcar as séries (tocar nos círculos, ou usar **Concluir série N** / **Desfazer**);
   - ajustar a carga em passos de 2,5 kg.
3. Voltar (seta ou botão voltar do sistema) mostra a lista já com o progresso atualizado.
4. **Finalizar treino**, que exige ao menos uma série concluída, salva o treino no histórico de forma assíncrona e abre a tela de **Histórico**.
5. Fechar e reabrir o app: o histórico continua lá. O botão **Histórico** na tela inicial também abre essa tela.

## Bibliotecas

| Biblioteca | Para que serve no projeto |
|---|---|
| Jetpack Compose (BOM, UI, Material 3) | Toda a interface: telas, componentes e tema |
| Activity Compose | `ComponentActivity.setContent` e `enableEdgeToEdge` na única Activity |
| Navigation 3 (`navigation3-runtime` e `navigation3-ui`) | Back stack de rotas (`rememberNavBackStack`) e `NavDisplay`, que mostra a tela do topo |
| Lifecycle ViewModel Compose | `viewModel { }` para criar os ViewModels passando o repositório |
| Lifecycle ViewModel Navigation 3 | `rememberViewModelStoreNavEntryDecorator`: cada tela da pilha tem os próprios ViewModels, que são limpos quando ela sai da pilha |
| Lifecycle Runtime Compose | `collectAsStateWithLifecycle()`: a tela só coleta o `StateFlow` enquanto está visível |
| kotlinx.serialization (JSON + plugin) | Lê o `workout.json` e lê/grava o histórico em JSON; também salva as rotas da navegação |
| kotlinx-coroutines-test (testes) | `runTest`, `StandardTestDispatcher` e `Dispatchers.setMain` para testar os ViewModels |
| JUnit 4 (testes) | Testes de unidade |
| Compose UI Tooling (só debug) | `@Preview` das telas no Android Studio |

Não há Hilt, Room nem Retrofit. A injeção de dependências é manual, a persistência é um arquivo JSON e não existe API remota.

## Arquitetura

```
com.mateusavila.gymtracker
├── MainActivity            única Activity: setContent { GymTrackerTheme { GymTrackerNavDisplay } }
├── GymTrackerApplication   cria o AppContainer quando o processo do app inicia
├── AppContainer            injeção manual: cria data sources e o repositório (uma instância)
├── data/
│   ├── dto/                WorkoutDto, ExerciseDto, WorkoutSessionDto (@Serializable)
│   ├── mapper/             DTO <-> domínio (toDomain / toDto)
│   ├── WorkoutLocalDataSource   lê assets/workout.json (Dispatchers.IO)
│   ├── HistoryLocalDataSource   lê/grava filesDir/workout_history.json (Dispatchers.IO)
│   ├── WorkoutRepository        interface usada pelos ViewModels
│   └── DefaultWorkoutRepository implementação: cache do treino + StateFlow do progresso
├── domain/model/           Exercise, SetsProgress, ExerciseProgress, Workout, WorkoutSession
└── ui/
    ├── navigation/         rotas (@Serializable : NavKey) + GymTrackerNavDisplay
    ├── theme/              cores, tipografia (Syne + JetBrains Mono) e GymTrackerTheme
    ├── components/         StatTile, TagChip, SetCircle, ProgressBar, Overline, BackTopBar,
    │                       LoadingState / ErrorState / EmptyState
    ├── home/               HomeScreen + HomeViewModel + HomeUiState (+ modelos de UI)
    ├── detail/             ExerciseDetailScreen + ExerciseDetailViewModel + ExerciseDetailUiState
    ├── history/            HistoryScreen + HistoryViewModel + HistoryUiState
    └── WeightFormatter     gramas -> "12,5 kg" só com aritmética inteira
```

### Decisões e justificativas

- **Três modelos separados, com mappers explícitos** (pedido da correção da parcial).
  - **DTO** (`data/dto`): é o formato do JSON e só a camada de dados o conhece.
  - **Domínio** (`domain/model`): regras de negócio em Kotlin puro, sem Android e sem anotações de serialização.
  - **Estado de UI** (`*UiState`, `ExerciseListItem`, `ExerciseDetailUi`, `WorkoutSessionItem`): só o que cada tela mostra.
  - As conversões ficam em funções `toDomain()`, `toDto()`, `toListItem()`, `toDetailUi()` e `toHistoryItem()`. Assim, mudar o JSON não muda a tela, e vice-versa.
- **Peso como `weightInGrams: Int?` em todas as camadas**, inclusive no JSON. É uma unidade explícita e evita `Double`/`Float`: 12,5 kg vira `12500`.
  - O ajuste de carga soma ou subtrai 2500 g.
  - A conversão para "12,5 kg" só acontece na UI, com divisão e resto inteiros.
  - `null` significa exercício de peso corporal.
- **UiState com `sealed interface`** (`Loading`, `Error`, `Empty`, `Success`). A tela trata cada caso com `when`, e o compilador obriga a tratar todos. O ViewModel expõe um `StateFlow`, e a tela coleta com `collectAsStateWithLifecycle()`.
- **Fluxo unidirecional.**
  - O estado desce: ViewModel → tela.
  - As ações sobem: a tela chama funções do ViewModel, como `onCompleteSet()` e `finishWorkout()`.
  - Cada tela tem um composable "com estado" (`HomeScreen`), que liga o ViewModel, e um "sem estado" (`HomeContent`), que só recebe dados e callbacks. Os composables nunca acessam o repositório.
- **Eventos únicos** ("treino salvo → abrir histórico", "erro ao salvar → snackbar") passam por um `Channel`, não pelo estado. Assim não se repetem ao girar a tela.
- **O progresso do treino fica num `StateFlow` dentro do repositório.**
  - O repositório é uma instância única no `AppContainer`, então a lista e o detalhe enxergam os mesmos dados.
  - Ao concluir uma série no detalhe e voltar, a lista já está atualizada, sem passar dados de volta entre telas.
- **Passar o mínimo de dados entre telas:** a rota do detalhe é `ExerciseDetailRoute(exerciseId: String)`. O `ExerciseDetailViewModel` recebe só o id e busca o exercício no repositório.
- **Coroutines.**
  - Toda operação assíncrona roda em `viewModelScope`, e é cancelada quando a tela sai da pilha.
  - O I/O (ler asset, ler e gravar arquivo) roda em `withContext(Dispatchers.IO)` nos data sources.
  - Um `Mutex` no repositório evita que duas telas leiam o arquivo do treino ao mesmo tempo.
  - Existe um `delay` curto e comentado (`SIMULATED_DELAY_MILLIS`), só para o estado de loading ficar visível. A leitura dos arquivos depois dele é real.
- **Erros reais tratados:**
  - `IOException`: arquivo não pode ser lido.
  - `SerializationException`: JSON inválido.
  - `IllegalArgumentException`: valores inválidos, por exemplo `totalSets = 0`, rejeitado pelo `init` do domínio.
  - Arquivo de histórico ainda inexistente **não** é erro: é a lista vazia.
- **Navigation 3.**
  - O back stack é uma lista de rotas que o próprio app altera: `add` para abrir uma tela, remover a última para voltar.
  - As rotas são `@Serializable`, então o back stack é salvo e restaurado ao girar a tela.
  - Dois decorators: `rememberSaveableStateHolderNavEntryDecorator` (estado salvável de cada tela) e `rememberViewModelStoreNavEntryDecorator` (ViewModel por tela).
  - O voltar do sistema chama o `onBack` do `NavDisplay`.
- **Injeção manual (`AppContainer`).** Para três telas, um framework de DI seria exagero. O container é criado uma vez no `Application`, e os ViewModels o recebem com `viewModel { HomeViewModel(container.workoutRepository) }`.
- **Rotação.** Sobrevivem à rotação:
  - os ViewModels, que ficam no `ViewModelStore` da entrada;
  - o progresso, que fica no repositório do `Application`;
  - o back stack, que é salvo.
- **Identidade visual própria** (mantida da parcial, sem layout de exemplos ou templates):
  - tema sempre escuro, com acento verde-limão `#C6F432`;
  - Syne nos títulos e JetBrains Mono nos números e rótulos;
  - blocos de estatística, séries como círculos numerados e barras de progresso finas.
  - As duas fontes são variáveis, e o peso é aplicado com `FontVariation`.
- **Logs e dados sensíveis.** O app não usa `Log` em nenhum lugar, então nada do usuário vai para o Logcat. `secrets.properties` e `*.env` estão no `.gitignore` por precaução.

## Requisitos da avaliação → onde está no código

Caminhos relativos a `app/src/main/java/com/mateusavila/gymtracker/`.

| Requisito | Onde |
|---|---|
| ≥ 3 telas conectadas, toda a UI em Compose, sem layouts XML | `ui/home/HomeScreen.kt`, `ui/detail/ExerciseDetailScreen.kt`, `ui/history/HistoryScreen.kt`. Não existe `res/layout/`, e `viewBinding = false` em `app/build.gradle.kts` |
| Lista com `LazyColumn` | `ui/home/HomeScreen.kt:149` (exercícios) e `ui/history/HistoryScreen.kt:99` (treinos finalizados), com `items(..., key = { it.id })` |
| ViewModel gerenciando estado e respondendo a ações | `HomeViewModel` (`loadWorkout`, `finishWorkout`), `ExerciseDetailViewModel` (`onSetClick`, `onCompleteSet`, `onUndoSet`, `onIncreaseWeight`, `onDecreaseWeight`), `HistoryViewModel` (`loadHistory`); estado em `StateFlow` coletado com `collectAsStateWithLifecycle()` |
| Operação assíncrona relevante com coroutines | Leitura do treino: `data/WorkoutLocalDataSource.kt:19`. Leitura/gravação do histórico: `data/HistoryLocalDataSource.kt:18` e `:24`. Disparadas em `viewModelScope.launch` (`ui/home/HomeViewModel.kt:43` e `:73`, `ui/history/HistoryViewModel.kt:32`) |
| Feedback de loading, erro, sucesso e lista vazia | `sealed interface` em `ui/home/HomeUiState.kt:9`, `ui/detail/ExerciseDetailUiState.kt:7`, `ui/history/HistoryUiState.kt:9`. Componentes em `ui/components/ScreenStates.kt` (`LoadingState`, `ErrorState` com "Tentar novamente", `EmptyState`). Snackbar de erro ao salvar e loading no botão em `ui/home/HomeScreen.kt` |
| Navigation 3 em uma única Activity | `MainActivity.kt:13` (única no `AndroidManifest.xml`). `ui/navigation/Routes.kt` e `ui/navigation/GymTrackerNavDisplay.kt:26` (`rememberNavBackStack`), `:29` (`NavDisplay`), `:32` (decorators), `:39`–`:53` (`entry<...>`) |
| Valores opcionais (`String?`, `Int?`) | `weightInGrams: Int?` e `notes: String?` em `domain/model/Exercise.kt:19-20` e `data/dto/ExerciseDto.kt:19-20` (com valor padrão `null`). Card de dica só com `notes != null` (`ui/detail/ExerciseDetailScreen.kt:156`). Ajuste de carga só com peso (`:133`). "Peso corporal" em `ui/WeightFormatter.kt:28`. Id inexistente → `getExercise` retorna `null` → erro "Exercício não encontrado" |
| Serializar/desserializar com kotlinx.serialization | DTOs `@Serializable` em `data/dto/`. `decodeFromString` em `WorkoutLocalDataSource.kt:21` e `HistoryLocalDataSource.kt:21`, `encodeToString` em `HistoryLocalDataSource.kt:25`. Rotas `@Serializable` em `ui/navigation/Routes.kt` |
| Nenhum dado sensível versionado nem nos logs | Sem API, sem chaves e sem `Log` no código. `secrets.properties` e `*.env` no `.gitignore` |
| Testes de unidade | `app/src/test/`: `SetsProgressTest`, `ExerciseProgressTest`, `WeightFormatterTest`, `WorkoutMappersTest`, `WorkoutJsonTest`, `HistoryLocalDataSourceTest`, `HomeUiModelsTest`, `HistoryUiStateTest`, `HomeViewModelTest`, `ExerciseDetailViewModelTest` e `HistoryViewModelTest` (68 testes) |

## Testes

```powershell
.\gradlew.bat test
```

- **Domínio:**
  - séries (concluir, desfazer, alternar, %);
  - ajuste de carga em gramas (mínimo de 2500 g, peso corporal inalterado);
  - cálculo da sessão finalizada.
- **Dados:**
  - mappers DTO ↔ domínio, incluindo campos nulos e valores inválidos;
  - (de)serialização: opcionais `null` ou ausentes, chave desconhecida, JSON inválido, peso não inteiro e validação do `assets/workout.json` real;
  - `HistoryLocalDataSource` com pasta temporária: arquivo inexistente, gravar e ler, arquivo corrompido.
- **ViewModels:** com `FakeWorkoutRepository` e `kotlinx-coroutines-test`, cobrem:
  - as transições Loading → Success/Error/Empty;
  - "Tentar novamente";
  - o progresso compartilhado entre as telas;
  - os eventos de finalizar treino.

## Convenções

- Código (classes, funções, variáveis e comentários) em inglês, em camelCase. Textos exibidos ao usuário em português, em `res/values/strings.xml`, incluindo um `<plurals>`.
- Recursos em snake_case com prefixo por tipo (`ic_` para ícones vetoriais).
- Lint sem avisos. Em `app/build.gradle.kts`, só os avisos de "versão mais nova disponível" estão desativados, com a justificativa comentada:
  - as versões de AGP (9.3.1), Kotlin (2.2.10) e compileSdk (37.0) são fixas;
  - as bibliotecas estão na versão mais nova compatível com elas.

## Limitações conhecidas

- O progresso do treino em andamento fica em memória: sobrevive à rotação e à navegação entre telas, mas não ao encerramento do processo pelo sistema. O que é persistido é o treino **finalizado**, no histórico.
- Se o arquivo do histórico for corrompido fora do app, o histórico mostra o erro "dados inválidos" e não é possível finalizar novos treinos até o arquivo ser apagado (limpar os dados do app).
