[busstoppa](../index.md) / [danbroid.busapp.ui](./index.md)

## Package danbroid.busapp.ui

### Types

| Name | Summary |
|---|---|
| [AboutDialogHelper](-about-dialog-helper/index.md) | `class AboutDialogHelper : `[`HasPrefs`](../danbroid.busapp.utils/-has-prefs/index.md) |
| [BrowserFragment](-browser-fragment/index.md) | `class BrowserFragment : Fragment, `[`SupportsBackButton`](-supports-back-button/index.md) |
| [BusStopSearchView](-bus-stop-search-view/index.md) | `class BusStopSearchView : SearchView` |
| [BusStopSuggestionsAdapter](-bus-stop-suggestions-adapter/index.md) | `class BusStopSuggestionsAdapter : CursorAdapter` |
| [DeparturesFragment](-departures-fragment/index.md) | `class DeparturesFragment : Fragment, `[`SupportsBackButton`](-supports-back-button/index.md) |
| [DeparturesPagerAdapter](-departures-pager-adapter/index.md) | `class DeparturesPagerAdapter : FragmentPagerAdapter` |
| [HasFAB](-has-f-a-b/index.md) | `interface HasFAB` |
| [MapFragment](-map-fragment/index.md) | `abstract class MapFragment : Fragment` |
| [OnCreateContextMenuListener](-on-create-context-menu-listener.md) | `typealias OnCreateContextMenuListener = (BusStopViewHolder, ContextMenu, View) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [StartDragListener](-start-drag-listener.md) | `typealias StartDragListener = (BusStopViewHolder) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [StopDeparturesFragment](-stop-departures-fragment/index.md) | `class StopDeparturesFragment : Fragment, `[`SupportsBackButton`](-supports-back-button/index.md) |
| [StopSelectedListener](-stop-selected-listener.md) | `typealias StopSelectedListener = (`[`BusStop`](../danbroid.busapp.data/-bus-stop/index.md)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [StopsHistoryFragment](-stops-history-fragment/index.md) | A placeholder fragment containing a simple view.`class StopsHistoryFragment : Fragment` |
| [StopsListAdapter](-stops-list-adapter/index.md) | `class StopsListAdapter : ListAdapter<`[`BusStop`](../danbroid.busapp.data/-bus-stop/index.md)`, BusStopViewHolder>` |
| [SupportsBackButton](-supports-back-button/index.md) | `interface SupportsBackButton` |
| [VtmFragment](-vtm-fragment/index.md) | `class VtmFragment : `[`MapFragment`](-map-fragment/index.md) |

### Annotations

| Name | Summary |
|---|---|
| [ViewType](-view-type/index.md) | `annotation class ViewType` |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [android.app.Activity](android.app.-activity/index.md) |  |

### Properties

| Name | Summary |
|---|---|
| [ARG_STOP](-a-r-g_-s-t-o-p.md) | `const val ARG_STOP: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [DAY_START](-d-a-y_-s-t-a-r-t.md) | `const val DAY_START: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [NO_DEPARTURES](-n-o_-d-e-p-a-r-t-u-r-e-s.md) | `const val NO_DEPARTURES: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [NOTICE](-n-o-t-i-c-e.md) | `const val NOTICE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [SERVICE](-s-e-r-v-i-c-e.md) | `const val SERVICE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [STOP](-s-t-o-p.md) | `const val STOP: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [viewToBitmap](view-to-bitmap.md) | `fun viewToBitmap(context: Context, view: View): Bitmap` |
