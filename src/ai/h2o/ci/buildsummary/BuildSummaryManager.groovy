package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

interface BuildSummaryManager {
    /**
     * Used to automatically publish (if enabled) the content of the {@link BuildSummary} to the build details page
     * @param context
     */
    @NonCPS
    void publishAutomatically(final context)
}