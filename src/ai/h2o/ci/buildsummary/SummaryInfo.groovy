package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

class SummaryInfo implements Comparable<SummaryInfo>, Serializable {
    protected final String id
    protected final String icon
    protected final boolean emailOnly
    protected final int order
    protected String title
    protected String content
    protected BuildSummaryManager manager

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

    String getId() {
        return id
    }

    String getIcon() {
        return icon
    }

    String getTitle() {
        return title
    }

    @NonCPS
    void setTitle(String title) {
        this.title = title
    }

    String getContent() {
        return content
    }

    @NonCPS
    void setContent(String content) {
        this.content = content
    }

    int getOrder() {
        return order
    }

    boolean isEmailOnly() {
        return emailOnly
    }

    String toHtml() {
        return """
            <h1 style="display: inline-block">${getTitle()}</h1>
            ${getContent()}
        """
    }

    @Override
    int compareTo(SummaryInfo o) {
        return Integer.compare(getOrder(), o.getOrder())
    }

    BuildSummaryManager getManager() {
        return manager
    }

    @NonCPS
    void setManager(BuildSummaryManager manager) {
        this.manager = manager
    }

    @NonCPS
    protected publishAutomatically(final context) {
        if (manager != null) {
            manager.publishAutomatically(context)
        }
    }
}