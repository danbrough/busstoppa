[busstoppa](../../../index.md) / [danbroid.busapp.ui](../../index.md) / [StopsHistoryFragment](../index.md) / [BusItemTouchHelperCallback](./index.md)

# BusItemTouchHelperCallback

`inner class BusItemTouchHelperCallback : SimpleCallback`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BusItemTouchHelperCallback()` |

### Functions

| Name | Summary |
|---|---|
| [clearView](clear-view.md) | `fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [isLongPressDragEnabled](is-long-press-drag-enabled.md) | `fun isLongPressDragEnabled(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onChildDraw](on-child-draw.md) | `fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`, dY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`, actionState: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, isCurrentlyActive: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onMove](on-move.md) | `fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onMoved](on-moved.md) | `fun onMoved(recyclerView: RecyclerView, viewHolder: ViewHolder, fromPos: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, target: ViewHolder, toPos: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, x: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, y: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSelectedChanged](on-selected-changed.md) | `fun onSelectedChanged(viewHolder: ViewHolder?, actionState: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSwiped](on-swiped.md) | `fun onSwiped(viewHolder: ViewHolder, direction: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
