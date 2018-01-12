package ai.h2o.ci

class BuildSummary {

    public static final String RESULT_PENDING = 'pending'
    public static final String RESULT_SUCCESS = 'success'
    public static final String RESULT_FAILURE = 'failure'
    public static final String RESULT_WARNING = 'warning'

    public static final String DETAILS_SECTION_ID = 'details'
    public static final String CHANGES_SECTION_ID = 'changes'

    public static final String TABLE_STYLE = 'margin-left: 1em; border-collapse: collapse'
    public static final String TD_STYLE = 'vertical-align: middle; border: 1px solid black; padding: 0.3em 1em;'
    public static final String TH_STYLE = 'vertical-align: middle; border: 1px solid black; padding: 0.5em;'

    private static final String REPO_URL = 'https://github.com/h2oai/h2o-3'

    private final List<Stage> stageSummaries = []
    private final List<Section> sections = []
    private final updateJobDescription

    BuildSummary(final boolean updateJobDescription) {
        this.updateJobDescription = updateJobDescription
    }

    static BuildSummary newInstance(final boolean updateJobDescription) {
        return new BuildSummary(updateJobDescription)
    }

    Section addSection(final context, final String id, final String title, final String contentTemplate) {
        if (findSection(id) != null) {
            throw new IllegalArgumentException('Section with id %s already.exists'.format(id))
        }
        def section = new Section(id, title, contentTemplate)
        sections.add(section)
        updateJobDescriptionIfRequired(context)
        return section
    }

    Section addSection(final context, final Section section) {
        if (findSection(section.getId()) != null) {
            throw new IllegalArgumentException('Section with id %s already.exists'.format(section.getId()))
        }
        sections.add(section)
        updateJobDescriptionIfRequired(context)
        return section
    }

    Section findSection(final String id) {
        return sections.find({it.getId() == id})
    }

    Section findSectionOrThrow(final String id) {
        def section = findSection(id)
        if (section == null) {
            throw new IllegalStateException("Cannot find section with id %s".format(id))
        }
        return section
    }

    Section addDetailsSection(final context) {
        return addSection(context, DETAILS_SECTION_ID, "<a href=\"${context.currentBuild.rawBuild.getAbsoluteUrl()}\" style=\"color: black;\">Details</a>", """
                <ul>
                <li><strong>Commit Message:</strong> ${context.env.COMMIT_MESSAGE}</li>
          <li><strong>Git Branch:</strong> ${context.env.BRANCH_NAME}</li>
          <li><strong>Git SHA:</strong> ${context.env.GIT_SHA}</li>
        </ul>
                """)
    }

    Section addChangesSectionIfNecessary(final context) {

        def changesContent = ''
        context.currentBuild.rawBuild.getChangeSets().each { changeSetList ->
            if (changeSetList.getBrowser().getRepoUrl() == REPO_URL) {
                changesContent += "<ul>"
                changeSetList.each { changeSet ->
                        changesContent += """
                        <li>
                    <a href=\"${REPO_URL}/commit/${changeSet.getRevision()}\">
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

    Stage addStageSummary(final context, final String stageName, final String stageDirName) {
        if (findStageSummaryWithName(stageName) != null) {
            throw new IllegalArgumentException(String.format("Stage Summary with name %s already defined", stageName))
        }
        def stage = new Stage(stageName, stageDirName)
        stageSummaries.add(stage)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    Stage markStageSuccessful(final context, final String stageName) {
        final Stage stage = setStageResult(stageName, RESULT_SUCCESS)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    Stage markStageFailed(final context, final String stageName) {
        final Stage stage = setStageResult(stageName, RESULT_FAILURE)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    Stage setStageDetails(final context, final String stageName, final String nodeName, final String workspacePath) {
        def stage = findStageSummaryWithNameOrThrow(stageName)
        stage.setNodeName(nodeName)
        stage.setWorkspace(workspacePath)
        updateJobDescriptionIfRequired(context)
        return stage
    }

    String getSummaryHTML(final context) {

        String stagesSection = ''
        String stagesTableBody = ''

        if (!stageSummaries.isEmpty()) {
            for (stageSummary in stageSummaries) {
                def nodeName = stageSummary.getNodeName() ?: 'Not yet allocated'
                def result = stageSummary.getResult() ?: RESULT_PENDING.capitalize()
                stagesTableBody += """
                        <tr style="background-color: ${stageResultToBgColor(stageSummary.getResult())}">
                <td style="${TD_STYLE}">${stageSummary.getName()}</td>
                <td style="${TD_STYLE}">${nodeName}</td>
                <td style="${TD_STYLE}">${stageSummary.getWorkspaceText()}</td>
                <td style="${TD_STYLE}">${stageSummary.getArtifactsHTML(context)}</td>
                <td style="${TD_STYLE}">${result.capitalize()}</td>
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
        </div>
                """
    }

    private void updateJobDescriptionIfRequired(final context) {
        if (updateJobDescription) {
            context.currentBuild.description = getSummaryHTML(context)
        }
    }

    private static String createHTMLForSection(final String title, final String content, final boolean borderBottom, final boolean borderTop) {
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
        </div>
                """
    }

    private setStageResult(final String stageName, final String result) {
        def summary = findStageSummaryWithNameOrThrow(stageName)
        summary.setResult(result)
        return summary
    }

    private static String stageResultToBgColor(final String result) {
        def BG_COLOR_SUCCESS = '#a8ff8e'
        def BG_COLOR_FAILURE = '#fe9272'
        def BG_COLOR_OTHER = '#fbf78b'

        if (result == RESULT_SUCCESS) {
            return BG_COLOR_SUCCESS
        }
        if (result == RESULT_FAILURE) {
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

    static class Stage {
        private final String name
        private final String stageDirName
        private String nodeName
        private String workspace
        private String result

        Stage(final String name, final String stageDirName) {
            this.name = name
            this.stageDirName = stageDirName
            this.result = RESULT_PENDING
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

        String getResult() {
            return result
        }

        void setResult(String result) {
            this.result = result
        }

        String getWorkspaceText() {
            return getWorkspace() ?: 'Not yet allocated'
        }

        String getArtifactsHTML(final context) {
            if (result == RESULT_PENDING) {
                return 'Not yet available'
            }
            return "<a href=\"${context.currentBuild.rawBuild.getAbsoluteUrl()}artifact/${stageDirName}/\" target=\"_blank\" style=\"color: black;\">Artifacts</a>"
        }
    }
}
