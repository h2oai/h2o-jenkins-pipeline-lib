package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

/**
 * {@link DetailsSummary} base class. This {@link DetailsSummary} creates an empty {@link DetailsSummary}. Should be subclassed
 */
class DetailsSummary extends SummaryInfo implements Serializable {
    private static final String ID = 'details'
    private static final String ICON = 'notepad.gif'
    private static final String TITLE = 'Details'

    private Map entries = [:]

    DetailsSummary() {
        super(ID, ICON, false, Integer.MAX_VALUE)
        setTitle(TITLE)
    }

    /**
     * Returns value for entry with given name, or null if no such entry exists
     * @param name name of the required entry
     * @return value for entry with given name, or null if no such entry exists
     */
    String getEntry(final String name) {
        return entries[name]
    }

    /**
     * Sets the value of the entry with given name. If parent {@link BuildSummary} has autoPublish enabled, updates the content.
     * @param context
     * @param name name of the entry, if this name already exists, value is overwritten
     * @param value value to store
     */
    @NonCPS
    void setEntry(final context, final String name, final String value) {
        entries[name] = value
        setContent(buildContent())
        publishAutomatically(context)
    }

    /**
     * Return HTML representation of this {@link DetailsSummary}
     * @return
     */
    @NonCPS
    String buildContent() {
        String entriesHtml = ''
        for (String name : entries.keySet()) {
            entriesHtml += "<li><strong>${name}:</strong> ${entries[name]}</li>"
        }
        return "<ul>${entriesHtml}</ul>"
    }

}
