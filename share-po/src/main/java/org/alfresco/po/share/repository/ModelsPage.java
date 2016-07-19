/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.repository;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.RepositoryPage;

/**
 * Model's page object, holds all element of the HTML page relating to share's repository > Models page.
 * 
 * @author mbhave
 * @since 5.0.2
 */

public class ModelsPage extends RepositoryPage
{


    @SuppressWarnings("unchecked")
    @Override
    public ModelsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModelsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        return isBrowserTitle("Repository Browser");
    }

}
