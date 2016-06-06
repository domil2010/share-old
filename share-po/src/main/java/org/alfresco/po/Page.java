package org.alfresco.po;

import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

/**
 * Abstract which holds all common functions that applies to an Html Page.
 * The class provides the mechanism to determine if the page is ready to use.
 * @author Michael Suzuki
 */
public abstract class Page extends PageElement implements HtmlPage
{
    public String getTitle()
    {
        return driver.getTitle();
    }
    public void close()
    {
        if(driver != null)
        {
            driver.close();
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T extends HtmlPage> T render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        basicRender(timer);
        webElementRender(timer);
        return (T)this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render(RenderTime timer) 
    {
        basicRender(timer);
        webElementRender(timer);
        return (T)this;
    }  
    /**
     * Basic render that checks if the page has rendered.
     *
     * @param timer {@link RenderTime}
     */
    public void basicRender(RenderTime timer)
    {
        try
        {
            timer.start();
            isBlackMessageDisappeared(timer.timeLeft());
            isRenderComplete(timer.timeLeft());
        }
        finally
        {
            timer.end();
        }
    }
    /**
     * Helper method to verify if the current Browser page
     * title contains the title name.
     * @param titleName String partial title value
     * @return true if page title contains title
     */
    public boolean isBrowserTitle(final String titleName)
    {
        boolean exists = false;
        if(titleName != null && titleName.length() > 0)
        {
            String title = driver.getTitle().toLowerCase();
            exists = title.contains(titleName.toLowerCase());
        }
        return exists;
    }
    
     /**
     * Helper method to verify if the black message has disappeared
     * @return true if black message disappears
     */
    public boolean isBlackMessageDisappeared(final long waitTime)
    {
        By blackMessage = By.cssSelector("div.alfresco-notifications-AlfNotification");
        waitUntilElementDeletedFromDom(blackMessage, waitTime);
        return true;
    }
    /**
     * Gets the browser session id from a cookie.
     * 
     * @return String JSESSIONID and value
     */
    public String getSessionId()
    {
        Cookie cookie = getCookie("JSESSIONID");
        if(cookie != null)
        {
            return String.format("%s = %s%n", cookie.getName(), cookie.getValue());
        }
        return "";
    }
    
    
    /**
     * Gets the cookie from the browser session.
     * 
     * @return name of cookie
     */
    public Cookie getCookie(final String name)
    {
        if(name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Cookie identifier is required.");
        }
        Set<Cookie> cookies = driver.manage().getCookies();
        if(cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if(name.equalsIgnoreCase(cookie.getName()))
                {
                    return cookie;
                }
            }
        }
        return null;
    }
    
}