[busstoppa](../../index.md) / [danbroid.busapp.ui](../index.md) / [MapFragment](./index.md)

# MapFragment

`abstract class MapFragment : Fragment`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MapFragment()` |

### Properties

| Name | Summary |
|---|---|
| [gpsMenuItem](gps-menu-item.md) | `var gpsMenuItem: MenuItem?` |
| [mapModel](map-model.md) | `lateinit var mapModel: `[`MapModel`](../../danbroid.busapp.models/-map-model/index.md) |
| [model](model.md) | `lateinit var model: `[`AppModel`](../../danbroid.busapp.models/-app-model/index.md) |
| [stop](stop.md) | `var stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`?` |

### Functions

| Name | Summary |
|---|---|
| [onCreateOptionsMenu](on-create-options-menu.md) | `open fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onLocationChanged](on-location-changed.md) | `open fun onLocationChanged(location: Location): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onLocationEnabled](on-location-enabled.md) | `open fun onLocationEnabled(enabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onOptionsItemSelected](on-options-item-selected.md) | `open fun onOptionsItemSelected(item: MenuItem): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onPause](on-pause.md) | `open fun onPause(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRequestPermissionsResult](on-request-permissions-result.md) | `open fun onRequestPermissionsResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, permissions: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<out `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, grantResults: `[`IntArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onResume](on-resume.md) | `open fun onResume(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStopSelected](on-stop-selected.md) | `fun onStopSelected(stopCode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onViewCreated](on-view-created.md) | `open fun onViewCreated(view: View, savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showStop](show-stop.md) | `abstract fun showStop(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`, zoomIn: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showStopPrompt](show-stop-prompt.md) | `fun showStopPrompt(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`, x: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, y: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [startGPS](start-g-p-s.md) | `fun startGPS(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toggleLocationSearch](toggle-location-search.md) | `fun toggleLocationSearch(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [VtmFragment](../-vtm-fragment/index.md) | `class VtmFragment : `[`MapFragment`](./index.md) |
