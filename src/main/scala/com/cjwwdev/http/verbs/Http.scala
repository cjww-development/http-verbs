// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cjwwdev.http.verbs

import javax.inject.{Inject, Singleton}

import com.cjwwdev.http.utils.{HttpHeaders, ResponseUtils}
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.Format
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Http @Inject()(wsClient: WSClient) extends HttpHeaders with ResponseUtils {
  def GET(url: String)(implicit request: Request[_]): Future[WSResponse] = {
    wsClient.url(url).withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader, contentTypeHeader).get() map {
      resp => processHttpResponse(resp)
    }
  }

  def POST[T](url: String, data: T)(implicit request: Request[_], format: Format[T]): Future[WSResponse] = {
    val body = DataSecurity.encryptData[T](data).get
    wsClient.url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader, contextIdHeader)
      .withBody(body)
      .post(body)
  }

  def PUT[T](url: String, data: T)(implicit request: Request[_], format: Format[T]): Future[WSResponse] = {
    val body = DataSecurity.encryptData[T](data).get
    wsClient.url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader, contextIdHeader)
      .withBody(body)
      .put(body)
  }

  def PATCH[T](url: String, data: T)(implicit request: Request[_], format: Format[T]): Future[WSResponse] = {
    val body = DataSecurity.encryptData[T](data).get
    wsClient.url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader, contextIdHeader)
      .withBody(body)
      .patch(body)
  }

  def DELETE(url: String)(implicit request: Request[_]): Future[WSResponse] = {
    wsClient.url(url).withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader, contextIdHeader).delete()
  }
}