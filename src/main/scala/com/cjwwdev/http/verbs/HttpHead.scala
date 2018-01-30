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
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait HttpHead {
  self: HttpHeaders =>

  val wsClient: WSClient

  def HEAD(url: String)(implicit request: Request[_]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHeaders(initialiseHeaderPackage)
      .head map(EvaluateResponse(url, "Head", _))
  }
}
