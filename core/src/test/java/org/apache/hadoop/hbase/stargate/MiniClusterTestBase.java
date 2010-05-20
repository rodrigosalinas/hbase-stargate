/*
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hbase.stargate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HBaseClusterTestCase;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class MiniClusterTestBase extends HBaseClusterTestCase
    implements Constants {
  protected static final Log LOG =
    LogFactory.getLog(MiniClusterTestBase.class);

  // use a nonstandard port
  public static final int DEFAULT_TEST_PORT = 38080;

  protected int testServletPort;
  protected Server server;

  private void startServletContainer() throws Exception {
    if (server != null) {
      LOG.error("ServletContainer already running");
      return;
    }

    // set up the Jersey servlet container for Jetty
    ServletHolder sh = new ServletHolder(ServletContainer.class);
    sh.setInitParameter(
      "com.sun.jersey.config.property.resourceConfigClass",
      ResourceConfig.class.getCanonicalName());
    sh.setInitParameter("com.sun.jersey.config.property.packages",
      "jetty");

    LOG.info("configured " + ServletContainer.class.getName());
    
    // set up Jetty and run the embedded server
    testServletPort = conf.getInt("test.stargate.port", DEFAULT_TEST_PORT);
    server = new Server(testServletPort);
    server.setSendServerVersion(false);
    server.setSendDateHeader(false);
      // set up context
    Context context = new Context(server, "/", Context.SESSIONS);
    context.addServlet(sh, "/*");
      // start the server
    server.start();

    LOG.info("started " + server.getClass().getName() + " on port " + 
      testServletPort);
  }

  private void stopServletContainer() {
    if (server != null) try {
      server.stop();
      server = null;
    } catch (Exception e) {
      LOG.warn(StringUtils.stringifyException(e));
    }
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startServletContainer();
    // tell HttpClient to dump request and response headers into the test
    // log at DEBUG level
    Logger.getLogger("httpclient.wire.header").setLevel(Level.DEBUG);
  }

  @Override
  protected void tearDown() throws Exception {
    stopServletContainer();
    super.tearDown();
  }
}
