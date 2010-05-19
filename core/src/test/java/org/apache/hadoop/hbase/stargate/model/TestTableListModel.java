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

public class TestTableListModel extends TestCase {
  static final String TABLE1 = "table1";
  static final String TABLE2 = "table2";
  static final String TABLE3 = "table3";
  
  static final String AS_XML =
    "<TableList><table name=\"table1\"/><table name=\"table2\"/>" +
      "<table name=\"table3\"/></TableList>";

  static final String AS_PB = "CgZ0YWJsZTEKBnRhYmxlMgoGdGFibGUz";

  JAXBContext context;

  TableListModel buildTestModel() {
    TableListModel model = new TableListModel();
    model.add(new TableModel(TABLE1));
    model.add(new TableModel(TABLE2));
    model.add(new TableModel(TABLE3));
    return model;
  }

  @SuppressWarnings("unused")
  String toXML(TableListModel model) throws JAXBException {
    StringWriter writer = new StringWriter();
    context.createMarshaller().marshal(model, writer);
    return writer.toString();
  }

  TableListModel fromXML(String xml) throws JAXBException {
    return (TableListModel)
      context.createUnmarshaller().unmarshal(new StringReader(xml));
  }

  @SuppressWarnings("unused")
  byte[] toPB(TableListModel model) {
    return model.createProtobufOutput();
  }

  TableListModel fromPB(String pb) throws IOException {
    return (TableListModel) 
      new TableListModel().getObjectFromMessage(Base64.decode(AS_PB));
  }

  void checkModel(TableListModel model) {
    Iterator<TableModel> tables = model.getTables().iterator();
    TableModel table = tables.next();
    assertEquals(table.getName(), TABLE1);
    table = tables.next();
    assertEquals(table.getName(), TABLE2);
    table = tables.next();
    assertEquals(table.getName(), TABLE3);
    assertFalse(tables.hasNext());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = JAXBContext.newInstance(
        TableListModel.class,
        TableModel.class);
  }

  public void testTableListModel() throws Exception {
    checkModel(buildTestModel());
    checkModel(fromXML(AS_XML));
    checkModel(fromPB(AS_PB));
  }
}
