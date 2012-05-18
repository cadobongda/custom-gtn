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

import com.cadobongda.livenews.messaging.JMSProvider;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.picocontainer.Startable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 5/18/12
 */
public class LiveDataProvider implements Startable
{

   private List<LiveDataHandler> handlers = new LinkedList<LiveDataHandler>();

   private JMSProvider jmsProvider;

   public LiveDataProvider(InitParams params, JMSProvider jmsProvider) throws Exception
   {
      this.jmsProvider = jmsProvider;
   }

   public void start()
   {
      for(LiveDataHandler handler : handlers)
      {
         handler.start();
      }
   }

   public void stop()
   {
      for(LiveDataHandler handler : handlers)
      {
         handler.stop();
      }
   }

   public void addHandler(ComponentPlugin handler) throws Exception
   {
      if(handler instanceof LiveDataHandler)
      {
         handlers.add((LiveDataHandler)handler);
      }
   }
}
