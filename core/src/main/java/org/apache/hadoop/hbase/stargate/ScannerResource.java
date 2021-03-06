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

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.hbase.filter.Filter;

import org.apache.hadoop.hbase.stargate.User;
import org.apache.hadoop.hbase.stargate.model.ScannerModel;

public class ScannerResource extends ResourceBase {

  private static final Log LOG = LogFactory.getLog(ScannerResource.class);

  static final Map<String,ScannerInstanceResource> scanners =
   Collections.synchronizedMap(new HashMap<String,ScannerInstanceResource>());

  User user;
  String tableName;
  String actualTableName;

  public ScannerResource(User user, String table) throws IOException {
    super();
    if (user != null) {
      this.user = user;
      this.actualTableName = 
        !user.isAdmin() ? user.getName() + "." + table : table;
    } else {
      this.actualTableName = table;
    }
    this.tableName = table;
  }

  static void delete(final String id) {
    ScannerInstanceResource instance = scanners.remove(id);
    if (instance != null) {
      instance.generator.close();
    }
  }

  Response update(final ScannerModel model, final boolean replace, 
      final UriInfo uriInfo) throws IOException {
    if (!servlet.userRequestLimit(user, 1)) {
      return Response.status(509).build();
    }
    servlet.getMetrics().incrementRequests(1);
    byte[] endRow = model.hasEndRow() ? model.getEndRow() : null;
    RowSpec spec = new RowSpec(model.getStartRow(), endRow,
      model.getColumns(), model.getStartTime(), model.getEndTime(), 1);
    try {
      Filter filter = ScannerResultGenerator.buildFilterFromModel(model);
      ScannerResultGenerator gen = 
        new ScannerResultGenerator(actualTableName, spec, filter);
      String id = gen.getID();
      ScannerInstanceResource instance = 
        new ScannerInstanceResource(user, actualTableName, id, gen, 
          model.getBatch());
      scanners.put(id, instance);
      if (LOG.isDebugEnabled()) {
        LOG.debug("new scanner: " + id);
      }
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      URI uri = builder.path(id).build();
      return Response.created(uri).build();
    } catch (IOException e) {
      throw new WebApplicationException(e,
              Response.Status.SERVICE_UNAVAILABLE);
    } catch (Exception e) {
      throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
    }
  }

  @PUT
  @Consumes({MIMETYPE_XML, MIMETYPE_JSON, MIMETYPE_PROTOBUF})
  public Response put(final ScannerModel model, 
      final @Context UriInfo uriInfo) throws IOException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("PUT " + uriInfo.getAbsolutePath());
    }
    return update(model, true, uriInfo);
  }

  @POST
  @Consumes({MIMETYPE_XML, MIMETYPE_JSON, MIMETYPE_PROTOBUF})
  public Response post(final ScannerModel model,
      final @Context UriInfo uriInfo) throws IOException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("POST " + uriInfo.getAbsolutePath());
    }
    return update(model, false, uriInfo);
  }

  @Path("{scanner: .+}")
  public ScannerInstanceResource getScannerInstanceResource(
      final @PathParam("scanner") String id) {
    ScannerInstanceResource instance = scanners.get(id);
    if (instance == null) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    return instance;
  }
}
