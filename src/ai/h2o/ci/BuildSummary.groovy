package ai.h2o.ci

/**
 * Class for creating build summaries. Used for example for job detail page or email.
 */
class BuildSummary {

    /** ID of Details section */
    public static final String DETAILS_SECTION_ID = 'details'
    /** ID of Changes section */
    public static final String CHANGES_SECTION_ID = 'changes'

    /** CSS Style used for table element in this BuildSummary */
    public static final String TABLE_STYLE = 'margin-left: 1em; border-collapse: collapse'
    /** CSS Style used for td element in this BuildSummary */
    public static final String TD_STYLE = 'vertical-align: middle; border: 1px solid black; padding: 0.3em 1em;'
    /** CSS Style used for th ielement n this BuildSummary */
    public static final String TH_STYLE = 'vertical-align: middle; border: 1px solid black; padding: 0.5em;'

    private final String repoUrl
    private final List<StageSummary> stageSummaries = []
    private final List<Section> sections = []
    private final updateJobDescription

    /**
     * Constructor for BuildSummary, which will automatically populate the build description
     * @param repoUrl url of repository which commit is being built
     */
    BuildSummary(final String repoUrl) {
        this(repoUrl, true)
    }

    /**
     *
     * @param repoUrl url of repository which commit is being built
     * @param updateJobDescription if true, then the build description will be updated automatically after each change
     */
    BuildSummary(final String repoUrl, final boolean updateJobDescription) {
        this.repoUrl = repoUrl
        this.updateJobDescription = updateJobDescription
    }

    /**
     * Adds a section to this BuildSummary. Example usage:
     * stage {
     *     steps {
     *         script {
     *             // ... rest of the stage code
     *             buildSummary.addSection(this, 'Custom Section', '<p>Custom HTML content</p>')
     *             // ... rest of the stage code
     *         }
     *     }
     * }
     * @param context script, use 'this' when calling from pipeline script
     * @param id unique ID of the Section
     * @param title title of the section, might be HTML
     * @param contentHTML HTML content of the Section
     * @return added Section
     * @throws IllegalArgumentException if Section with the same id exists in this BuildSummary
     */
    Section addSection(final context, final String id, final String title, final String contentHTML) {
        if (findSection(id) != null) {
            throw new IllegalArgumentException('Section with id %s already.exists'.format(id))
        }
        def section = new Section(id, title, contentHTML)
        sections.add(section)
        updateJobDescriptionIfRequired(context)
        return section
    }

    /**
     * Adds a section to this BuildSummary
     * @param context script, use 'this' when calling from pipeline script
     * @param section Section to add. Might be a Section from another BuildSummary
     * @return added Section
     * @throws IllegalArgumentException if Section with the same id as the passed Section exists in this BuildSummary
     */
    Section addSection(final context, final Section section) {
        if (findSection(section.getId()) != null) {
            throw new IllegalArgumentException('Section with id %s already.exists'.format(section.getId()))
        }
        sections.add(section)
        updateJobDescriptionIfRequired(context)
        return section
    }

    /**
     * Finds a Section with specified ID
     * @param id ID of the section
     * @return Section with specified ID or null if there is no such Section in this BuildSummary
     */
    Section findSection(final String id) {
        return sections.find({ it.getId() == id })
    }

    /**
     * Finds a Section with specified ID
     * @param id ID of the section
     * @return Section with specified ID
     * @throws IllegalStateException if there is no Section with given ID in this BuildSummary
     */
    Section findSectionOrThrow(final String id) {
        def section = findSection(id)
        if (section == null) {
            throw new IllegalStateException("Cannot find section with id %s".format(id))
        }
        return section
    }

    /**
     * Adds Details section which contains:
     * <ul>
     *     <li>Commit Message - read from <strong>env.COMMIT_MESSAGE</strong></li>
     *     <li>Commit Message - read from <strong>env.BRANCH_NAME</strong></li>
     *     <li>Commit Message - read from <strong>env.GIT_SHA</strong></li>
     * </ul>
     * @param context script, use 'this' when calling from pipeline script
     * @return added Section
     */
    Section addDetailsSection(final context) {
        return addSection(context, DETAILS_SECTION_ID, "<a href=\"${context.currentBuild.rawBuild.getAbsoluteUrl()}\" style=\"color: black;\">Details</a>", """
<ul>
    <li><strong>Commit Message:</strong> ${context.env.COMMIT_MESSAGE}</li>
    <li><strong>Git Branch:</strong> ${context.env.BRANCH_NAME}</li>
    <li><strong>Git SHA:</strong> ${context.env.GIT_SHA}</li>
</ul>
                """)
    }

