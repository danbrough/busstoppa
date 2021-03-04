[busstoppa](../../index.md) / [danbroid.busapp.ui](../index.md) / [StopsListAdapter](./index.md)

# StopsListAdapter

`class StopsListAdapter : ListAdapter<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`, BusStopViewHolder>`

### Types

| Name | Summary |
|---|---|
| [BusStopViewHolder](-bus-stop-view-holder/index.md) | `inner class BusStopViewHolder : ViewHolder, LayoutContainer, OnCreateContextMenuListener` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `StopsListAdapter()` |

### Properties

| Name | Summary |
|---|---|
| [onCreateContextMenuListener](on-create-context-menu-listener.md) | `var onCreateContextMenuListener: `[`OnCreateContextMenuListener`](../-on-create-context-menu-listener.md)`?` |
| [onStartDragListener](on-start-drag-listener.md) | `var onStartDragListener: `[`StartDragListener`](../-start-drag-listener.md)`?` |
| [onStopSelected](on-stop-selected.md) | `var onStopSelected: `[`StopSelectedListener`](../-stop-selected-listener.md)`?` |

### Functions

| Name | Summary |
|---|---|
| [getItemId](get-item-id.md) | `fun getItemId(position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [onBindViewHolder](on-bind-view-holder.md) | `fun onBindViewHolder(holder: BusStopViewHolder, position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateViewHolder](on-create-view-holder.md) | `fun onCreateViewHolder(parent: ViewGroup, viewType: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): BusStopViewHolder` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [STOPS_DIFF_CALLBACK](-s-t-o-p-s_-d-i-f-f_-c-a-l-l-b-a-c-k.md) | `val STOPS_DIFF_CALLBACK: ItemCallback<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>` |
