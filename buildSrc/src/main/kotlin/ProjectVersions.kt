import org.gradle.api.JavaVersion
import java.util.*

object ProjectVersions {
  var SDK_VERSION = 30
  var MIN_SDK_VERSION = 16

  var JAVA_VERSION = JavaVersion.VERSION_1_8
  var BUILD_VERSION = 118
  var GROUP_ID = "com.github.danbrough.busstoppa"

  var VERSION_OFFSET = 1
  var KEYSTORE_PASSWORD = ""
  var VERSION_FORMAT = ""


  fun init(props: Properties) {
    SDK_VERSION = props.getProperty("sdkVersion", "29").toInt()
    MIN_SDK_VERSION = props.getProperty("minSdkVersion", "23").toInt()
    BUILD_VERSION = props.getProperty("buildVersion", "1").toInt()
    VERSION_OFFSET = props.getProperty("versionOffset", "1").toInt()
    VERSION_FORMAT = props.getProperty("versionFormat", "0.0.%d").trim()
    GROUP_ID = props.getProperty("groupID", "com.github.danbrough.androidutils").trim()
    KEYSTORE_PASSWORD = props.getProperty("keystorePassword", "").trim()
  }

  fun getIncrementedVersionName() = getVersionName(BUILD_VERSION + 1)


  fun getVersionName(version: Int = BUILD_VERSION) =
    VERSION_FORMAT.format(version - VERSION_OFFSET)

  val VERSION_NAME: String
    get() = getVersionName()
}

