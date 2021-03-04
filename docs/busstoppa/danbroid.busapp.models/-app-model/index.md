[busstoppa](../../index.md) / [danbroid.busapp.models](../index.md) / [AppModel](./index.md)

# AppModel

`class AppModel : AndroidViewModel, `[`HasPrefs`](../../danbroid.busapp.utils/-has-prefs/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AppModel(context: Application)` |

### Properties

| Name | Summary |
|---|---|
| [context](context.md) | `val context: Application` |
| [currentStop](current-stop.md) | `val currentStop: MutableLiveData<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>` |
| [errorMessage](error-message.md) | `val errorMessage: MutableLiveData<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [metlink](metlink.md) | `val metlink: `[`Metlink`](../../danbroid.busapp.metlink/-metlink/index.md) |
| [prefs](prefs.md) | `val prefs: SharedPreferences` |
| [recentStops](recent-stops.md) | `val recentStops: LiveData<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>>` |
| [repository](repository.md) | `val repository: `[`BusStopRepository`](../../danbroid.busapp.data/-bus-stop-repository/index.md) |
| [results](results.md) | `val results: LiveData<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStopInfo`](../../danbroid.busapp.metlink/-bus-stop-info/index.md)`>>` |
| [searchBusy](search-busy.md) | `val searchBusy: MutableLiveData<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [selectedStop](selected-stop.md) | `val selectedStop: MutableLiveData<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>` |

### Functions

| Name | Summary |
|---|---|
| [addStop](add-stop.md) | `fun addStop(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`, retainOrder: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): Job` |
| [cancelSearch](cancel-search.md) | `fun cancelSearch(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [findNearbyStops](find-nearby-stops.md) | `fun findNearbyStops(lat: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, lng: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): Deferred<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>?>` |
| [getStopDeparturesAsync](get-stop-departures-async.md) | `fun getStopDeparturesAsync(stopCode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Deferred<`[`StopDepartures`](../../danbroid.busapp.metlink/-stop-departures/index.md)`?>` |
| [moveStop](move-stop.md) | `fun moveStop(srcStop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`, destStop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`): Job` |
| [onCleared](on-cleared.md) | `fun onCleared(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [performSearch](perform-search.md) | `fun performSearch(queryString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeStop](remove-stop.md) | `fun removeStop(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`): Job` |
| [updateStop](update-stop.md) | `fun updateStop(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`): Job` |
