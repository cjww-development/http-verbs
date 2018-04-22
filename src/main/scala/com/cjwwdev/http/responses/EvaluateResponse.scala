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

import com.cjwwdev.http.exceptions._
import com.cjwwdev.logging.Logging
import play.api.http.Status._
import play.api.libs.ws.WSResponse

object EvaluateResponse extends Logging with WsResponseHelpers {
  private class Contains(range: Range) {
    def unapply(statusCode: Int): Boolean = range contains statusCode
  }

  private val success     = new Contains(200 to 299)
  private val clientError = new Contains(400 to 499)
  private val serverError = new Contains(500 to 599)

  def apply(url: String, method: String, response: WSResponse): WSResponse = response.status match {
    case success()      => response
    case BAD_REQUEST    => throw new BadRequestException(response.logErrorAndReturn(url, method).body)
    case FORBIDDEN      => throw new ForbiddenException(response.logErrorAndReturn(url, method).body)
    case NOT_FOUND      => throw new NotFoundException(response.logErrorAndReturn(url, method).body)
    case NOT_ACCEPTABLE => throw new NotAcceptableException(response.logErrorAndReturn(url, method).body)
    case CONFLICT       => throw new ConflictException(response.logErrorAndReturn(url, method).body)
    case clientError()  => throw new ClientErrorException(response.logErrorAndReturn(url, method).body, response.status)
    case serverError()  => throw new ServerErrorException(response.logErrorAndReturn(url, method).body, response.status)
  }
}
