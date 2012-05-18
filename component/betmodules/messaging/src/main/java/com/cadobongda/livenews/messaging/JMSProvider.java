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
package com.cadobongda.livenews.messaging;

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.xml.InitParams;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.picocontainer.Startable;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 5/7/12
 */
public class JMSProvider implements Startable
{

   private final Logger LOG = LoggerFactory.getLogger(JMSProvider.class);

   private final EmbeddedJMS jmsServer;

   private String hornetConfigDir;

   private final ConcurrentMap<String, Queue> queues;

   private final ConcurrentMap<String, Topic> topics;

   public JMSProvider(InitParams params) throws Exception
   {
      jmsServer = new EmbeddedJMS();
      hornetConfigDir = PropertyManager.getProperty(params.getValueParam("configDirParam").getValue());
      queues = new ConcurrentHashMap<String, Queue>();
      topics = new ConcurrentHashMap<String, Topic>();
   }

   public void start()
   {
      /**
       * We need to set a custom class loader as the FileDeploymentManager in HornetQ use ClassLoader instead of URL.openConnection
       * so external file configuration won't be processed.
       */
      final ClassLoader cl = Thread.currentThread().getContextClassLoader();
      boolean resetLoader = false;

      try
      {
         Set<URL> urls = new HashSet<URL>();
         if(hornetConfigDir != null)
         {
            LOG.info("HornetQ config directory is " + hornetConfigDir + " , setup config resource paths now");

            File jmsConfigFile = new File(hornetConfigDir + "/hornetq-jms.xml");
            if(jmsConfigFile.exists())
            {
               LOG.info("Found hornetq-jms.xml under " + hornetConfigDir);
               URI jmsConfigFileURI = jmsConfigFile.toURI().normalize();
               jmsServer.setJmsConfigResourcePath(jmsConfigFileURI.toString());
               urls.add(jmsConfigFileURI.toURL());
            }

            File hornetConfigFile = new File(hornetConfigDir + "/hornetq-configuration.xml");
            if(hornetConfigFile.exists())
            {
               LOG.info("Found hornetq-configuration.xml under " + hornetConfigDir);
               URI hornetConfigFileURI = hornetConfigFile.toURI().normalize();
               jmsServer.setConfigResourcePath(hornetConfigFileURI.toString());
               urls.add(hornetConfigFileURI.toURL());
            }

            if(urls.size() > 0)
            {
               ClassLoader customCl = new URLClassLoader(urls.toArray(new URL[urls.size()]), cl);
               Thread.currentThread().setContextClassLoader(customCl);
               resetLoader = true;
            }
         }
         else
         {
            LOG.info("No hornetConfigDir is defined, default configuration files are used");
         }
         jmsServer.start();
      }
      catch(Exception ex)
      {
         ex.printStackTrace();
      }
      finally
      {
         if(resetLoader)
         {
            Thread.currentThread().setContextClassLoader(cl);
         }
      }
   }

   public void stop()
   {
      try
      {
         jmsServer.stop();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public Queue getQueue(String queueName)
   {
      Queue q = queues.get(queueName);
      if(q != null)
      {
         return q;
      }
      else
      {
         q = (Queue)jmsServer.lookup(queueName);
         if(q != null)
         {
            queues.putIfAbsent(queueName, q);
         }
         return q;
      }
   }

   public Topic getTopic(String topicName)
   {
      Topic t = topics.get(topicName);
      if(t != null)
      {
         return t;
      }
      else
      {
         t = (Topic)jmsServer.lookup(topicName);
         if(t != null)
         {
            topics.putIfAbsent(topicName, t);
         }
         return t;
      }
   }

   public Connection createConnection() throws JMSException
   {
      ConnectionFactory factory = (ConnectionFactory)jmsServer.lookup("cf");
      return factory.createConnection();
   }

   public Connection createConnection(String userName, String password) throws JMSException
   {
      ConnectionFactory factory = (ConnectionFactory)jmsServer.lookup("cf");
      return factory.createConnection(userName, password);
   }
}
