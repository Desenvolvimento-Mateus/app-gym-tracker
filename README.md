# Gym Tracker

App Android para acompanhar o treino de academia do dia. A tela inicial lista os exercícios do treino; ao tocar em um exercício, abre-se o detalhe com a forma de execução, as informações de séries, repetições, carga e descanso, e os botões para marcar (▶) ou desfazer (◀) as séries concluídas, com barra de progresso.

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

## Bibliotecas utilizadas

| Biblioteca | Para que serve no projeto |
|---|---|
| AndroidX Core KTX | Extensões Kotlin do Android e `ViewCompat`/`WindowInsetsCompat` para tratar as barras do sistema |
| AndroidX AppCompat | `AppCompatActivity` e suporte à `Toolbar` como action bar |
| AndroidX Activity KTX | `enableEdgeToEdge()` e o `onBackPressedDispatcher` usado no botão voltar |
| AndroidX Lifecycle Runtime KTX | Ciclo de vida das Activities (dependência padrão do template) |
| Material Components | Tema Material 3 escuro, `MaterialToolbar`, `LinearProgressIndicator`, `FloatingActionButton` e `MaterialDivider` |
| AndroidX ConstraintLayout | Declarada no template; as telas usam `LinearLayout`/`FrameLayout` |
| AndroidX RecyclerView | Lista de exercícios (vem como dependência transitiva do Material/AppCompat) |
| AndroidX Fragment | `SetsProgressFragment` e `FragmentContainerView` (vem como dependência transitiva do AppCompat) |
| Jetpack Compose (BOM, UI, Material3, Activity Compose) | Já declarado para a Etapa 2; não é usado nas telas desta etapa |
| JUnit 4 | Testes de unidade da regra de negócio (`ExerciseDetailTest`) |

## Requisitos da avaliação → onde está no código

| Requisito | Onde |
|---|---|
| Duas telas em Views/XML com Views e ViewGroups adequados | `res/layout/activity_home.xml` (LinearLayout + RecyclerView) e `res/layout/exercise_detail_layout.xml` (ScrollView, LinearLayout, FrameLayout, ImageView, TextView, FragmentContainerView) + `fragment_sets_progress.xml` (LinearProgressIndicator, FloatingActionButton) |
| Navegação por Intent explícita com passagem de dados | `HomeActivity.kt` → `ExerciseListAdapter.onBindViewHolder` (`putExtra(EXERCISE_ID_KEY, item.id)`); `ExerciseDetailActivity.kt` lê o id e busca em `mockExercises` |
| Views conectadas por ViewBinding + interação que atualiza a interface | `SetsProgressFragment.kt`: FABs ◀/▶ chamam `undoSet()`/`completeSet()` e `updateSetsProgressViews()`; clique no item da lista abre o detalhe |
| Modelos imutáveis (`data class`) | `ExerciseDetail` (fim de `ExerciseDetailActivity.kt`) e `ExerciseListItem` (fim de `HomeActivity.kt`); alterações via `copy()` |
| Valores opcionais tratados | `weightKg: Double?` → "Peso corporal" e badge omitido; `notes: String?` → "Sem observações"; id inexistente → Toast + `finish()` |
| Dados simulados (mock) | `mockExercises` no topo de `ExerciseDetailActivity.kt` |
| Opcional: apenas ViewBinding | Nenhum `findViewById` no projeto |
| Opcional: componentes XML reutilizáveis inflados + eventos que atualizam a UI | `badge_layout.xml` (usado com `<include>` no item da lista e inflado com `BadgeLayoutBinding.inflate` no header do detalhe); `detail_row_layout.xml` (reutilizado 6 vezes no card Detalhes, preenchido por `setDetailRow`); `fragment_sets_progress.xml` (inflado pelo Fragment, com os eventos ◀/▶ que atualizam texto, percentual, barra e botões) |
| Opcional: interface funcional em `Fragment` com ciclo de vida e ViewBinding | `SetsProgressFragment.kt`: `newInstance` com argumentos, binding criado em `onCreateView` e liberado em `onDestroyView`, listeners em `onViewCreated` e séries concluídas salvas em `onSaveInstanceState` (sobrevivem à rotação) |
| Testes da regra de negócio | `app/src/test/java/com/mateusavila/gymtracker/ExerciseDetailTest.kt` |

## Limitações conhecidas

- As séries marcadas sobrevivem à rotação da tela, mas não são persistidas: ao voltar para a lista e abrir o exercício de novo, o progresso recomeça. Persistência e `ViewModel` entram na Etapa 2.
- As imagens são um ícone vetorial genérico de haltere.
