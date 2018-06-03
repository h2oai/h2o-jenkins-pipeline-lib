package ai.h2o.ci.buildsummary

import com.cloudbees.groovy.cps.NonCPS

/**
 * Custom implementation of the {@link DetailsSummary}. The {@link SimpleDetailsSummary} contains commit author, branch name, sha and message of the commit
 */
class SimpleDetailsSummary extends DetailsSummary implements Serializable {
    private static final String SHA_NAME = 'SHA'
    private static final String BRANCH_NAME = 'Branch'
    private static final String MESSAGE_NAME = 'Message'
    private static final String AUTHOR_NAME = 'Author'

    private final String sha
    private final String branch
    private final String message
    private final String author

    SimpleDetailsSummary(final context, final String sha, final String branch, final String message, final String author) {
        super()
        this.author = author
        this.message = message
        this.branch = branch
        this.sha = sha
        updateEntries(context)
    }

    String getSha() {
        return sha
    }

    String getBranch() {
        return branch
    }

    String getMessage() {
        return message
    }

    String getAuthor() {
        return author
    }

    @NonCPS
    private void updateEntries(final context) {
        setEntry(context, SHA_NAME, sha)
        setEntry(context, BRANCH_NAME, branch)
        setEntry(context, MESSAGE_NAME, message)
        setEntry(context, AUTHOR_NAME, author)
    }
}

