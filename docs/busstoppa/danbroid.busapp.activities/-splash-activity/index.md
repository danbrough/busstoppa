[busstoppa](../../index.md) / [danbroid.busapp.activities](../index.md) / [SplashActivity](./index.md)

# SplashActivity

`class ~~SplashActivity~~ : Activity`
**Deprecated:** This here to support old application shortcuts

Displays a splash screen and loads the main activity.

if [MainActivity.INTENT_EXTRA_STOP_CODE](#) has been specified then
the live information screen will be displayed for the specified stop.

This occurs when the activity has been loaded via a shortcut created by
[danbroid.busapp.BusApp.createShortcut](#)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Displays a splash screen and loads the main activity.`SplashActivity()` |

### Functions

| Name | Summary |
|---|---|
| [onCreate](on-create.md) | `fun onCreate(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
