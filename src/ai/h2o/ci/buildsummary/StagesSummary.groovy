package ai.h2o.ci.buildsummary

import hudson.Util

import ai.h2o.ci.BuildResult

class StagesSummary extends SummaryInfo {
    private static final String ID = 'stages'
    private static final String ICON = 'computer.gif'
    private static final String TITLE = 'Stages Overview'

    public static final String TABLE_STYLE = 'border-collapse: collapse'
    public static final String TD_STYLE = 'vertical-align: middle; border: 1px solid #b1b1b1; padding: 0.3em 1em;'
    public static final String TH_STYLE = 'vertical-align: middle; border: 1px solid #b1b1b1; padding: 0.5em;'

    private final List<StageInfo> stages = []

    protected StagesSummary() {
        super(ID, ICON, false, Integer.MIN_VALUE)
        setTitle(TITLE)
    }

    void addStage(final context, final StageInfo stageInfo) {
        stages.add(stageInfo)
        updateContent(context)
    }

    void stage(final context, final String name, final String stageDirName, final String rerunCommand, final Closure body) {
        final StageInfo stageInfo = new StageInfo(name, stageDirName, rerunCommand)
        addStage(context, stageInfo)
        try {
            setStageDetails(context, name, true)
            body()
            markStageSuccessful(context, name)
        } catch (Exception e) {
            markStageFailed(context, name)
            throw e
        }
    }
    
    void markStageSuccessful(final context, final String stageName) {
        setStageResult(stageName, BuildResult.SUCCESS)
        updateContent(context)
    }

    void markStageFailed(final context, final String stageName) {
        setStageResult(stageName, BuildResult.FAILURE)
        updateContent(context)
    }

    void setStageDetails(final context, final String stageName, final boolean resetTimer = true) {
        setStageDetails(context, stageName, context.env.NODE_NAME, context.env.WORKSPACE, resetTimer)
    }

    void setStageDetails(final context, final String stageName, final String nodeName, final String workspacePath, final boolean resetTimer) {
        final StageInfo stage = findStageInfoWithNameOrThrow(stageName)
        stage.setNodeName(nodeName)
        stage.setWorkspace(workspacePath)
        if (resetTimer) {
            stage.setStartTime()
        }
        updateContent(context)
    }

    void setStageUrl(final context, final String stageName, String url) {
        if (!url) {
            url = getStageDefaultUrl(context, stageName)
        }
        final StageInfo stage = findStageInfoWithNameOrThrow(stageName)
        stage.setUrl(url)
        updateContent(context)
    }

    @NonCPS
    private String getStageDefaultUrl(final context, final String stageName) {
        def node = context.currentBuild.rawBuild.getAction(org.jenkinsci.plugins.workflow.job.views.FlowGraphAction.class).getNodes().find{ it.getDisplayName() == stageName }
        
        // check if there is node with Branch prefix
        def branchNode = node
        while (branchNode != null) {
            if (branchNode.getDisplayName().startsWith('Branch: ')) {
                break
            }
            def parents = branchNode.getParents()
            if (parents) {
                branchNode = parents[0]
            } else {
                branchNode = null
            }
        }
        node = branchNode ?: node
        
        if (node) {
            return "${context.env.JENKINS_URL}/blue/rest/organizations/jenkins/pipelines/${context.env.JOB_NAME.split('/').join('/branches/')}/runs/${context.currentBuild.number}/nodes/${node.getId()}/log"
        }
        return null
    }

    private StageInfo findStageInfoWithName(final String stageName) {
        return stages.find({ it.getName() == stageName })
    }

    private StageInfo findStageInfoWithNameOrThrow(final String stageName) {
        final StageInfo summary = findStageInfoWithName(stageName)
        if (summary == null) {
            throw new IllegalArgumentException('Cannot find StageSummary with name %s'.format(stageName))
        }
        return summary
    }

    private void setStageResult(final String stageName, final BuildResult result) {
        final StageInfo summary = findStageInfoWithNameOrThrow(stageName)
        summary.setResult(result)
    }

    private void updateContent(final context) {
        setContent(buildContent(context))
        publishAutomatically(context)
    }

