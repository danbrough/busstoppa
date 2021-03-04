[busstoppa](../../index.md) / [danbroid.busapp.ui](../index.md) / [StopsHistoryFragment](./index.md)

# StopsHistoryFragment

`class StopsHistoryFragment : Fragment`

A placeholder fragment containing a simple view.

### Types

| Name | Summary |
|---|---|
| [BusItemTouchHelperCallback](-bus-item-touch-helper-callback/index.md) | `inner class BusItemTouchHelperCallback : SimpleCallback` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | A placeholder fragment containing a simple view.`StopsHistoryFragment()` |

### Properties

| Name | Summary |
|---|---|
| [adapter](adapter.md) | `lateinit var adapter: `[`StopsListAdapter`](../-stops-list-adapter/index.md) |
| [itemTouchHelper](item-touch-helper.md) | `lateinit var itemTouchHelper: ItemTouchHelper` |
| [model](model.md) | `lateinit var model: `[`AppModel`](../../danbroid.busapp.models/-app-model/index.md) |
| [stops](stops.md) | `lateinit var stops: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>` |
| [stopsObserver](stops-observer.md) | `val stopsObserver: Observer<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>>` |

### Functions

| Name | Summary |
|---|---|
| [onActivityCreated](on-activity-created.md) | `fun onActivityCreated(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateOptionsMenu](on-create-options-menu.md) | `fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateView](on-create-view.md) | `fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): <ERROR CLASS>` |
| [onOptionsItemSelected](on-options-item-selected.md) | `fun onOptionsItemSelected(item: MenuItem): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onResume](on-resume.md) | `fun onResume(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onViewCreated](on-view-created.md) | `fun onViewCreated(view: View, savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [renameStop](rename-stop.md) | `fun renameStop(holder: BusStopViewHolder): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
