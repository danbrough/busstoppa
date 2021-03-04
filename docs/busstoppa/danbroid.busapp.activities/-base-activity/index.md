[busstoppa](../../index.md) / [danbroid.busapp.activities](../index.md) / [BaseActivity](./index.md)

# BaseActivity

`abstract class BaseActivity : AppCompatActivity, RationaleCallbacks, `[`HasPrefs`](../../danbroid.busapp.utils/-has-prefs/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BaseActivity()` |

### Properties

| Name | Summary |
|---|---|
| [prefs](prefs.md) | `open val prefs: SharedPreferences` |
| [withLocationAction](with-location-action.md) | `var withLocationAction: (() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)?` |

### Functions

| Name | Summary |
|---|---|
| [onRationaleAccepted](on-rationale-accepted.md) | `open fun onRationaleAccepted(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRationaleDenied](on-rationale-denied.md) | `open fun onRationaleDenied(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRequestPermissionsResult](on-request-permissions-result.md) | `open fun onRequestPermissionsResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, permissions: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, grantResults: `[`IntArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
