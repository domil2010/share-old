package org.alfresco.po.share.dashlet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.site.links.AddLinkForm;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.site-links")
/**
 * Page object to hold site links dashlet
 *
 * @author Marina.Nenadovets
 */
public class SiteLinksDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SiteLinksDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.site-links");
    private static final By CREATE_LINK = By.cssSelector("a[href='links-linkedit']");
    private static final By LINK_DETAILS = By.cssSelector("div.actions>a.details");
    private static final By LINKS_LIST = By.cssSelector("div.dashlet.site-links>div.scrollableList div[class='link']>a");

//    /**
//     * Constructor.
//     */
//    protected SiteLinksDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector(".yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public SiteLinksDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    //scrollDownToDashlet();
                    getFocus(DASHLET_CONTAINER_PLACEHOLDER);
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for SiteLinksDashlet dashlet was not found ", e);
                }
                catch (StaleElementReferenceException ste)
                {
                    logger.error("DOM has changed therefore page should render once change", ste);
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site links dashlet", te);
        }
        return this;
    }

    /**
     * This method gets the focus by placing mouse over on Site Links Dashlet.
     */
    private void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    private List<WebElement> getLinkElements()
    {
        try
        {
            return findAndWaitForElements(LINKS_LIST);
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
        catch (StaleElementReferenceException e)
        {
            return getLinkElements();
        }
    }

    /**
     * Return links count displayed in dashlet.
     *
     * @return int
     */
    public int getLinksCount()
    {
        return getLinkElements().size();
    }

    /**
     * Return true if link 'linkName' displayed in dashlet
     *
     * @param linkName String
     * @return boolean
     */
    public boolean isLinkDisplayed(String linkName)
    {
        checkNotNull(linkName);
        List<WebElement> eventLinks = getLinkElements();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkName.equals(linkText))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }
    AddLinkForm addLinkForm;
    /**
     * Method to create a link
     *
     * @param name String
     * @param url String
     * @return LinksDetailsPage
     */
    public HtmlPage createLink(String name, String url)
    {
        findAndWait(CREATE_LINK).click();
        addLinkForm.setTitleField(name);
        addLinkForm.setUrlField(url);
        addLinkForm.clickSaveBtn();
        return factoryPage.instantiatePage(driver,LinksDetailsPage.class);
    }

    /**
     * Method to verify whether Links details is available on site links dashlet
     *
     * @return boolean
     */
    public boolean isDetailsLinkDisplayed()
    {
        try
        {
            getFocus();
            return findAndWait(LINK_DETAILS).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteLinksDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
