import ai.h2o.ci.BuildResult
import ai.h2o.ci.Emailer

def call(final BuildResult result, final String content, final List<String> recipients) {
    Emailer.sendEmail(this, result, content, recipients)
}
