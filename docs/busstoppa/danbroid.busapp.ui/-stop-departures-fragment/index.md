[busstoppa](../../index.md) / [danbroid.busapp.ui](../index.md) / [StopDeparturesFragment](./index.md)

# StopDeparturesFragment

`class StopDeparturesFragment : Fragment, `[`SupportsBackButton`](../-supports-back-button/index.md)

### Types

| Name | Summary |
|---|---|
| [BaseViewHolder](-base-view-holder/index.md) | `class BaseViewHolder : ViewHolder` |
| [DataEntry](-data-entry/index.md) | `sealed class DataEntry` |
| [DayStartViewHolder](-day-start-view-holder/index.md) | `class DayStartViewHolder : BaseViewHolder, LayoutContainer` |
| [NoDeparturesViewHolder](-no-departures-view-holder/index.md) | `class NoDeparturesViewHolder : BaseViewHolder, LayoutContainer` |
| [StopDeparturesAdapter](-stop-departures-adapter/index.md) | `class StopDeparturesAdapter : Adapter<BaseViewHolder>` |
| [StopViewHolder](-stop-view-holder/index.md) | `class StopViewHolder : BaseViewHolder, LayoutContainer` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `StopDeparturesFragment()` |

### Properties

| Name | Summary |
|---|---|
| [model](model.md) | `lateinit var model: `[`StopDeparturesModel`](../../danbroid.busapp.models/-stop-departures-model/index.md) |
| [stop](stop.md) | `val stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md) |

### Functions

| Name | Summary |
|---|---|
| [onBackPressed](on-back-pressed.md) | `fun onBackPressed(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onCreate](on-create.md) | `fun onCreate(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateOptionsMenu](on-create-options-menu.md) | `fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateView](on-create-view.md) | `fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): <ERROR CLASS>` |
| [onOptionsItemSelected](on-options-item-selected.md) | `fun onOptionsItemSelected(item: MenuItem): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onPause](on-pause.md) | `fun onPause(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onResume](on-resume.md) | `fun onResume(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onViewCreated](on-view-created.md) | `fun onViewCreated(view: View, savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [newInstance](new-instance.md) | `fun newInstance(stop: `[`BusStop`](../../danbroid.busapp.data/-bus-stop/index.md)`): `[`StopDeparturesFragment`](./index.md) |
