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

import javax.inject.Inject

import com.cjwwdev.http.utils.{HttpHeaders, ResponseUtils}
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.{OWrites, Reads}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HttpImpl @Inject()(val wsClient: WSClient) extends Http

trait Http extends HttpHeaders with ResponseUtils {
  val wsClient: WSClient

  def HEAD(url: String)(implicit request: Request[_]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader)
      .head map(processHttpResponse(url, _))
  }

  def GET[T](url: String)(implicit request: Request[_], reads: Reads[T]): Future[T] = {
    wsClient
      .url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader)
      .get map(processHttpResponseIntoType(url, _))
  }

  def POST[T](url: String, data: T)(implicit request: Request[_], writes: OWrites[T]): Future[WSResponse] = {
    val body = DataSecurity.encryptType[T](data)
    wsClient
      .url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader)
      .withBody(body)
      .post(body) map(processHttpResponse(url, _))
  }

  def PUT[T](url: String, data: T)(implicit request: Request[_], writes: OWrites[T]): Future[WSResponse] = {
    val body = DataSecurity.encryptType[T](data)
    wsClient
      .url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader)
      .withBody(body)
      .put(body) map(processHttpResponse(url, _))
  }

  def PATCH[T](url: String, data: T)(implicit request: Request[_], writes: OWrites[T]): Future[WSResponse] = {
    val body = DataSecurity.encryptType[T](data)
    wsClient
      .url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader)
      .withBody(body)
      .patch(body) map(processHttpResponse(url, _))
  }

  def DELETE(url: String)(implicit request: Request[_]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHeaders(appIdHeader, contentTypeHeader, sessionIdHeader)
      .delete map(processHttpResponse(url, _))
  }
}
