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

def stageWithSummary(final String name, final String stageDir, final String rerunCommand, final Closure body) {
    stage(name) {
        _buildSummary.getStagesSummary().stage(this, name, stageDir, rerunCommand, body)
    }
}

def stageWithSummary(final String name, final String stageDir, final Closure body) {
    stage(name) {
        _buildSummary.getStagesSummary().stage(this, name, stageDir, '', body)
    }
}

def stageWithSummary(final String name, final Closure body) {
    stage(name) {
        _buildSummary.getStagesSummary().stage(this, name, '', '', body)
    }
}

def refreshStage(final String name, final boolean resetTimer = true) {
    _buildSummary.getStagesSummary().setStageDetails(this, name, resetTimer)
}

def setStageUrl(final String name) {
    _buildSummary.getStagesSummary().setStageUrl(this, name, null)
}

def setStageUrl(final String name, final String url) {
    _buildSummary.getStagesSummary().setStageUrl(this, name, url)
}
