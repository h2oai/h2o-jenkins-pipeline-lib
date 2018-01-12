package ai.h2o.ci

class Emailer implements Serializable {

    private static final String LOGO_URL = 'https://pbs.twimg.com/profile_images/501572396810129408/DTgFCs-n.png'
    private static final BACKGROUND_COLORS = [
            (BuildResult.SUCCESS): '#84e03e',
            (BuildResult.FAILURE): '#f8433c',
            (BuildResult.WARNING): '#f49242'
    ]
    private static final String DEFAULT_BACKGROUND_COLOR = BACKGROUND_COLORS[BuildResult.FAILURE]

    static void sendEmail(final context, final BuildResult result, final String content, final List<String> recipients) {
        context.echo "Sending email to ${recipients}"

        final String headerDiv = """
            <div style="box-shadow: 0px 5px 5px #aaaaaa;background-color: #424242;color: white;"> 
                <div style="display: table;overflow: hidden;height: 150px">
                    <div style="display: table-cell;vertical-align: middle;padding-left: 1em">
                        <div>
                            <img width="80" height="80" alt="H2O.ai" title="H2O.ai" style="vertical-align:middle;" src="${LOGO_URL}"/>
                            <a style="vertical-align:middle;color: white;font-size: 20pt;font-weight: bolder;margin-left: 20px;" href="${context.currentBuild.rawBuild.getAbsoluteUrl()}">${URLDecoder.decode(context.env.JOB_NAME, "UTF-8")} #${context.currentBuild.number} - ${result.toUpperCase()}</a>
                        </div>
                    </div>
                </div>
                <div style="height: 15px; background-color:${BACKGROUND_COLORS[result] ?: DEFAULT_BACKGROUND_COLOR}"></div>
            </div>
        """

        final String emailBody = """
            <div>
                ${headerDiv}
                ${content}
            </div>
        """

        context.echo emailBody
        context.emailext(
                subject: "${context.env.JOB_NAME.split('/')[0]}: ${result}",
                body: emailBody,
                to: recipients.join(' ')
        )
    }
}