    /**
     * Adds Changes section , if there are any changes, which contains the following for each change:
     * <ul>
     *     <li>Link to change</li>
     *     <li>Author email</li>
     *     <li>Commit Message</li>
     * </ul>
     * @param context script, use 'this' when calling from pipeline script
     * @return added Section
     */
    Section addChangesSectionIfNecessary(final context) {

        def changesContent = ''
        context.currentBuild.rawBuild.getChangeSets().each { changeSetList ->
            if (changeSetList.getBrowser().getRepoUrl() == repoUrl) {
                changesContent += "<ul>"
                changeSetList.each { changeSet ->
                    changesContent += """
<li>
    <a href=\"${repoUrl}/commit/${changeSet.getRevision()}\">
        <strong>${changeSet.getRevision().substring(0, 8)}</strong>
    </a> by <strong>${changeSet.getAuthorEmail()}</strong> - ${changeSet.getMsg()}
</li>
                            """
                }
                changesContent += "</ul>"
            }
        }

        Section section = null
        if (changesContent != '') {
            section = addSection(context, CHANGES_SECTION_ID, 'Changes', changesContent)
        }
        return section
    }

    /**
     * Creates a new row in the Stages Overview. Example usage:
     * <pre>
     *     stage('My Stage') {
     *         steps {
     *             script {
     *                 final String stageName = 'My Stage'
     *                 buildSummary.addStageSummary(this, stageName, 'subdir/of/stage')
     *                 buildSummary.setStageDetails(this, stageName, env.NODE_NAME, env.WORKSPACE)
     *                 try {
     *                     // ..............
     *                     // . Stage code .
     *                     // ..............
     *                     buildSummary.markStageSuccessful(this, stageName)
     *                 } catch (Exception e) {
     *                     buildSummary.markStageFailed(this, stageName)
     *                 }
     *             }
     *         }
     *     }
     * @param context script, use 'this' when calling from pipeline script
     * @param stageName name of the stage
     * @return added StageSummary
     */
    StageSummary addStageSummary(final context, final String stageName) {
        return addStageSummary(context, stageName, '')
    }

