[busstoppa](../../../index.md) / [danbroid.busapp.ui](../../index.md) / [StopDeparturesFragment](../index.md) / [StopDeparturesAdapter](./index.md)

# StopDeparturesAdapter

`class StopDeparturesAdapter : Adapter<BaseViewHolder>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `StopDeparturesAdapter(stop: `[`BusStop`](../../../danbroid.busapp.data/-bus-stop/index.md)`, model: `[`StopDeparturesModel`](../../../danbroid.busapp.models/-stop-departures-model/index.md)`, activity: FragmentActivity)` |

### Properties

| Name | Summary |
|---|---|
| [activity](activity.md) | `val activity: FragmentActivity` |
| [data](data.md) | `var data: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<DataEntry>` |
| [model](model.md) | `val model: `[`StopDeparturesModel`](../../../danbroid.busapp.models/-stop-departures-model/index.md) |
| [stop](stop.md) | `val stop: `[`BusStop`](../../../danbroid.busapp.data/-bus-stop/index.md) |

### Functions

| Name | Summary |
|---|---|
| [getItemCount](get-item-count.md) | `fun getItemCount(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [getItemViewType](get-item-view-type.md) | `fun getItemViewType(position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [onBindViewHolder](on-bind-view-holder.md) | `fun onBindViewHolder(holder: BaseViewHolder, position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateViewHolder](on-create-view-holder.md) | `fun onCreateViewHolder(parent: ViewGroup, viewType: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): BaseViewHolder` |
| [setDepartures](set-departures.md) | `fun setDepartures(departures: `[`StopDepartures`](../../../danbroid.busapp.metlink/-stop-departures/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
