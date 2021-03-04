[busstoppa](../../index.md) / [danbroid.busapp.metlink](../index.md) / [Metlink](./index.md)

# Metlink

`interface Metlink`

### Functions

| Name | Summary |
|---|---|
| [stopDepartures](stop-departures.md) | `abstract fun stopDepartures(code: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`StopDepartures`](../-stop-departures/index.md)`>` |
| [stopInfo](stop-info.md) | `abstract fun stopInfo(code: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>` |
| [stopList](stop-list.md) | `abstract fun stopList(): Call<`[`StopList`](../-stop-list/index.md)`>` |
| [stopSearch](stop-search.md) | `abstract fun stopSearch(query: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStopInfo`](../-bus-stop-info/index.md)`>>` |
| [stopsNearby](stops-nearby.md) | `abstract fun stopsNearby(lat: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, lng: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>>` |
