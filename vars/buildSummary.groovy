import ai.h2o.ci.buildsummary.BuildSummary
import ai.h2o.ci.buildsummary.StagesSummary

def call(final String repoUrl, final boolean autoPublish) {
    _buildSummary = new BuildSummary(repoUrl, autoPublish)
}

def get() {
    return _buildSummary
}

def getStagesSummary() {
    return _buildSummary.getStagesSummary()
}

def stage(final String name, final String stageDir, final Closure body) {
    _buildSummary.getStagesSummary().stage(this, name, stageDir, body)
}

def stage(final String name, final Closure body) {
    _buildSummary.getStagesSummary().stage(this, name, '', body)
}

def refreshStage(final String name) {
    _buildSummary.getStagesSummary().setStageDetails(this, name)
}