    private String buildContent(final context) {
        String stagesTableBody = ''

        for (stageSummary in stages) {
            final BuildResult result = stageSummary.getResult() ?: BuildResult.PENDING
            stagesTableBody += """
                <tr>
                    <td style="${TD_STYLE}"><img src="${result.getImageUrl(context, BuildSummaryUtils.ImageSize.LARGE)}" alt="${result.getText()}"/></td>
                    <td style="${TD_STYLE}">${stageSummary.getNameForOverview()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getNodeNameText(context)}</td>
                    <td style="${TD_STYLE}">${stageSummary.getWorkspaceText()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getArtifactsHTML(context)}</td>
                    <td style="${TD_STYLE}">${stageSummary.getDuration()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getQueueTime()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getRestartStatus(context)}</td>
                </tr>
            """
        }

        return """
            <table style="${TABLE_STYLE}">
                <thead>
                <tr>
                    <th style="${TH_STYLE}"></th>
                    <th style="${TH_STYLE}">Name</th>
                    <th style="${TH_STYLE}">Node</th>
                    <th style="${TH_STYLE}">Workspace</th>
                    <th style="${TH_STYLE}">Artifacts</th>
                    <th style="${TH_STYLE}">Duration</th>
                    <th style="${TH_STYLE}">Queue Time</th>
                    <th style="${TH_STYLE}"></th>
                </tr>
                </thead>
                <tbody>
                    ${stagesTableBody}
                </tbody>
            </table>
        """
    }

    class StageInfo {
        private final String name
        private final String stageDirName
        private String nodeName
        private String workspace
        private BuildResult result
        private long startTime
        private long endTime
        private long totalTimeInQueue = 0L
        private long lastNodeStartTime = 0L
        private String url
        private String rerunCommand

        StageInfo(String name, String stageDirName, String rerunCommand) {
            this.name = name
            this.stageDirName = stageDirName
            this.result = BuildResult.PENDING
            this.lastNodeStartTime = System.currentTimeMillis()
            this.rerunCommand = rerunCommand
        }

        String getName() {
            return name
        }

        String getNameForOverview() {
            if (url) {
                return "<a href=\"${url}\">${name}</a>"
            }
            return getName()
        }

        String getStageDirName() {
            return stageDirName
        }

        String getNodeName() {
            return nodeName
        }

        String getNodeNameText(final context) {
            if (nodeName == null) {
                return 'Not yet allocated'
            } else {
                return "<a href=\"${context.env.HUDSON_URL}computer/${nodeName}\" target=\"_blank\" style=\"color: black;\">${nodeName}</a>"
            }
        }

        void setNodeName(String nodeName) {
            def newNodeName = nodeName ?: 'Not yet allocated'
            if (this.nodeName == null && newNodeName != 'Not yet allocated') {
                this.totalTimeInQueue += System.currentTimeMillis() - this.lastNodeStartTime
            }
            if (this.nodeName != null && newNodeName == 'Not yet allocated') {
                this.lastNodeStartTime = System.currentTimeMillis()
            }

            this.nodeName = nodeName
        }

        String getWorkspace() {
            return workspace
        }

        String getWorkspaceText() {
            return workspace ?: 'Not yet allocated'
        }

        void setWorkspace(String workspace) {
            this.workspace = workspace
        }

        BuildResult getResult() {
            return result
        }

        void setResult(BuildResult result) {
            this.result = result
            setEndTime()
        }

        void setStartTime() {
            setStartTime(System.currentTimeMillis())
        }

        void setStartTime(long startTime) {
            this.startTime = startTime
        }

        void setEndTime() {
            setEndTime(System.currentTimeMillis())
        }

        void setEndTime(long endTime) {
            this.endTime = endTime
        }

        void setUrl(String url) {
            this.url = url
        }

        String getArtifactsHTML(final context) {
            if (result == BuildResult.PENDING) {
                return 'Not yet available'
            }
            return "<a href=\"${context.currentBuild.rawBuild.getAbsoluteUrl()}artifact/${stageDirName}/\" target=\"_blank\" style=\"color: black;\">Artifacts</a>"
        }

        String getDuration() {
            if (result == BuildResult.PENDING) {
                return "In progress" + (startTime ? " since ${new Date(startTime).format('hh:mm aa dd/MMM (z)')}" : "...")
            }
            return "${Util.getTimeSpanString(endTime - startTime)}"
        }

        String getQueueTime() {
            return "${Util.getTimeSpanString(totalTimeInQueue)}"
        }

        String getRestartStatus(final context) {
            if (rerunCommand != '' && result == BuildResult.FAILURE) {
                return "<a href=\"${context.env.JOB_URL}buildWithParameters?more_tests=${rerunCommand}\" target=\"_blank\" style=\"color: black;\">rerun</a>"
            } else {
                return '-'
            }
        }
    }
}
