[busstoppa](../../index.md) / [danbroid.busapp.models](../index.md) / [MapModel](./index.md)

# MapModel

`class MapModel : ViewModel, `[`HasPrefs`](../../danbroid.busapp.utils/-has-prefs/index.md)`, LocationListener`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MapModel(ctx: Context)` |

### Properties

| Name | Summary |
|---|---|
| [location](location.md) | `val location: LiveData<Location>` |
| [locationEnabled](location-enabled.md) | `val locationEnabled: MutableLiveData<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [prefs](prefs.md) | `val prefs: SharedPreferences` |

### Functions

| Name | Summary |
|---|---|
| [onCleared](on-cleared.md) | `fun onCleared(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onLocationChanged](on-location-changed.md) | `fun onLocationChanged(location: Location): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onProviderDisabled](on-provider-disabled.md) | `fun onProviderDisabled(provider: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onProviderEnabled](on-provider-enabled.md) | `fun onProviderEnabled(provider: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStatusChanged](on-status-changed.md) | `fun onStatusChanged(provider: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, status: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, extras: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [DEFAULT_LAT](-d-e-f-a-u-l-t_-l-a-t.md) | `const val DEFAULT_LAT: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [DEFAULT_LNG](-d-e-f-a-u-l-t_-l-n-g.md) | `const val DEFAULT_LNG: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [PERMISSIONS](-p-e-r-m-i-s-s-i-o-n-s.md) | `val PERMISSIONS: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [PERMISSIONS_ID](-p-e-r-m-i-s-s-i-o-n-s_-i-d.md) | `const val PERMISSIONS_ID: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [PREF_GPS_ENABLED](-p-r-e-f_-g-p-s_-e-n-a-b-l-e-d.md) | `const val PREF_GPS_ENABLED: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
