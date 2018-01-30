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

import com.cjwwdev.http.exceptions._
import com.cjwwdev.logging.Logging
import play.api.http.Status._
import play.api.libs.ws.WSResponse

object EvaluateResponse extends Logging {
  private class Contains(range: Range) {
    def unapply(statusCode: Int): Boolean = range contains statusCode
  }

  private val success     = new Contains(200 to 299)
  private val clientError = new Contains(400 to 499)
  private val serverError = new Contains(500 to 599)

  def apply(url: String, method: String, response: WSResponse): WSResponse = response.status match {
    case success() =>
      logger.info(s"[Http$method] - Call out to $url was successful (${response.status})")
      response
    case BAD_REQUEST =>
      logger.error(s"[Http$method] = Call out to $url return a BadRequest (${response.status})")
      throw new BadRequestException(response.body)
    case FORBIDDEN =>
      logger.error(s"[Http$method] = Call out to $url return a Forbidden (${response.status})")
      throw new ForbiddenException(response.body)
    case NOT_FOUND =>
      logger.error(s"[Http$method] = Call out to $url return a NotFound (${response.status})")
      throw new NotFoundException(response.body)
    case NOT_ACCEPTABLE =>
      logger.error(s"[Http$method] - Call out to $url returned a NotAcceptable (${response.status})")
      throw new NotAcceptableException(response.body)
    case CONFLICT =>
      logger.error(s"[Http$method] = Call out to $url return a Conflict (${response.status})")
      throw new ConflictException(response.body)
    case clientError() =>
      logger.error(s"[Http$method] = Call out to $url return a 4xx error (${response.status})")
      throw new ClientErrorException(response.body, response.status)
    case serverError() =>
      logger.error(s"[Http$method] = Call out to $url return a 5xx error (${response.status})")
      throw new ServerErrorException(response.body, response.status)
  }
}
