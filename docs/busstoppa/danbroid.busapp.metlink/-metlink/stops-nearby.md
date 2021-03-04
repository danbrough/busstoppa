[busstoppa](../../index.md) / [danbroid.busapp.metlink](../index.md) / [Metlink](index.md) / [stopsNearby](./stops-nearby.md)

# stopsNearby

`@GET("StopNearby/{lat}/{lng}") abstract fun stopsNearby(@Path("lat") lat: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, @Path("lng") lng: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): Call<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`>>`