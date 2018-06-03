package ai.h2o.ci

/**
 * Class responsible for sending emails. This class adds a default email header div to the specified HTML content
 * before sending it to the recipients.
 */
class Emailer implements Serializable {

    private static final String LOGO_URL = 'https://pbs.twimg.com/profile_images/501572396810129408/DTgFCs-n.png'
    private static final BACKGROUND_COLORS = [
            (BuildResult.SUCCESS): '#84e03e',
            (BuildResult.FAILURE): '#f8433c',
            (BuildResult.WARNING): '#f49242'
    ]
    private static final String DEFAULT_BACKGROUND_COLOR = BACKGROUND_COLORS[BuildResult.FAILURE]

    /**
     * Adds the default email header to the specified content and sends this to given recipients. Example usage:
     * <pre>
     *     <code>
     *          Emailer.sendEmail(BuildResult.SUCCESS, buildSummary.get().toEmail(this), ['recipient@h2o.ai', 'recipient2@h2o.ai'])
     *      </code>
     * </pre>
     * @param context
     * @param result result of the build/stage
     * @param content HTML content of the email
     * @param recipients list of email addresses
     * @param verbose if true, then echo the email before sending; false by default
     */
    static void sendEmail(
            final context,
            final BuildResult result,
            final String content, final List<String> recipients, final boolean verbose = false) {
        context.echo "Sending email to ${recipients}"

        final String headerDiv = """
            <div style="box-shadow: 0px 5px 5px #aaaaaa;background-color: #424242;color: white;"> 
                <div style="display: table;overflow: hidden;height: 150px">
                    <div style="display: table-cell;vertical-align: middle;padding-left: 1em">
                        <div>
                            <img width="80" height="80" alt="H2O.ai" title="H2O.ai" style="vertical-align:middle;" src="${LOGO_URL}"/>
                            <a style="vertical-align:middle;color: white;font-size: 20pt;font-weight: bolder;margin-left: 20px;" href="${context.currentBuild.rawBuild.getAbsoluteUrl()}">${URLDecoder.decode(context.env.JOB_NAME, "UTF-8")} #${context.currentBuild.number} - ${result.getText()}</a>
                        </div>
                    </div>
                </div>
                <div style="height: 15px; background-color:${BACKGROUND_COLORS[result] ?: DEFAULT_BACKGROUND_COLOR}"></div>
            </div>
        """

        final String emailBody = """
            <div>
                ${headerDiv}
                <div style="border: 1px solid gray; padding: 1em;">
                    ${content}
                </div>
            </div>
        """

        if (verbose) {
            context.echo emailBody
        }
        context.emailext(
                subject: "${context.env.JOB_NAME.split('/')[0]}: ${result}",
                body: emailBody,
                to: recipients.join(' ')
        )
    }
}