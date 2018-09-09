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

import com.cjwwdev.http.exceptions.HttpJsonParseException
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.logging.Logging
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import com.cjwwdev.security.deobfuscation.DeObfuscator
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import play.utils.Colors

import scala.util.{Failure, Success, Try}

trait WsResponseHelpers {
  implicit class WSResponseOps(wsResponse: WSResponse) extends Logging {

    def toResponseString(needsDecrypt: Boolean): String = {
      val jsBody = wsResponse.json

      val httpMethod  = jsBody.\("method").as[String]
      val requestPath = jsBody.\("uri").as[String]
      val statusCode  = jsBody.\("status").as[Int]

      logger.info(s"[toDataType] - Outbound ${Colors.yellow(httpMethod)} call to ${Colors.green(requestPath)} returned a ${Colors.cyan(statusCode.toString)}")

      val body = jsBody.\("body").as[String]

      if(needsDecrypt) body.decrypt[String].fold(identity, x => throw x.asInstanceOf[Throwable]) else body
    }

    def toDataType[T](needsDecrypt: Boolean)(implicit deObfuscator: DeObfuscator[T], reads: Reads[T]): T = {
      val jsBody = wsResponse.json

      val httpMethod  = jsBody.get[String]("method")
      val requestPath = jsBody.get[String]("uri")
      val statusCode  = jsBody.get[Int]("status")

      logger.info(s"[toDataType] - Outbound ${Colors.yellow(httpMethod)} call to ${Colors.green(requestPath)} returned a ${Colors.cyan(statusCode.toString)}")
      if(needsDecrypt) {
        deObfuscator.decrypt(jsBody.get[String]("body")).fold(identity, x => throw x.asInstanceOf[Throwable])
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

      val httpMethod  = jsBody.get[String]("method")
      val requestPath = jsBody.get[String]("uri")
      val statusCode  = jsBody.get[Int]("status")

      val errorMessage = jsBody.get[String]("errorMessage")
      val errorBody    = jsBody.getOption[JsValue]("errorBody")

      logger.error(s"[logErrorAndReturn] - Outbound ${Colors.red(httpMethod)} call to ${Colors.red(requestPath)} returned a ${Colors.red(statusCode.toString)}")
      if(errorBody.isDefined) {
        logger.error(s"[logErrorAndReturn] - Error message: $errorMessage")
        logger.error(s"[logErrorAndReturn] - Error body: ${Json.prettyPrint(errorBody.get)}")
      } else {
        logger.error(s"[logErrorAndReturn] - Error message: $errorMessage")
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
