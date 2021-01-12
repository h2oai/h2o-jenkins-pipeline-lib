package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

/**
 * Base class representing one section in the build details page in Jenkins, or one section of email notification.
 */
class SummaryInfo implements Comparable<SummaryInfo>, Serializable {
    protected final String id
    protected final String icon
    protected final boolean emailOnly
    protected final int order
    protected String title
    protected String content
    protected BuildSummaryManager manager

    /**
     * Creates a {@link SummaryInfo} with given icon, title, content and order. If marked for emails only,
     * then it will be present in {@link BuildSummary#toEmail(java.lang.Object)} but not in build details summary.
     * @param id id of this build summary, must be unique in scope of {@link BuildSummary}
     * @param icon icon name to use for this {@link SummaryInfo}
     * @param title title of this {@link SummaryInfo}
     * @param content HTML content of this {@link SummaryInfo}
     * @param emailOnly if true, then will be present only in {@link BuildSummary#toEmail(java.lang.Object)} but not in build details summary
     * @param order order between other {@link SummaryInfo} objects. {@link SummaryInfo} objects with higher order are displayed before {@link SummaryInfo} objects with lower order
     */
    SummaryInfo(final String id, final String icon, final String title, final String content, final boolean emailOnly, final int order) {
        this.id = id
        this.icon = icon
        this.title = title
        this.content = content
        this.order = order
        this.emailOnly = emailOnly
    }

    protected SummaryInfo(final String id, final String icon, final boolean emailOnly, final int order) {
        this.id = id
        this.icon = icon
        this.order = order
        this.emailOnly = emailOnly
    }

    /**
     *
     * @return id of this {@link SummaryInfo}
     */
    String getId() {
        return id
    }

    /**
     *
     * @return icon name of this {@link SummaryInfo}
     */
    String getIcon() {
        return icon
    }

    /**
     *
     * @return title of this {@link SummaryInfo}
     */
    String getTitle() {
        return title
    }

    /**
     *
     * @param title title to set for this summary
     */
    @NonCPS
    void setTitle(String title) {
        this.title = title
    }

    /**
     *
     * @return HTML content of this {@link SummaryInfo}
     */
    String getContent() {
        return content
    }

    @NonCPS
    void setContent(String content) {
        this.content = content
    }

    /**
     *
     * @return order of this {@link SummaryInfo}
     */
    @NonCPS
    int getOrder() {
        return order
    }

    /**
     *
     * @return if true, then this {@link SummaryInfo} is intended for emails only and should not be displayed on the build details page
     */
    boolean isEmailOnly() {
        return emailOnly
    }

    /**
     *
     * @return HTML representation of this {@link SummaryInfo}
     */
    String toHtml() {
        return """
            <h1 style="display: inline-block">${getTitle()}</h1>
            ${getContent()}
        """
    }

    @NonCPS
    @Override
    int compareTo(SummaryInfo o) {
        return Integer.compare(o.getOrder(), getOrder())
    }

    /**
     *
     * @return returns {@link BuildSummaryManager} associated with this {@link SummaryInfo}. Might be null
     */
    BuildSummaryManager getManager() {
        return manager
    }

    /**
     * Sets {@link BuildSummaryManager} for this {@link SummaryInfo}. Might be null
     * @param manager
     */
    @NonCPS
    void setManager(BuildSummaryManager manager) {
        this.manager = manager
    }

    /**
     * If {@link BuildSummaryManager} is set, then notify this {@link BuildSummaryManager} to refresh the build
     * details page. It is responsibility of the  {@link BuildSummaryManager} to update the build details page
     * only if autoPublish is enabled.
     * @param context
     * @return
     */
    @NonCPS
    protected publishAutomatically(final context) {
        if (manager != null) {
            manager.publishAutomatically(context)
        }
    }
}