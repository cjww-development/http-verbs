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

package com.cjwwdev.http.responses

import com.cjwwdev.logging.Logging
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

object EvaluateResponse extends Logging with WsResponseHelpers {
  private class Contains(range: Range) {
    def unapply(statusCode: Int): Boolean = range contains statusCode
  }

  private val success     = new Contains(200 to 299)
  private val clientError = new Contains(400 to 499)
  private val serverError = new Contains(500 to 599)

  type ConnectorResponse = Either[WSResponse, WSResponse]

  val SuccessResponse: Left.type = scala.util.Left
  val ErrorResponse: Right.type  = scala.util.Right

  def apply(url: String, method: String, response: WSResponse)(implicit request: Request[_]): ConnectorResponse = response.status match {
    case success()                     => SuccessResponse(response.logResponse(url, method, inError = false))
    case clientError() | serverError() => ErrorResponse(response.logResponse(url, method, inError = true))
  }
}
