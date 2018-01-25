package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

class DetailsSummary extends SummaryInfo implements Serializable {
    private static final String ID = 'details'
    private static final String ICON = 'notepad.gif'
    private static final String TITLE = 'Details'

    private Map entries = [:]

    DetailsSummary() {
        super(ID, ICON, false, Integer.MAX_VALUE)
        setTitle(TITLE)
    }

    String getEntry(final String name) {
        return entries[name]
    }

    @NonCPS
    setEntry(final context, final String name, final String value) {
        entries[name] = value
        setContent(buildContent())
//        publishAutomatically(context)
    }

    @NonCPS
    String buildContent() {
        String entriesHtml = ''
        for (String name : entries.keySet()) {
            entriesHtml += "<li><strong>${name}:</strong> ${entries[name]}</li>"
        }
        return "<ul>${entriesHtml}</ul>"
    }

}
