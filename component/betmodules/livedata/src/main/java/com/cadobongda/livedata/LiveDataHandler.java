/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.cadobongda.livedata;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import java.net.URL;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 5/18/12
 */
public abstract class LiveDataHandler extends BaseComponentPlugin
{

   protected URL channelURL;

   protected DataFormat newsFormat = DataFormat.XML;

   public LiveDataHandler(InitParams params) throws Exception
   {
      ValueParam urlParam = params.getValueParam("channelURL");
      if(urlParam != null)
      {
         this.channelURL = new URL(urlParam.getValue());
      }

      ValueParam newsFormatParam = params.getValueParam("newsFormat");
      if(newsFormatParam != null)
      {
         this.newsFormat = DataFormat.valueOf(newsFormatParam.getValue());
      }
   }

   public abstract void start();

   public abstract void stop();
}
