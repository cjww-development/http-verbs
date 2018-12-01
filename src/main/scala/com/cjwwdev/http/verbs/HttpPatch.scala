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
import com.cjwwdev.http.responses.EvaluateResponse.ConnectorResponse
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.security.obfuscation.Obfuscator
import play.api.http.HttpVerbs.PATCH
import play.api.libs.json.{Json, OFormat}
import play.api.libs.ws.WSClient
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

trait HttpPatch {
  self: HttpHeaders =>

  val wsClient: WSClient

  def patch[T](url: String, data: T, secure: Boolean = true, headers: Seq[(String, String)] = Seq())
              (implicit ec: ExC, request: Request[_], format: OFormat[T], obfuscator: Obfuscator[T]): Future[ConnectorResponse] = {
    wsClient
      .url(url)
      .withHttpHeaders(headers ++ Seq(initialiseHeaderPackage, requestIdHeader, contentTypeHeader):_*)
      .patch(if(secure) data.encrypt else Json.prettyPrint(Json.toJson(data)))
      .map(EvaluateResponse(url, PATCH, _))
  }

  def patchString(url: String, data: String, secure: Boolean = true, headers: Seq[(String, String)] = Seq())
                 (implicit ec: ExC, request: Request[_]): Future[ConnectorResponse] = {
    wsClient
      .url(url)
      .withHttpHeaders(headers ++ Seq(initialiseHeaderPackage, requestIdHeader, contentTypeHeader):_*)
      .patch(if(secure) data.encrypt else data)
      .map(EvaluateResponse(url, PATCH, _))
  }
}
