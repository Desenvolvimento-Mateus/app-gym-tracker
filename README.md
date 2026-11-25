# Gym Tracker

App Android para acompanhar o treino de academia do dia. A tela inicial mostra um resumo do treino (exercícios, séries e grupos musculares) e a sequência de exercícios; ao tocar em um exercício, abre-se o detalhe com séries, repetições, carga, descanso, a forma de execução e uma dica opcional. No detalhe, o usuário registra as séries concluídas tocando nos números das séries ou nos botões **Concluir série** / **Desfazer**.

Projeto individual da **Avaliação Parcial** da disciplina de Programação Mobile — Android Views (XML) e navegação com Intent. Os dados são simulados (mock), sem API ou banco de dados.

## Como rodar

1. Instale o **Android Studio** (versão com suporte ao Android Gradle Plugin 9.3) com o **Android SDK 37**.
2. JDK: use o JDK embutido do Android Studio (JBR). O Gradle baixa automaticamente o JDK 25 usado pelo daemon (`gradle/gradle-daemon-jvm.properties`).
3. Clone o repositório:
   ```bash
   git clone https://github.com/Desenvolvimento-Mateus/app-gym-tracker.git
   ```
4. No Android Studio, **File → Open** e selecione a pasta do projeto.
5. Aguarde o **Gradle Sync** terminar.
6. Crie/inicie um emulador (ou conecte um dispositivo) com **Android 13 (API 33) ou superior**.
7. Selecione a configuração `app` e clique em **Run ▶**.

Pela linha de comando (Windows/PowerShell):

```powershell
.\gradlew.bat assembleDebug   # gera o APK de debug
.\gradlew.bat test            # roda os testes de unidade
```

## Estrutura do código

```
com.mateusavila.gymtracker
├── domain/model/        → modelos de domínio (regras de negócio, sem nada de Android)
│   ├── Exercise.kt         exercício do treino (peso em gramas: weightInGrams: Int?)
│   └── SetsProgress.kt     progresso das séries (concluir, desfazer, alternar, %)
├── data/
│   └── ExerciseMockDataSource.kt   dados simulados + busca por id
└── ui/                  → camada de apresentação
    ├── WeightFormatter.kt  gramas → "12,5 kg" só com aritmética inteira
    ├── StatTile.kt         preenche o componente reutilizável view_stat_tile.xml
    ├── home/               HomeActivity, ExerciseListAdapter e modelos de UI
    │                       (ExerciseListItem, WorkoutSummary + mappers do domínio)
    └── detail/             ExerciseDetailActivity e SetsProgressFragment
```

- Os modelos de domínio não conhecem a UI; a tela inicial usa seus próprios modelos (`ExerciseListItem`, `WorkoutSummary`) criados por funções de mapeamento (`toListItem`, `toWorkoutSummary`).
- Entre as telas trafega **somente o id do exercício** (`ExerciseDetailActivity.createIntent(context, exerciseId)`); o detalhe e o Fragment buscam o restante no `ExerciseMockDataSource`.
- A carga é guardada como `Int` em gramas (`weightInGrams`), evitando `Double`/`Float`; a conversão para kg acontece só na apresentação.

## Bibliotecas utilizadas

| Biblioteca | Para que serve no projeto |
|---|---|
| AndroidX Core KTX | `ViewCompat`/`WindowInsetsCompat` (barras do sistema), `bundleOf` e `isVisible` |
| AndroidX AppCompat | `AppCompatActivity`, `Toolbar` como action bar e `drawableTint` em `TextView` |
| AndroidX Activity KTX | `enableEdgeToEdge()` e o `onBackPressedDispatcher` usado no botão voltar |
| AndroidX Lifecycle Runtime KTX | Ciclo de vida das Activities (dependência padrão do template) |
| Material Components | Tema Material 3 escuro, `MaterialToolbar` e `MaterialButton` |
| AndroidX RecyclerView | Lista de exercícios (vem como dependência transitiva do Material/AppCompat) |
| AndroidX Fragment | `SetsProgressFragment` e `FragmentContainerView` (vem como dependência transitiva do AppCompat) |
| Jetpack Compose (BOM, UI, Material3, Activity Compose) | Já declarado para a Etapa 2; não é usado nas telas desta etapa |
| JUnit 4 | Testes de unidade |

## Requisitos da avaliação → onde está no código

| Requisito | Onde |
|---|---|
| Duas telas em Views/XML com Views e ViewGroups adequados | `activity_home.xml` (LinearLayout, TextView, RecyclerView) e `activity_exercise_detail.xml` (MaterialToolbar, ScrollView, LinearLayout, FragmentContainerView); itens em `item_exercise.xml` (TextView, ImageView, `<include>`) |
| Navegação por Intent explícita com passagem de dados | `HomeActivity.openExerciseDetail` → `ExerciseDetailActivity.createIntent(context, exerciseId)` (passa apenas o id) |
| Views conectadas por ViewBinding + interação que atualiza a interface | Clique no item da lista abre o detalhe; em `SetsProgressFragment`, tocar numa série ou em Concluir/Desfazer atualiza contador, %, séries marcadas e botões |
| Modelos imutáveis (`data class`) | `Exercise`, `SetsProgress`, `ExerciseListItem`, `WorkoutSummary`; alterações via `copy()` |
| Valores opcionais tratados | `weightInGrams: Int?` → "Peso corporal"/"Corporal"; `notes: String?` → card de dica escondido; id inexistente → Toast + `finish()` |
| Dados simulados (mock) | `data/ExerciseMockDataSource.kt` |
| Opcional: apenas ViewBinding | Nenhum `findViewById` no projeto |
| Opcional: componentes XML reutilizáveis inflados + eventos que atualizam a UI | `view_stat_tile.xml` (3× na home, 4× no detalhe, via `<include>`), `view_chip.xml` (no item da lista), `item_set.xml` (inflado com `ItemSetBinding.inflate` uma vez por série, com clique que atualiza a UI) |
| Opcional: interface funcional em `Fragment` com ciclo de vida e ViewBinding | `SetsProgressFragment`: `newInstance` com argumentos, estado criado em `onCreate`, binding em `onCreateView` e liberado em `onDestroyView`, séries concluídas salvas em `onSaveInstanceState` (sobrevivem à rotação) |
| Testes | `app/src/test/...`: `SetsProgressTest`, `WeightFormatterTest`, `HomeUiModelsTest`, `ExerciseMockDataSourceTest` |

## Convenções

- Código (classes, variáveis, funções, comentários) em inglês e em camelCase; textos exibidos ao usuário em português, em `strings.xml`.
- Recursos em snake_case com prefixo por tipo: `activity_`, `fragment_`, `item_`, `view_` (layouts), `bg_`, `ic_` (drawables); ids de views também em snake_case.

## Limitações conhecidas

- As séries marcadas sobrevivem à rotação da tela, mas não são persistidas: ao voltar para a lista e abrir o exercício de novo, o progresso recomeça. Persistência e `ViewModel` entram na Etapa 2.
