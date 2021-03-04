[busstoppa](../../index.md) / [danbroid.busapp.utils](../index.md) / [BasePref](./index.md)

# BasePref

`abstract class BasePref<T>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BasePref(prefName: `[`Enum`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)`<*>, defValue: T)`<br>`BasePref(prefName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defValue: T)` |

### Properties

| Name | Summary |
|---|---|
| [defValue](def-value.md) | `val defValue: T` |
| [prefName](pref-name.md) | `val prefName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [getPrefValue](get-pref-value.md) | `abstract fun getPrefValue(hasPrefs: `[`HasPrefs`](../-has-prefs/index.md)`): T` |
| [getValue](get-value.md) | `operator fun getValue(thisRef: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>): T` |
| [setPrefValue](set-pref-value.md) | `abstract fun setPrefValue(hasPrefs: `[`HasPrefs`](../-has-prefs/index.md)`, value: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setValue](set-value.md) | `operator fun setValue(thisRef: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>, value: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BooleanPref](../-boolean-pref/index.md) | `class BooleanPref : `[`BasePref`](./index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [DoublePref](../-double-pref/index.md) | `class DoublePref : `[`BasePref`](./index.md)`<`[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`>` |
| [FloatPref](../-float-pref/index.md) | `class FloatPref : `[`BasePref`](./index.md)`<`[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`>` |
| [IntPref](../-int-pref/index.md) | `class IntPref : `[`BasePref`](./index.md)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| [LongPref](../-long-pref/index.md) | `class LongPref : `[`BasePref`](./index.md)`<`[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`>` |
| [StringPref](../-string-pref/index.md) | `class StringPref : `[`BasePref`](./index.md)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
