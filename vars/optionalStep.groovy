import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

/** Replaces 'when' statements from declarative pipeline. */
def call(name, execute, block) {
    return stage(name, execute ? block : {
        echo "skipped stage $name"
        Utils.markStageSkippedForConditional(STAGE_NAME)
    })
}