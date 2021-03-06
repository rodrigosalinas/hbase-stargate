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
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.hadoop.hbase.util.Base64;

import junit.framework.TestCase;

public class TestTableSchemaModel extends TestCase {

  public static final String TABLE_NAME = "testTable";
  static final boolean IS_META = false;
  static final boolean IS_ROOT = false;
  static final boolean READONLY = false;

  static final String AS_XML =
    "<TableSchema name=\"testTable\"" +
      " IS_META=\"false\"" +
      " IS_ROOT=\"false\"" +
      " READONLY=\"false\">" +
      TestColumnSchemaModel.AS_XML + 
    "</TableSchema>";

  private static final String AS_PB = 
    "Cgl0ZXN0VGFibGUSEAoHSVNfTUVUQRIFZmFsc2USEAoHSVNfUk9PVBIFZmFsc2USEQoIUkVBRE9O" +
    "TFkSBWZhbHNlGpcBCgp0ZXN0Y29sdW1uEhIKCUJMT0NLU0laRRIFMTYzODQSEwoLQkxPT01GSUxU" +
    "RVISBE5PTkUSEgoKQkxPQ0tDQUNIRRIEdHJ1ZRIRCgtDT01QUkVTU0lPThICR1oSDQoIVkVSU0lP" +
    "TlMSATESDAoDVFRMEgU4NjQwMBISCglJTl9NRU1PUlkSBWZhbHNlGICjBSABKgJHWigA";

  JAXBContext context;

  public static TableSchemaModel buildTestModel() {
    return buildTestModel(TABLE_NAME);
  }

  public static TableSchemaModel buildTestModel(String name) {
    TableSchemaModel model = new TableSchemaModel();
    model.setName(name);
    model.__setIsMeta(IS_META);
    model.__setIsRoot(IS_ROOT);
    model.__setReadOnly(READONLY);
    model.addColumnFamily(TestColumnSchemaModel.buildTestModel());
    return model;
  }

  String toXML(TableSchemaModel model) throws JAXBException {
    StringWriter writer = new StringWriter();
    context.createMarshaller().marshal(model, writer);
    return writer.toString();
  }

  TableSchemaModel fromXML(String xml) throws JAXBException {
    return (TableSchemaModel)
      context.createUnmarshaller().unmarshal(new StringReader(xml));
  }

  byte[] toPB(TableSchemaModel model) {
    return model.createProtobufOutput();
  }

  TableSchemaModel fromPB(String pb) throws IOException {
    return (TableSchemaModel) 
      new TableSchemaModel().getObjectFromMessage(Base64.decode(AS_PB));
  }

  public static void checkModel(TableSchemaModel model) {
    checkModel(model, TABLE_NAME);
  }

  public static void checkModel(TableSchemaModel model, String tableName) {
    assertEquals(model.getName(), tableName);
    assertEquals(model.__getIsMeta(), IS_META);
    assertEquals(model.__getIsRoot(), IS_ROOT);
    assertEquals(model.__getReadOnly(), READONLY);
    Iterator<ColumnSchemaModel> families = model.getColumns().iterator();
    assertTrue(families.hasNext());
    ColumnSchemaModel family = families.next();
    TestColumnSchemaModel.checkModel(family);
    assertFalse(families.hasNext());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = JAXBContext.newInstance(
      ColumnSchemaModel.class,
      TableSchemaModel.class);
  }

  public void testTableSchemaModel() throws Exception {
    checkModel(buildTestModel());
    checkModel(fromXML(AS_XML));
    checkModel(fromPB(AS_PB));
  }
}
