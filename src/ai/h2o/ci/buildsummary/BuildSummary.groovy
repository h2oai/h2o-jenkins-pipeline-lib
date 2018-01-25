package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS
import org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildSummaryAction

class BuildSummary implements BuildSummaryManager {

    private final boolean autoPublish
    private final List<SummaryInfo> summaries = []
    private final String repoUrl
    private DetailsSummary detailsSummary
    private StagesSummary stagesSummary

    BuildSummary(final String repoUrl, final boolean autoPublish) {
        this.repoUrl = repoUrl
        this.autoPublish = autoPublish
    }

    SummaryInfo findSummary(final String id) {
        return summaries.find({it.getId() == id})
    }

    SummaryInfo findSummary(final SummaryInfo summaryInfo) {
        if (summaryInfo == null) {
            return null
        }
        return summaries.find({it.getId() == summaryInfo.getId()})
    }

    SummaryInfo findSummaryOrThrow(final String id) {
        final SummaryInfo summary = findSummary(id)
        if (summary == null) {
            throw new IllegalStateException("Cannot find SummaryInfo with id %s".format(id))
        }
        return summary
    }

    SummaryInfo findSummaryOrThrow(final SummaryInfo summaryInfo) {
        final SummaryInfo summary = findSummary(summaryInfo)
        if (summary == null) {
            String message = 'Cannot find SummaryInfo with id null'
            if (summaryInfo != null) {
                message = "Cannot find SummaryInfo with id %s".format(summaryInfo.getId())
            }
            throw new IllegalStateException(message)
        }
        return summary
    }

    void addSummary(final context, final SummaryInfo summary) {
        if (findSummary(summary.getId()) != null) {
            throw new IllegalArgumentException('Summary with id %s already.exists'.format(summary.getId()))
        }
        addToSummaries(context, summary)
    }

    @NonCPS
    void addDetailsSummary(final context, final DetailsSummary detailsSummary) {
        this.detailsSummary = detailsSummary
        addSummary(context, detailsSummary)
    }

    DetailsSummary getDetailsSummary() {
        return detailsSummary
    }

    void addStagesSummary(final context, final StagesSummary stagesSummary) {
        this.stagesSummary = stagesSummary
        addSummary(context, stagesSummary)
    }

    StagesSummary getStagesSummary() {
        return stagesSummary
    }

    void publish(final context) {
        context.manager.removeSummaries()
        for (SummaryInfo summaryInfo : summaries) {
            final GroovyPostbuildSummaryAction summaryAction = context.manager.createSummary(summaryInfo.getIcon())
            summaryAction.appendText(summaryInfo.toHtml(), false)
        }
    }

    String toEmail(final context) {
        return toHtml(context, true)
    }

    String toHtml(final context, final boolean email) {
        String result = ''
        for (SummaryInfo summaryInfo : summaries) {
            if (email || !summaryInfo.isEmailOnly() ) {
                if (email) {
                    result += "<h1 style=\"display: inline-block\"><img src=\"${BuildSummaryUtils.imageLink(context, summaryInfo.getIcon())}\" /></h1>"
                }
                result += summaryInfo.toHtml()
            }
        }
        return result
    }

    private void addToSummaries(final context, final SummaryInfo summaryInfo) {
        summaryInfo.setManager(this)
        summaries.add(summaryInfo)
        Collections.sort(summaries)
        publishAutomatically(context)
    }

    @Override
    void publishAutomatically(final context) {
        if (autoPublish) {
            publish(context)
        }
    }
}
