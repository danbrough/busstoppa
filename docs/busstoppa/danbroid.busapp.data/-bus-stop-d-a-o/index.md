[busstoppa](../../index.md) / [danbroid.busapp.data](../index.md) / [BusStopDAO](./index.md)

# BusStopDAO

`abstract class BusStopDAO`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BusStopDAO()` |

### Functions

| Name | Summary |
|---|---|
| [_insert](_insert.md) | `abstract fun _insert(busStop: `[`BusStop`](../-bus-stop/index.md)`): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [addStop](add-stop.md) | `open fun addStop(stop: `[`BusStop`](../-bus-stop/index.md)`, retainOrder: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [delete](delete.md) | `abstract fun delete(busStop: `[`BusStop`](../-bus-stop/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [deleteAllStops](delete-all-stops.md) | `abstract fun deleteAllStops(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAllStops](get-all-stops.md) | `abstract fun getAllStops(): LiveData<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../-bus-stop/index.md)`>>` |
| [getAllStopsByCode](get-all-stops-by-code.md) | `abstract fun getAllStopsByCode(): Factory<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, `[`BusStop`](../-bus-stop/index.md)`>` |
| [getMaxStopOrder](get-max-stop-order.md) | `abstract fun getMaxStopOrder(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [getStopByCode](get-stop-by-code.md) | `abstract fun getStopByCode(code: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`BusStop`](../-bus-stop/index.md)`?` |
| [incrementOrdersAbove](increment-orders-above.md) | `abstract fun incrementOrdersAbove(startOrder: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [insert](insert.md) | `abstract fun insert(stops: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../-bus-stop/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [moveStop](move-stop.md) | `open fun moveStop(src: `[`BusStop`](../-bus-stop/index.md)`, dest: `[`BusStop`](../-bus-stop/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [update](update.md) | `abstract fun update(busStop: `[`BusStop`](../-bus-stop/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`abstract fun update(stops: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../-bus-stop/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
