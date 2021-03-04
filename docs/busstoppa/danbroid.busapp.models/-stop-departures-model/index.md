[busstoppa](../../index.md) / [danbroid.busapp.models](../index.md) / [StopDeparturesModel](./index.md)

# StopDeparturesModel

`class StopDeparturesModel : ViewModel`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `StopDeparturesModel(fragment: Fragment, stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [appModel](app-model.md) | `val appModel: `[`AppModel`](../-app-model/index.md) |
| [live](live.md) | `val live: MutableLiveData<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [stop](stop.md) | `val stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md) |
| [stopDepartures](stop-departures.md) | `val stopDepartures: LiveData<`[`StopDepartures`](../../danbroid.busapp.metlink/-stop-departures/index.md)`>` |
| [timeoutMessage](timeout-message.md) | `var timeoutMessage: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [onCleared](on-cleared.md) | `fun onCleared(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [refreshData](refresh-data.md) | `fun refreshData(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
