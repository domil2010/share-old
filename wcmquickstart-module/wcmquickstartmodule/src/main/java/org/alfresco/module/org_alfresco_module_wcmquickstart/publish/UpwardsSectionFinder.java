/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transfer.AbstractNodeFinder;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Given a section or a web asset, this finder finds its parent section(s)
 * @author Brian
 *
 */
public class UpwardsSectionFinder extends AbstractNodeFinder
{
    private NodeService nodeService;
    private DictionaryService dictionaryService;

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService();
        dictionaryService = serviceRegistry.getDictionaryService();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<NodeRef> findFrom(NodeRef thisNode)
    {
        Set<NodeRef> result = Collections.emptySet();
        if (dictionaryService.isSubClass(nodeService.getType(thisNode), WebSiteModel.TYPE_SECTION))
        {
            result = new HashSet<NodeRef>(23);
            //If this section's parent is also a section then we'll include that too...
            NodeRef parent = nodeService.getPrimaryParent(thisNode).getParentRef();
            if ((parent != null) && dictionaryService.isSubClass(
                    nodeService.getType(thisNode), WebSiteModel.TYPE_SECTION))
            {
                result.add(parent);
            }
        }
        else if (nodeService.hasAspect(thisNode, WebSiteModel.ASPECT_WEBASSET))
        {
            List<NodeRef> parentSections =(List<NodeRef>)nodeService.getProperty(thisNode, WebSiteModel.PROP_PARENT_SECTIONS);
            if (parentSections != null)
            {
                result = new HashSet<NodeRef>(23);
                result.addAll(parentSections);
            }
        }
        return result;
    }

}
