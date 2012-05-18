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
package com.cadobongda.livebet;

import com.cadobongda.livenews.messaging.JMSProvider;
import org.exoplatform.container.xml.InitParams;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.picocontainer.Startable;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date 5/18/12
 */
public class BetOrderBroker implements Startable
{

   private Connection connection;

   private final JMSProvider jmsProvider;

   private final Logger LOG = LoggerFactory.getLogger(BetOrderBroker.class);

   private Topic destinationTopic;

   private String destinationTopicName;

   private MessageProducer messageProducer;

   private volatile boolean reinit = false;

   public BetOrderBroker(InitParams initParams, JMSProvider jmsProvider) throws Exception
   {
      this.jmsProvider = jmsProvider;
      this.destinationTopicName = initParams.getValueParam("destinationTopicName").getValue();
   }

   public void start()
   {
      initInfrastructure();
   }

   private void initInfrastructure()
   {
      try
      {
         LOG.debug("Get the destination queue");
         destinationTopic = jmsProvider.getTopic(destinationTopicName);

         LOG.debug("Create a connection");
         connection = jmsProvider.createConnection();
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         LOG.debug("Init message producer");
         messageProducer = session.createProducer(destinationTopic);

         LOG.debug("Start the connection");
         connection.start();
      }
      catch(JMSException jmsEx)
      {
         LOG.error("Failed to initialize infrastructure of OrderBroker", jmsEx);
      }
   }

   private void reinitInfrastructure()
   {
      if(reinit)
      {
         synchronized(this)
         {
            if(reinit)
            {
               initInfrastructure();
               reinit = false;
            }
         }
      }
   }

   public void stop()
   {
      try
      {
         LOG.debug("Stop the JMS connection");
         connection.stop();
      }
      catch(JMSException jmsEx)
      {
         LOG.error("Failed to stop the connection", jmsEx);
      }
   }

   public void sendBetOrder(BetOrder order)
   {
      try
      {
         messageProducer.send(order.getJMSMessage());
      }
      catch (JMSException jmsEx)
      {
         LOG.debug(jmsEx);
      }
   }
}
