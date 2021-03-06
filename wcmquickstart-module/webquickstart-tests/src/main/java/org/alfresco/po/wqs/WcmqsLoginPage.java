package org.alfresco.po.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public class WcmqsLoginPage extends SharePage
{
    private static final By USERNAME_INPUT = By.cssSelector("input[id='wef-login-panel-username']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[id='wef-login-panel-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[id='wef-login-panel-btn-login-button']");
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     */
    public WcmqsLoginPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsLoginPage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(USERNAME_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(LOGIN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsLoginPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsLoginPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for input username
     *
     * @param userName
     * @return
     */
    public void inputUserName(String userName)
    {
        try
        {
            drone.findAndWait(USERNAME_INPUT).clear();
            drone.findAndWait(USERNAME_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(userName);
        }
        catch (TimeoutException e)
        {
            logger.error("The username input was not found", e);
        }
    }

    /**
     * Method for input password
     *
     * @param password
     * @return
     */
    public void inputPassword(String password)
    {
        try
        {
            drone.findAndWait(PASSWORD_INPUT).clear();
            drone.findAndWait(PASSWORD_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(password);
        }
        catch (TimeoutException e)
        {

        }
    }

    /**
     * Method for click Login Button
     *
     * @param
     * @return
     */

    public void clickLoginButton()
    {
        try
        {
            drone.findAndWait(LOGIN_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            logger.error("The Login button was  not found", e);
        }

    }

    /**
     * Method for login to Alfresco Web Editor
     *
     * @param userName
     * @param password
     * @return My Alfresco Page
     */
    public void login(String userName, String password)
    {
        try
        {
            logger.info("Login to Alfresco Web Editor");
            inputUserName(userName);
            inputPassword(password);
            clickLoginButton();
            drone.waitUntilElementPresent(By.cssSelector("button[id='awe--loginToggle-button']"), 3);

        }
        catch (UnsupportedOperationException| IllegalArgumentException e)
        {
            throw new PageOperationException("The login was not successfully ", e);
        }
    }

}
