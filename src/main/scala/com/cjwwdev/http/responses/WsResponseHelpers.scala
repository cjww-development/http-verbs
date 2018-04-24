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

import com.cjwwdev.http.exceptions.{HttpDecryptionException, HttpJsonParseException}
import com.cjwwdev.implicits.ImplicitHandlers
import com.cjwwdev.logging.Logging
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import play.utils.Colors

import scala.util.{Failure, Success, Try}

trait WsResponseHelpers extends ImplicitHandlers {
  implicit class WSResponseOps(wsResponse: WSResponse) extends Logging {

    def toResponseString(needsDecrypt: Boolean): String = {
      val jsBody = wsResponse.json

      val httpMethod  = jsBody.\("method").as[String]
      val requestPath = jsBody.\("uri").as[String]
      val statusCode  = jsBody.\("status").as[Int]

      logger.info(s"[toDataType] - Outbound ${Colors.yellow(httpMethod)} call to ${Colors.green(requestPath)} returned a ${Colors.cyan(statusCode.toString)}")

      val body = jsBody.\("body").as[String]

      if(needsDecrypt) body.decrypt else body
    }

    def toDataType[T](needsDecrypt: Boolean)(implicit reads: Reads[T]): T = {
      val jsBody = wsResponse.json

      val httpMethod  = jsBody.\("method").as[String]
      val requestPath = jsBody.\("uri").as[String]
      val statusCode  = jsBody.\("status").as[Int]

      logger.info(s"[toDataType] - Outbound ${Colors.yellow(httpMethod)} call to ${Colors.green(requestPath)} returned a ${Colors.cyan(statusCode.toString)}")
      if(needsDecrypt) {
        DataSecurity.decryptIntoType[JsValue](jsBody.\("body").as[String]).fold(
          _ => throw new HttpDecryptionException("Unable to decrypt response body into the specified type", statusCode),
          _.validate[T](reads).fold(
            errs => {
              logger.error("[toDataType] - Json parsing encountered errors")
              logger.error(Json.prettyPrint(JsError.toJson(JsError(errs))))
              throw new HttpJsonParseException
            },
            identity
          )
        )
      } else {
        jsBody.\("body").validate[T](reads).fold(
          errs => {
            logger.error("[toDataType] - Json parsing encountered errors")
            logger.error(Json.prettyPrint(JsError.toJson(JsError(errs))))
            throw new HttpJsonParseException
          },
          identity
        )
      }
    }

    private def logWithDetail: WSResponse = {
      val jsBody = wsResponse.json

      val httpMethod  = jsBody.\("method").as[String]
      val requestPath = jsBody.\("uri").as[String]
      val statusCode  = jsBody.\("status").as[Int]

      val errorMessage = jsBody.\("errorMessage").asOpt[String]
      val errorBody    = jsBody.\("errorBody").as[JsValue]

      logger.error(s"[logErrorAndReturn] - Outbound ${Colors.red(httpMethod)} call to ${Colors.red(requestPath)} returned a ${Colors.red(statusCode.toString)}")
      if(errorMessage.isDefined) {
        logger.error(s"[logErrorAndReturn] - Error message: ${errorMessage.get}")
        logger.error(s"[logErrorAndReturn] - Error body: ${Json.prettyPrint(errorBody)}")
      } else {
        logger.error(s"[logErrorAndReturn] - Error body: ${Json.prettyPrint(errorBody)}")
      }
      wsResponse
    }

    private def minimalLogging(url: String, method: String): WSResponse = {
      logger.error(s"[logErrorAndReturn] - Outbound ${Colors.red(method)} call to ${Colors.red(url)} returned a ${Colors.red(wsResponse.status.toString)}")
      wsResponse
    }

    def logErrorAndReturn(url: String, method: String): WSResponse = {
      Try(logWithDetail) match {
        case Success(resp) => resp
        case Failure(_)    => minimalLogging(url, method)
      }
    }
  }
}
