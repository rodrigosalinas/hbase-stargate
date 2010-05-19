/*
 * Copyright 2009 The Apache Software Foundation
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

package org.apache.hadoop.hbase.stargate.model;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

public class TestStorageClusterVersionModel extends TestCase {
  static final String VERSION = "0.0.1-testing";

  static final String AS_XML =
    "<ClusterVersion>" + VERSION + "</ClusterVersion>";

  JAXBContext context;

  StorageClusterVersionModel buildTestModel() {
    StorageClusterVersionModel model = new StorageClusterVersionModel();
    model.setVersion(VERSION);
    return model;
  }

  @SuppressWarnings("unused")
  String toXML(StorageClusterVersionModel model) throws JAXBException {
    StringWriter writer = new StringWriter();
    context.createMarshaller().marshal(model, writer);
    return writer.toString();
  }

  StorageClusterVersionModel fromXML(String xml) throws JAXBException {
    return (StorageClusterVersionModel)
      context.createUnmarshaller().unmarshal(new StringReader(xml));
  }

  void checkModel(StorageClusterVersionModel model) {
    assertEquals(model.getVersion(), VERSION);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = JAXBContext.newInstance(StorageClusterVersionModel.class);
  }

  public void testStorageClusterModel() throws Exception {
    checkModel(buildTestModel());
    checkModel(fromXML(AS_XML));
  }
}
