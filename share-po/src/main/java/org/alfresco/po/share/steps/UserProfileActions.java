package org.alfresco.po.share.steps;

import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.TrashCanDeleteConfirmDialog;
import org.alfresco.po.share.user.TrashCanDeleteConfirmationPage;
import org.alfresco.po.share.user.TrashCanItem;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.user.TrashCanRecoverConfirmDialog;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserProfileActions extends CommonActions
{
    private static Log logger = LogFactory.getLog(DashBoardActions.class);
    
    /**
     * Navigate to User Profile page 
     * @param driver WebDriver Instance
     * @return {@link MyProfilePage}
     */
    public MyProfilePage navigateToMyProfile(WebDrone driver)
    {
        try
        {
            SharePage dashBoard = getSharePage(driver);

            MyProfilePage myprofile = dashBoard.getNav().selectMyProfile().render();
            return myprofile;
        }
        catch (ClassCastException c)
        {
            throw new UnexpectedSharePageException(DashBoardPage.class, c);
        }
    }
    
    /**
     * Navigate to User's TrashCan page 
     * @param driver WebDriver Instance
     * @return {@link TrashCanPage}
     */
    public TrashCanPage navigateToTrashCan(WebDrone driver)
    {
        MyProfilePage myprofile = navigateToMyProfile(driver);
        TrashCanPage trashCan = myprofile.getProfileNav().selectTrashCan().render();
        return trashCan;
    }   
    
    /**
     * Delete specified content from User's TrashCan page 
     * @param driver WebDriver Instance
     * @param 
     * @return {@link TrashCanPage}
     */
    public TrashCanPage deleteFromTrashCan(WebDrone driver, TrashCanValues contentType, String contentName, String path)
    {
        WebDroneUtil.checkMandotaryParam("Content Type must be specified", contentType);
        WebDroneUtil.checkMandotaryParam("Content Name must be specified", contentName);
        WebDroneUtil.checkMandotaryParam("Appropriate path for the content must be specified", path);
        
        TrashCanPage trashCan = getSharePage(driver).render();

        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(contentType, contentName, path);
        if (item1.isEmpty())
        {
            throw new PageOperationException("Content not found in TrashCan: " + contentName);
        }
        
        if (item1.size() > 1)
        {
            logger.info("More than 1 matching items found in the TrashCan. Deleting 1st found item!");
        }
        
        trashCan = item1.get(0).selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanDeleteConfirmation = trashCan.selectedDelete().render();
        
        TrashCanDeleteConfirmDialog trashCanConfrimDialog = (TrashCanDeleteConfirmDialog) trashCanDeleteConfirmation.clickOkButton().render();
        trashCan = trashCanConfrimDialog.clickDeleteOK().render();
        return trashCan;
    } 
    
    
    /**
     * Delete specified content from User's TrashCan page 
     * @param driver WebDriver Instance
     * @param 
     * @return {@link TrashCanPage}
     */
    public TrashCanPage recoverFromTrashCan(WebDrone driver, TrashCanValues contentType, String contentName, String path)
    {
        WebDroneUtil.checkMandotaryParam("Content Type must be specified", contentType);
        WebDroneUtil.checkMandotaryParam("Content Name must be specified", contentName);
        WebDroneUtil.checkMandotaryParam("Appropriate path for the content must be specified", path);
        
        TrashCanPage trashCan = getSharePage(driver).render();

        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(contentType, contentName, path);
        if (item1.isEmpty())
        {
            throw new PageOperationException("Content not found in TrashCan: " + contentName);
        }
        
        if (item1.size() > 1)
        {
            logger.info("More than 1 matching items found in the TrashCan. Recovering 1st found item!");
        }
        
        trashCan = item1.get(0).selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCan.selectedRecover().render();
        
        trashCan= trashCanRecoverConfirmation.clickRecoverOK().render();
        return trashCan;
    } 
}