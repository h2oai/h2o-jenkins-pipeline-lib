package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

interface BuildSummaryManager {
    @NonCPS
    void publishAutomatically(final context)
}