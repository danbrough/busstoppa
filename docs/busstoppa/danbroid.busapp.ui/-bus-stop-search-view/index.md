[busstoppa](../../index.md) / [danbroid.busapp.ui](../index.md) / [BusStopSearchView](./index.md)

# BusStopSearchView

`class BusStopSearchView : SearchView`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BusStopSearchView(context: Context, attributeSet: AttributeSet? = null)` |

### Properties

| Name | Summary |
|---|---|
| [expanded](expanded.md) | `var expanded: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onActionViewExpandedListener](on-action-view-expanded-listener.md) | `var onActionViewExpandedListener: ((expanded: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)?` |
| [onStopSelectListener](on-stop-select-listener.md) | `var onStopSelectListener: ((stopInfo: `[`BusStopInfo`](../../danbroid.busapp.metlink/-bus-stop-info/index.md)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)?` |
| [results](results.md) | `var results: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStopInfo`](../../danbroid.busapp.metlink/-bus-stop-info/index.md)`>?` |

### Functions

| Name | Summary |
|---|---|
| [closeSearchView](close-search-view.md) | `fun closeSearchView(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [dispatchKeyEventPreIme](dispatch-key-event-pre-ime.md) | `fun dispatchKeyEventPreIme(event: KeyEvent): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onActionViewCollapsed](on-action-view-collapsed.md) | `fun onActionViewCollapsed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onActionViewExpanded](on-action-view-expanded.md) | `fun onActionViewExpanded(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
