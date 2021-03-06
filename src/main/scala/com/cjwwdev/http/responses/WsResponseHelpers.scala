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

import com.cjwwdev.http.exceptions.{HttpDecryptionError, HttpError, HttpJsonParseError}
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.logging.output.Logger
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import com.cjwwdev.security.deobfuscation.DeObfuscator
import com.typesafe.config.ConfigFactory
import play.api.http.HttpVerbs
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import play.api.mvc.Request
import play.utils.Colors

import scala.util.Try

trait WsResponseHelpers {
  implicit class WSResponseOps(wsResponse: WSResponse) extends Logger {
    private val colouredOutput: Boolean = Try(ConfigFactory.load().getBoolean("logging.colouredOutput")).getOrElse(false)

    def logResponse(url: String, method: String, inError: Boolean)(implicit request: Request[_]): WSResponse = {
      wsResponse.logOutboundCall(url, method, inError)
      if(inError && (method != HttpVerbs.HEAD)) wsResponse.logError else wsResponse
    }

    protected def logOutboundCall(url: String, method: String, inError: Boolean)(implicit request: Request[_]): WSResponse = {
      val methodColour: String = logInColour(method, inError)(Colors.yellow)
      val urlColour: String    = logInColour(url, inError)(Colors.green)
      val statusColour: String = logInColour(wsResponse.status.toString, inError)(Colors.cyan)
      val logString = s"Outbound $methodColour call to $urlColour returned a $statusColour"
      if(inError) LogAt.error(logString) else LogAt.info(logString)
      wsResponse
    }

    protected def logError(implicit request: Request[_]): WSResponse = {
      val jsBody       = wsResponse.json
      val errorMessage = jsBody.get[String]("errorMessage")
      val errorBody    = jsBody.getOption[JsValue]("errorBody")

      if(errorBody.isDefined) {
        LogAt.error(s"Error message: $errorMessage -> Error body: ${Json.prettyPrint(errorBody.get)}")
      } else {
        LogAt.error(s"Error message: $errorMessage")
      }
      wsResponse
    }

    protected def logInColour(log: String, inError: Boolean)(logString: String => String): String = {
      (inError, colouredOutput) match {
        case (true,  true) => Colors.red(log)
        case (false, true) => logString(log)
        case (_,_)         => log
      }
    }

    def toResponseString(needsDecrypt: Boolean): Either[String, HttpError] = {
      val jsBody = wsResponse.json
      val body   = jsBody.\("body").as[String]

      if(needsDecrypt) body.decrypt[String].map(_ => HttpDecryptionError) else Left(body)
    }

    def toDataType[T](needsDecrypt: Boolean)(implicit request: Request[_], deObfuscator: DeObfuscator[T], reads: Reads[T]): Either[T, HttpError] = {
      val jsBody = wsResponse.json

      if(needsDecrypt) {
        deObfuscator.decrypt(jsBody.get[String]("body")).map(_ => HttpDecryptionError)
      } else {
        jsBody.\("body").validate[T](reads).fold(
          errs => {
            LogAt.error(s"[toDataType] - Json parsing encountered errors -> ${Json.prettyPrint(JsError.toJson(JsError(errs)))}")
            Right(HttpJsonParseError)
          },
          Left(_)
        )
      }
    }
  }
}
