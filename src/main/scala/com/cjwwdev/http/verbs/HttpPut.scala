/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cjwwdev.http.verbs

import com.cjwwdev.http.headers.HttpHeaders
import com.cjwwdev.http.responses.EvaluateResponse
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.security.obfuscation.Obfuscator
import play.api.libs.json.{Json, OFormat}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Request
import play.api.http.HttpVerbs.PUT

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait HttpPut {
  self: HttpHeaders =>

  val wsClient: WSClient

  def put[T](url: String, data: T, secure: Boolean = true, headers: Seq[(String, String)] = Seq())
            (implicit request: Request[_], format: OFormat[T], obfuscator: Obfuscator[T]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHttpHeaders(headers ++ Seq(initialiseHeaderPackage, contentTypeHeader):_*)
      .put(if(secure) data.encrypt else Json.prettyPrint(Json.toJson(data))) map(EvaluateResponse(url, PUT, _))
  }

  def putString(url: String, data: String, secure: Boolean = true, headers: Seq[(String, String)] = Seq())
               (implicit request: Request[_]): Future[WSResponse] = {
    wsClient
      .url(url)
      .withHttpHeaders(headers ++ Seq(initialiseHeaderPackage, contentTypeHeader):_*)
      .put(if(secure) data.encrypt else data) map(EvaluateResponse(url, PUT, _))
  }
}
