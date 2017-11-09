package ai.h2o.ci

/**
 * THis is a simple strategy to get version of product.
 *
 * It follows Java-world rules of versioning:
 *   - when the version ends with -SNAPSHOT it is not considered as release version
 *   and the SNAPSHOT is replaced by <branch_name>_<build_number>
 *   - else the version is considered as release version
 */
class SimpleVersionSchema implements VersionSchema {

  private final boolean isRelease = false
  private final String version

  static SimpleVersionSchema create(script) {
    return new SimpleVersionSchema(script, script.readFile("version.txt").trim())
  }


  SimpleVersionSchema(script, String versionString) {
    isRelease = !versionString.endsWith("-SNAPSHOT")
    if (isRelease) {
      version = versionString
    } else {
      version = versionString.replace("SNAPSHOT", "${script.env.BRANCH_NAME.toLowerCase()}.${script.env.BUILD_ID}")
    }
  }

  @Override
  boolean isRelease() {
    return isRelease
  }

  @Override
  String getVersion() {
    return version
  }
}
