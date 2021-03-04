[busstoppa](../../index.md) / [danbroid.busapp.activities](../index.md) / [MainActivity](./index.md)

# MainActivity

`class MainActivity : AppCompatActivity`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MainActivity()` |

### Properties

| Name | Summary |
|---|---|
| [appModel](app-model.md) | `lateinit var appModel: `[`AppModel`](../../danbroid.busapp.models/-app-model/index.md) |
| [currentContent](current-content.md) | `var currentContent: Fragment?` |

### Functions

| Name | Summary |
|---|---|
| [closeSearchView](close-search-view.md) | `fun closeSearchView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackPressed](on-back-pressed.md) | `fun onBackPressed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreate](on-create.md) | `fun onCreate(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateOptionsMenu](on-create-options-menu.md) | `fun onCreateOptionsMenu(menu: Menu): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onNewIntent](on-new-intent.md) | `fun onNewIntent(intent: Intent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onOptionsItemSelected](on-options-item-selected.md) | `fun onOptionsItemSelected(item: MenuItem): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [processIntent](process-intent.md) | `fun processIntent(intent: Intent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setContent](set-content.md) | `fun setContent(fragment: Fragment, addToBackStack: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showBrowser](show-browser.md) | `fun showBrowser(url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showDepartures](show-departures.md) | `fun showDepartures(stopCode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, addToBackStack: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`fun showDepartures(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`, addToBackStack: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showMap](show-map.md) | `fun showMap(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
