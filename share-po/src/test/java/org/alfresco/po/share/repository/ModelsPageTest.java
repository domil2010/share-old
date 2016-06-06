package org.alfresco.po.share.repository;

import java.io.File;
import java.util.Map;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.steps.AdminActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Repo > Models page is operating correctly.
 *
 * @author Meenal Bhave
 * @since 5.0.2
 */
@Listeners(FailedTestListener.class)
public class ModelsPageTest extends AbstractDocumentTest
{
    
    
    private static RepositoryPage repoPage;
    
    @Autowired AdminActions adminActions;
    
    private String modelName;
    private File modelFileDraft;
    private ContentDetails modelFileContents = new ContentDetails();

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        modelName = "1model" + System.currentTimeMillis();
        loginAs(driver, shareUrl, username, password).render();
        modelFileDraft = siteUtil.prepareFile(modelName,  "<xml>modelName</xml>", ".xml");
        modelFileContents.setName(modelName);
        
        siteUtil.prepareFile("Alfresco456");
    }

    @AfterClass(groups={"alfresco-one"})
    public void teardown()
    {
        
    }
    
    /**
     * Test navigating to model works correctly for Ent and Cloud AlfrescoVersions
     *
     * @throws Exception
     */
    @Test(enabled = true, groups = "alfresco-one", priority = 1)
    public void testNavigateToModels() throws Exception
    {               
        ModelsPage modelsPage = adminActions.openRepositoryModelsPage(driver).render();
        modelsPage = modelsPage.getNavigation().selectSimpleView().render();
        Assert.assertNotNull(modelsPage);  
        UploadFilePage uploadForm = modelsPage.getNavigation().selectFileUpload().render();
        uploadForm.uploadFile(modelFileDraft.getCanonicalPath()).render();
    }

    @Test(dependsOnMethods="testNavigateToModels", groups = "alfresco-one", priority = 2)
    public void testSimpleView() throws Exception
    {
        ModelsPage modelsPage = adminActions.openRepositoryModelsPage(driver).render();
        
        FileDirectoryInfo file = modelsPage.getFileDirectoryInfo(modelFileDraft.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), modelFileDraft.getName());
        Assert.assertTrue(file.isModelInfoPresent());
        Assert.assertFalse(file.isModelActive());
    }
    
    @Test(groups = "alfresco-one", priority = 3)
    public void testDetailedView() throws Exception
    {        
        ModelsPage modelsPage = getModelsPage();
        modelsPage = modelsPage.getNavigation().selectDetailedView().render();
        
        FileDirectoryInfo file = modelsPage.getFileDirectoryInfo(modelFileDraft.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), modelFileDraft.getName());
        Assert.assertTrue(file.isModelInfoPresent());
        Assert.assertFalse(file.isModelActive());

// TODO: This is to be uncommented when capability to activate a model is implemented and there is an active model in the system  
//        file = modelsPage.getFileDirectoryInfo(activeModel);
//        Assert.assertNotNull(file);
//        Assert.assertTrue(file.isModelInfoPresent());
//        Assert.assertTrue(file.isModelActive());
//        Assert.assertNotNull(file.getModelName());
//        Assert.assertNotNull(file.getModelDesription());
       
    }
//    TODO fix getFileDirectoryInfo so that it can find when its table view.
//    @Test(groups = "alfresco-one", priority = 4, expectedExceptions=UnsupportedOperationException.class)
//    public void testTableView() throws Exception
//    {        
//        ModelsPage modelsPage = getModelsPage();
//        modelsPage = modelsPage.getNavigation().selectTableView().render();
//        
//        FileDirectoryInfo file = modelsPage.getFileDirectoryInfo(modelFileDraft.getName());
//        Assert.assertNotNull(file);
//        Assert.assertEquals(file.getName(), modelFileDraft.getName());
//        Assert.assertFalse(file.isModelInfoPresent());
//        Assert.assertFalse(file.isModelActive());
//    }
    
    
    @Test(groups = "alfresco-one", priority = 7, expectedExceptions=UnsupportedOperationException.class)
    public void testNotModelsPage() throws Exception
    {        
        repoPage = adminActions.openRepositoryDataDictionaryPage(driver).render();
        repoPage = repoPage.getNavigation().selectDetailedView().render();
        
        FileDirectoryInfo file = repoPage.getFileDirectoryInfo("Messages");
        Assert.assertNotNull(file);
        Assert.assertFalse(file.isModelInfoPresent());
        Assert.assertFalse(file.isModelActive());
    }
//Bug in the product.
    @Test(groups = "alfresco-one", priority = 8)
    public void testEditPropertiesPopup() throws Exception
    {        
        ModelsPage modelsPage = adminActions.openRepositoryModelsPage(driver).render();
        modelsPage = modelsPage.getNavigation().selectDetailedView().render();
        
        Assert.assertNotNull(modelsPage);
        
        FileDirectoryInfo file = modelsPage.getFileDirectoryInfo(modelFileDraft.getName());
        Assert.assertNotNull(file);
//        
//        EditDocumentPropertiesPage editPropPage = file.selectEditProperties().render();
//        Assert.assertFalse(editPropPage.isModelActive());
//        
//        editPropPage.setModelActive();
//        Assert.assertTrue(editPropPage.isModelActive());
//        
//        editPropPage.setModelActive();
//        Assert.assertFalse(editPropPage.isModelActive());
        
//        modelsPage = editPropPage.selectCancel().render();
//        Assert.assertNotNull(modelsPage);
    }
    
    @Test(groups = "alfresco-one", priority = 9)
    public void testModelDetailsPage() throws Exception
    {        
        ModelsPage modelsPage = getModelsPage();
        Assert.assertNotNull(modelsPage);
        
        DocumentDetailsPage modelDetails = modelsPage.selectFile(modelFileDraft.getName()).render();
        Assert.assertNotNull(modelDetails);
        
        Map<String, Object> modelProps = modelDetails.getProperties();
        Assert.assertEquals(modelProps.get("ModelActive"), "No");
    }
    
    private ModelsPage getModelsPage() throws Exception
    {
        ModelsPage modelsPage = resolvePage(driver).render();
        modelsPage = modelsPage.getNavigation().selectSimpleView().render();
        return modelsPage;
    }
}
