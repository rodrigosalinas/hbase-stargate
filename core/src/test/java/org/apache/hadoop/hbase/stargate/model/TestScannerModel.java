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

public class TestScannerModel extends TestCase {
  static final byte[] START_ROW = Bytes.toBytes("abracadabra");
  static final byte[] END_ROW = Bytes.toBytes("zzyzx");
  static final byte[] COLUMN1 = Bytes.toBytes("column1");
  static final byte[] COLUMN2 = Bytes.toBytes("column2:foo");
  static final long START_TIME = 1245219839331L;
  static final long END_TIME = 1245393318192L;
  static final int BATCH = 100;

  static final String AS_XML =
    "<Scanner startTime=\"1245219839331\"" +
      " startRow=\"YWJyYWNhZGFicmE=\"" + 
      " endTime=\"1245393318192\"" +
      " endRow=\"enp5eng=\"" +
      " batch=\"100\">" +
        "<column>Y29sdW1uMQ==</column>" +
        "<column>Y29sdW1uMjpmb28=</column>" +
      "</Scanner>";

  static final String AS_PB = 
    "CgthYnJhY2FkYWJyYRIFenp5engaB2NvbHVtbjEaC2NvbHVtbjI6Zm9vIGQo47qL554kMLDi57mf" +
    "JA==";

  JAXBContext context;

  ScannerModel buildTestModel() {
    ScannerModel model = new ScannerModel();
    model.setStartRow(START_ROW);
    model.setEndRow(END_ROW);
    model.addColumn(COLUMN1);
    model.addColumn(COLUMN2);
    model.setStartTime(START_TIME);
    model.setEndTime(END_TIME);
    model.setBatch(BATCH);
    return model;
  }

  @SuppressWarnings("unused")
  String toXML(ScannerModel model) throws JAXBException {
    StringWriter writer = new StringWriter();
    context.createMarshaller().marshal(model, writer);
    return writer.toString();
  }

  ScannerModel fromXML(String xml) throws JAXBException {
    return (ScannerModel)
      context.createUnmarshaller().unmarshal(new StringReader(xml));
  }

  @SuppressWarnings("unused")
  byte[] toPB(ScannerModel model) {
    return model.createProtobufOutput();
  }

  ScannerModel fromPB(String pb) throws IOException {
    return (ScannerModel) 
      new ScannerModel().getObjectFromMessage(Base64.decode(AS_PB));
  }

  void checkModel(ScannerModel model) {
    assertTrue(Bytes.equals(model.getStartRow(), START_ROW));
    assertTrue(Bytes.equals(model.getEndRow(), END_ROW));
    boolean foundCol1 = false, foundCol2 = false;
    for (byte[] column: model.getColumns()) {
      if (Bytes.equals(column, COLUMN1)) {
        foundCol1 = true;
      } else if (Bytes.equals(column, COLUMN2)) {
        foundCol2 = true;
      }
    }
    assertTrue(foundCol1);
    assertTrue(foundCol2);
    assertEquals(model.getStartTime(), START_TIME);
    assertEquals(model.getEndTime(), END_TIME);
    assertEquals(model.getBatch(), BATCH);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = JAXBContext.newInstance(ScannerModel.class);
  }

  public void testScannerModel() throws Exception {
    checkModel(buildTestModel());
    checkModel(fromXML(AS_XML));
    checkModel(fromPB(AS_PB));
  }
}
