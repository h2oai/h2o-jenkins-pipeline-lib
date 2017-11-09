package ai.h2o.ci;

interface VersionSchema extends Serializable {
  boolean isRelease();

  String getVersion();
}
