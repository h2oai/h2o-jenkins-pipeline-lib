package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

/**
 * Helper class to create build summaries shown for example on build details page or sent via email.
 */
class BuildSummary implements BuildSummaryManager {

    private final boolean autoPublish
    private final List<SummaryInfo> summaries = []
    private final String repoUrl
    private DetailsSummary detailsSummary
    private StagesSummary stagesSummary

    /**
     * Default constructor.
     * @param repoUrl URL of the repository relevant for this build.
     * @param autoPublish if true, all changes are automatically published to the build summary screen.
     */
    BuildSummary(final String repoUrl, final boolean autoPublish) {
        this.repoUrl = repoUrl
        this.autoPublish = autoPublish
    }

    /**
     * Returns {@link SummaryInfo} with given id or null if no such {@link SummaryInfo} exists.
     * @param id id of the required {@link SummaryInfo}.
     * @return {@link SummaryInfo} with given id or null if no such {@link SummaryInfo} exists.
     */
    SummaryInfo findSummary(final String id) {
        return summaries.find({it.getId() == id})
    }

    /**
     * Returns {@link SummaryInfo} with same id as the given {@link SummaryInfo} or null if no such {@link SummaryInfo} exists.
     * @param summaryInfo {@link SummaryInfo} of which the id will be searched, might be null.
     * @return {@link SummaryInfo} with same id as the given {@link SummaryInfo} or null if no such {@link SummaryInfo} exists.
     */
    SummaryInfo findSummary(final SummaryInfo summaryInfo) {
        if (summaryInfo == null) {
            return null
        }
        return summaries.find({it.getId() == summaryInfo.getId()})
    }

    /**
     * Returns {@link SummaryInfo} with given id or throws an {@link IllegalStateException} if no such {@link SummaryInfo} exists.
     * @param id id of the required {@link SummaryInfo}.
     * @return {@link SummaryInfo} with given id or throws an {@link IllegalStateException} if no such {@link SummaryInfo} exists.
     * @throws {@link IllegalStateException}
     */
    SummaryInfo findSummaryOrThrow(final String id) {
        final SummaryInfo summary = findSummary(id)
        if (summary == null) {
            throw new IllegalStateException("Cannot find SummaryInfo with id %s".format(id))
        }
        return summary
    }

    /**
     * Returns {@link SummaryInfo} with same id as the given {@link SummaryInfo} or throws an {@link IllegalStateException} if no such {@link SummaryInfo} exists.
     * @param summaryInfo {@link SummaryInfo} of which the id will be searched, might be null.
     * @return {@link SummaryInfo} with same id as the given {@link SummaryInfo} or throws an {@link IllegalStateException} if no such {@link SummaryInfo} exists.
     */
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

    /**
     * Adds the given {@link SummaryInfo}. Throws an {@link IllegalArgumentException} if {@link SummaryInfo} exists.
     * @param context
     * @param summary instance of the {@link SummaryInfo} to be added
     */
    void addSummary(final context, final SummaryInfo summary) {
        if (findSummary(summary.getId()) != null) {
            throw new IllegalArgumentException('Summary with id %s already.exists'.format(summary.getId()))
        }
        addToSummaries(context, summary)
    }

    /**
     * Adds the given {@link DetailsSummary}. Useful when creating custom {@link DetailsSummary} implementation.
     * Then the {@link DetailsSummary} should be extended and an instance of the custom subclass should be passed here.
     * @param context
     * @param detailsSummary instance of the {@link DetailsSummary} to be added
     */
    @NonCPS
    void addDetailsSummary(final context, final DetailsSummary detailsSummary) {
        this.detailsSummary = detailsSummary
        addSummary(context, detailsSummary)
    }

    /**
     * Returns the {@link DetailsSummary} or null if no {@link DetailsSummary} was added
     * @return {@link DetailsSummary} or null if no {@link DetailsSummary} was added
     */
    DetailsSummary getDetailsSummary() {
        return detailsSummary
    }

    /**
     * Adds the given {@link StagesSummary}. Useful when creating custom {@link StagesSummary} implementation.
     * Then the {@link StagesSummary} should be extended and an instance of the custom subclass should be passed here.
     * @param context
     * @param stagesSummary instance of the {@link StagesSummary} to be added
     */
    @NonCPS
    void addStagesSummary(final context, final StagesSummary stagesSummary) {
        this.stagesSummary = stagesSummary
        addSummary(context, stagesSummary)
    }

    /**
     * Returns the {@link StagesSummary} or null if no {@link StagesSummary} was added
     * @return {@link StagesSummary} or null if no {@link StagesSummary} was added
     */
    StagesSummary getStagesSummary() {
        return stagesSummary
    }

    /**
     * Publishes all of the {@link SummaryInfo}, marked not for emails only, added to this {@link BuildSummary} so they'll be visible at the build detail page.
     * @param context
     */
    void publish(final context) {
        context.manager.removeSummaries()
        for (SummaryInfo summaryInfo : summaries) {
            def summary = context.manager.createSummary(summaryInfo.getIcon())
            summary.appendText(summaryInfo.toHtml(), false)
            summary = null
        }
    }

    /**
     * Returns all of the {@link SummaryInfo}, even those marked for emails only, added to this {@link BuildSummary} as a HTML string.
     * @param context
     * @return all of the {@link SummaryInfo} as a HTML string.
     */
    String toEmail(final context) {
        return toHtml(context, true)
    }

    /**
     * Returns all of the {@link SummaryInfo}, those marked for emails only are included based on value of includeEmailsOnly, added to this {@link BuildSummary} as a HTML string.
     * @param context
     * @param includeEmailsOnly if true, then include all {@link SummaryInfo}, otherwise only those not marked for emails only
     * @return
     */
    String toHtml(final context, final boolean includeEmailsOnly) {
        String result = ''
        for (SummaryInfo summaryInfo : summaries) {
            if (includeEmailsOnly || !summaryInfo.isEmailOnly() ) {
                if (includeEmailsOnly) {
                    result += "<h1 style=\"display: inline-block\"><img src=\"${BuildSummaryUtils.imageLink(context, summaryInfo.getIcon(), BuildSummaryUtils.ImageSize.MEDIUM)}\" /></h1>"
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
