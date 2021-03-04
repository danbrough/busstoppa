import org.gradle.api.JavaVersion
import java.util.*


object ProjectVersions {
  var SDK_VERSION = 30
  var MIN_SDK_VERSION = 23
  val JAVA_VERSION = JavaVersion.VERSION_1_8
  var BUILD_VERSION = 1
  var VERSION_OFFSET = 1
  var GROUP_ID = ""
  var VERSION_FORMAT = ""
  val NDK_VERSION = "21.2.6472646"
  var COMPOSE_VERSION = "1.0.0-alpha10"

  val VERSION_NAME: String
    get() = getVersionName()

  fun init(props: Properties) {
    SDK_VERSION = props.getProperty("sdkVersion", "30").toInt()
    MIN_SDK_VERSION = props.getProperty("minSdkVersion", "23").toInt()
    BUILD_VERSION = props.getProperty("buildVersion", "1").toInt()
    VERSION_OFFSET = props.getProperty("versionOffset", "1").toInt()
    VERSION_FORMAT = props.getProperty("versionFormat", "0.0.%d").trim()
    GROUP_ID = props.getProperty("groupID", "").trim()
  }

  fun getIncrementedVersionName() = getVersionName(BUILD_VERSION + 1)


  fun getVersionName(version: Int = BUILD_VERSION) =
    VERSION_FORMAT.format(version - VERSION_OFFSET)


}
