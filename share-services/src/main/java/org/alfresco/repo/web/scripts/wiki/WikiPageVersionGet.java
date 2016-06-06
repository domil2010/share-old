package org.alfresco.repo.web.scripts.wiki;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AspectMissingException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionDoesNotExistException;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.wiki.WikiPageInfo;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the wiki page version fetching version.get webscript.
 * 
 * @author Nick Burch
 * @since 4.0
 */
public class WikiPageVersionGet extends AbstractWikiWebScript
{
   // For spotting if a version string is an ID or a Label
   private static final Pattern LABEL_PATTERN = Pattern.compile("\\d+\\.\\d+");
   
   private static final String PARAM_CONTENT = "content";
   
   private ContentService contentService;
   private VersionService versionService;
   
   public void setVersionService(VersionService versionService) 
   {
       this.versionService = versionService;
   }
   
   public void setContentService(ContentService contentService) 
   {
       this.contentService = contentService;
   }

   @Override
   protected Map<String, Object> executeImpl(SiteInfo site, String pageTitle,
         WebScriptRequest req, JSONObject json, Status status, Cache cache) 
   {
      Map<String, Object> model = new HashMap<>();
      
      // Grab the version string
      Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
      String versionId = templateVars.get("versionId");
      if (versionId == null)
      {
         String error = "No versionId supplied";
         throw new WebScriptException(Status.STATUS_BAD_REQUEST, error);
      }
      
      // Try to find the page
      WikiPageInfo page = wikiService.getWikiPage(site.getShortName(), pageTitle);
      if (page == null)
      {
         String message = "The Wiki Page could not be found";
         status.setCode(Status.STATUS_NOT_FOUND);
         status.setMessage(message);
         
         // Return an empty string though
         model.put(PARAM_CONTENT, "");
         return model;
      }
      
      
      // Fetch the version history for the node
      VersionHistory versionHistory = null;
      Version version = null;
      try
      {
         versionHistory = versionService.getVersionHistory(page.getNodeRef());
      }
      catch (AspectMissingException e) {}
      
      if (versionHistory == null)
      {
         // Not been versioned, return an empty string
         model.put(PARAM_CONTENT, "");
         return model;
      }
      
      
      // Fetch the version by either ID or Label
      Matcher m = LABEL_PATTERN.matcher(versionId);
      if (m.matches())
      {
         // It's a version label like 2.3
         try
         {
            version = versionHistory.getVersion(versionId);
         }
         catch (VersionDoesNotExistException e) {}
      }
      else
      {
         // It's a version ID like ed00bac1-f0da-4042-8598-45a0d39cb74d
         // (The ID is usually part of the NodeRef of the frozen node, but we
         //  don't assume to be able to just generate the full NodeRef)
         for (Version v : versionHistory.getAllVersions())
         {
            if (v.getFrozenStateNodeRef().getId().equals(versionId))
            {
               version = v;
            }
         }
      }
      
      
      // Did we find the right version in the end?
      String contents;
      if (version != null)
      {
         ContentReader reader = contentService.getReader(version.getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
         if (reader != null)
         {
            contents = reader.getContentString();
         }
         else
         {
            // No content was stored in the version history
            contents = "";
         }
      }
      else
      {
         // No warning of the missing version, just return an empty string
         contents = "";
      }
      
      // All done
      model.put(PARAM_CONTENT, contents);
      model.put("page", page);
      model.put("site", site);
      model.put("siteId", site.getShortName());
      return model;
   }
}
