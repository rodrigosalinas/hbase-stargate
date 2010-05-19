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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;

import junit.framework.TestCase;

public class TestCellModel extends TestCase {
  static final long TIMESTAMP = 1245219839331L;
  static final byte[] COLUMN = Bytes.toBytes("testcolumn");
  static final byte[] VALUE = Bytes.toBytes("testvalue");

  static final String AS_XML =
    "<Cell timestamp=\"1245219839331\"" +
      " column=\"dGVzdGNvbHVtbg==\">" +
      "dGVzdHZhbHVl</Cell>";

  static final String AS_PB = 
    "Egp0ZXN0Y29sdW1uGOO6i+eeJCIJdGVzdHZhbHVl";

  JAXBContext context;

  CellModel buildTestModel() {
    CellModel model = new CellModel();
    model.setColumn(COLUMN);
    model.setTimestamp(TIMESTAMP);
    model.setValue(VALUE);
    return model;
  }

  @SuppressWarnings("unused")
  String toXML(CellModel model) throws JAXBException {
    StringWriter writer = new StringWriter();
    context.createMarshaller().marshal(model, writer);
    return writer.toString();
  }

  CellModel fromXML(String xml) throws JAXBException {
    return (CellModel)
      context.createUnmarshaller().unmarshal(new StringReader(xml));
  }

  @SuppressWarnings("unused")
  byte[] toPB(CellModel model) {
    return model.createProtobufOutput();
  }

  CellModel fromPB(String pb) throws IOException {
    return (CellModel) 
      new CellModel().getObjectFromMessage(Base64.decode(AS_PB));
  }

  void checkModel(CellModel model) {
    assertTrue(Bytes.equals(model.getColumn(), COLUMN));
    assertTrue(Bytes.equals(model.getValue(), VALUE));
    assertTrue(model.hasUserTimestamp());
    assertEquals(model.getTimestamp(), TIMESTAMP);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = JAXBContext.newInstance(CellModel.class);
  }

  public void testCellModel() throws Exception {
    checkModel(buildTestModel());
    checkModel(fromXML(AS_XML));
    checkModel(fromPB(AS_PB));
  }
}