    /**
     * Creates a new row in the Stages Overview
     * @param context script, use 'this' when calling from pipeline script
     * @param stageName name of the stage
     * @param stageFolderSuffix relative path to the folder (without the ./ in the beginning),
     * where this stage is executed. Use <strong>empty<strong> string if stage is executed in $WORKSPACE
     * @return added StageSummary
     */
    StageSummary addStageSummary(final context, final String stageName, final String stageFolderSuffix) {
        if (findStageSummaryWithName(stageName) != null) {
            throw new IllegalArgumentException(String.format("StageSummary Summary with name %s already defined", stageName))
        }
        def stage = new StageSummary(stageName, stageFolderSuffix)
        stageSummaries.add(stage)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    /**
     * Indicates that stage has been successful
     * @param context script, use 'this' when calling from pipeline script
     * @param stageName name of the stage
     * @return affected StageSummary
     */
    StageSummary markStageSuccessful(final context, final String stageName) {
        final StageSummary stage = setStageResult(stageName, BuildResult.SUCCESS)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    /**
     * Indicates that stage has failed
     * @param context script, use 'this' when calling from pipeline script
     * @param stageName name of the stage
     * @return affected StageSummary
     */
    StageSummary markStageFailed(final context, final String stageName) {
        final StageSummary stage = setStageResult(stageName, BuildResult.FAILURE)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    /**
     * Sets additional information for the stage
     * @param context script, use 'this' when calling from pipeline script
     * @param stageName name of the stage
     * @param nodeName name of the node, where the stage is being executed
     * @param workspacePath path to workspace, env.WORKSPACE might be used
     * @return affected StageSummary
     */
    StageSummary setStageDetails(
            final context, final String stageName, final String nodeName, final String workspacePath) {
        def stage = findStageSummaryWithNameOrThrow(stageName)
        stage.setNodeName(nodeName)
        stage.setWorkspace(workspacePath)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    /**
     * Returns HTML representation of this BuildSummary
     * @param context script, use 'this' when calling from pipeline script
     * @return HTML representation of this BuildSummary
     */
    String getSummaryHTML(final context) {

        String stagesSection = ''
        String stagesTableBody = ''

        if (!stageSummaries.isEmpty()) {
            for (stageSummary in stageSummaries) {
                def nodeName = stageSummary.getNodeName() ?: 'Not yet allocated'
                BuildResult result = stageSummary.getResult() ?: BuildResult.PENDING
                stagesTableBody += """
                        <tr style="background-color: ${stageResultToBgColor(stageSummary.getResult())}">
                <td style="${TD_STYLE}">${stageSummary.getName()}</td>
                <td style="${TD_STYLE}">${nodeName}</td>
                <td style="${TD_STYLE}">${stageSummary.getWorkspaceText()}</td>
                <td style="${TD_STYLE}">${stageSummary.getArtifactsHTML(context)}</td>
                <td style="${TD_STYLE}">${result.getText()}</td>
              </tr>
                        """
            }
            stagesSection = createHTMLForSection('Stages Overview', """
                    <table style="${TABLE_STYLE}">
                    <thead>
                <tr>
                  <th style="${TH_STYLE}">Name</th>
                  <th style="${TH_STYLE}">Node</th>
                  <th style="${TH_STYLE}">Workspace</th>
                  <th style="${TH_STYLE}">Artifacts</th>
                  <th style="${TH_STYLE}">Result</th>
                </tr>
              </thead>
              <tbody>
                    ${stagesTableBody}
              </tbody>
            </table>
                    """, false, !sections.isEmpty())
        }

        String sectionsHTML = ''
        sections.eachWithIndex { Section section, int i ->
            sectionsHTML += createHTMLForSection(section.getTitle(), section.getContent(), (i + 1) < sections.size(), false)
        }

        return """
            <div style="border: 1px solid #d3d7cf; padding: 0em 1em 1em 1em;">
                ${sectionsHTML}
                ${stagesSection}
            </div>"""
    }

    private void updateJobDescriptionIfRequired(final context) {
        if (updateJobDescription) {
            context.currentBuild.description = getSummaryHTML(context)
        }
    }

    private static String createHTMLForSection(
            final String title, final String content, final boolean borderBottom, final boolean borderTop) {
        String borderBottomValue = ''
        if (borderBottom) {
            borderBottomValue = 'border-bottom: 1px dashed gray;'
        }

        String borderTopValue = ''
        if (borderTop) {
            borderTopValue = 'border-top: 1px dashed gray;'
        }
        return """
                <div style="margin-bottom: 15px;${borderBottomValue}${borderTopValue}">
                    <h3>${title}</h3>
                    <div style="margin-left: 15px;">
                        ${content}
                    </div>
                </div>"""
    }

    private setStageResult(final String stageName, final BuildResult result) {
        def summary = findStageSummaryWithNameOrThrow(stageName)
        summary.setResult(result)
        return summary
    }

    private static String stageResultToBgColor(final BuildResult result) {
        final String BG_COLOR_SUCCESS = '#a8ff8e'
        final String BG_COLOR_FAILURE = '#fe9272'
        final String BG_COLOR_OTHER = '#fbf78b'

        if (result == BuildResult.SUCCESS) {
            return BG_COLOR_SUCCESS
        }
        if (result == BuildResult.FAILURE) {
            return BG_COLOR_FAILURE
        }
        return BG_COLOR_OTHER
    }

    private def findStageSummaryWithName(final String stageName) {
        return stageSummaries.find({ it.getName() == stageName })
    }

    private def findStageSummaryWithNameOrThrow(final String stageName) {
        def summary = findStageSummaryWithName(stageName)
        if (summary == null) {
            throw new IllegalArgumentException('Cannot find StageSummary with name %s'.format(stageName))
        }
        return summary
    }

    /**
     * Class representing a Section in BuildSummary
     */
    static class Section {
        private final String id
        private String title
        private String content

        Section(final String id, final String title, final String content) {
            this.id = id
            this.title = title
            this.content = content
        }

        String getId() {
            return id
        }

        String getTitle() {
            return title
        }

        String getContent() {
            return content
        }

        void setTitle(String title) {
            this.title = title
        }

        void setContent(String content) {
            this.content = content
        }
    }

    /**
     * Class representing a stage in Stages Overview of the BuildSummary
     */
    static class StageSummary {
        private final String name
        private final String stageFolderSuffix
        private String nodeName
        private String workspace
        private BuildResult result

        StageSummary(final String name, final String stageFolderSuffix) {
            this.name = name
            this.stageFolderSuffix = stageFolderSuffix
            this.result = BuildResult.PENDING
        }

        String getName() {
            return name
        }

        String getNodeName() {
            return nodeName
        }

        void setNodeName(String nodeName) {
            this.nodeName = nodeName
        }

        String getWorkspace() {
            return workspace
        }

        void setWorkspace(String workspace) {
            this.workspace = workspace
        }

        BuildResult getResult() {
            return result
        }

        void setResult(BuildResult result) {
            this.result = result
        }

        String getWorkspaceText() {
            return getWorkspace() ?: 'Not yet allocated'
        }

        String getArtifactsHTML(final context) {
            if (result == BuildResult.PENDING) {
                return 'Not yet available'
            }
            return "<a href=\"${context.currentBuild.rawBuild.getAbsoluteUrl()}artifact/${stageFolderSuffix}/\" target=\"_blank\" style=\"color: black;\">Artifacts</a>"
        }
    }
}
