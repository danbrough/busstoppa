import java.io.FileInputStream

fun initProps() {
  //println("initProps()")
  val fis = FileInputStream(file("project.properties"))
  val prop = java.util.Properties()
  prop.load(fis)
  fis.close()

  prop.keys.forEach {
    val key = it as String
    extra[key] = prop[key]
  }

  ProjectVersions.init(prop)
}




initProps()



class ProjectPlugin : Plugin<Project> {


  override fun apply(project: Project) {

    project.task("projectVersionName") {
      doLast {
        println(ProjectVersions.getVersionName())
      }
    }

    project.task("projectNextVersionName") {
      doLast {
        println(ProjectVersions.getIncrementedVersionName())
      }
    }

    project.task("projectIncrementVersion") {
      doLast {
        val propsFile = project.file("project.properties")
        val fis = FileInputStream(propsFile)
        val prop = java.util.Properties()
        prop.load(fis)
        fis.close()
        val version = prop.getProperty("buildVersion", "0").toInt()
        println("version $version")
        prop.setProperty("buildVersion", "${version + 1}")
        val fos = java.io.PrintWriter(java.io.FileWriter(propsFile))
        prop.store(fos, "")
        fos.println()
        fos.close()
      }
    }
  }
}

// Apply the plugin
apply<ProjectPlugin>()
