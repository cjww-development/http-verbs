/*
 *  Copyright 2018 CJWW Development
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cjwwdev.http.verbs

import com.cjwwdev.http.headers.HttpHeaders
import com.cjwwdev.implicits.ImplicitHandlers
import play.api.libs.json.OFormat
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait HttpPost extends ImplicitHandlers {
  self: HttpHeaders =>

  val wsClient: WSClient

  def POST[T](url: String, data: T)(implicit request: Request[_], format: OFormat[T]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHeaders(initialiseHeaderPackage)
      .post(data.encryptType) map(EvaluateResponse(url, "Post", _))
  }

  def POSTString(url: String, data: String)(implicit request: Request[_]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHeaders(initialiseHeaderPackage)
      .post(data.encrypt) map(EvaluateResponse(url, "PostString", _))
  }
}
