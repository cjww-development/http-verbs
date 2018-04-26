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

package com.cjwwdev.http.headers

import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.typesafe.config.ConfigFactory
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.MimeTypes.TEXT
import play.api.mvc.Request

trait HttpHeaders {
  private val configRoot = "microservice.external-services"

  lazy val appName        = ConfigFactory.load.getString("appName")
  lazy val APPLICATION_ID = ConfigFactory.load.getString(s"$configRoot.$appName.application-id")

  def appIdHeader: (String, String)       = "appId"      -> APPLICATION_ID
  val contentTypeHeader: (String, String) = CONTENT_TYPE -> TEXT

  def sessionIdHeader(implicit request: Request[_]): (String, String) = "cookieId"  -> request.session.data.getOrElse("cookieId", "")
  def contextIdHeader(implicit request: Request[_]): (String, String) = "contextId" -> request.session.data.getOrElse("contextId", "")

  def initialiseHeaderPackage(implicit request: Request[_]): (String, String) = {
    "cjww-headers" -> HeaderPackage(APPLICATION_ID, request.session.data.getOrElse("cookieId", "")).encryptType
  }

  def constructHeaderPackageFromRequestHeaders(implicit request: Request[_]): Option[HeaderPackage] = {
    request.headers.get("cjww-headers").fold(Option.empty[HeaderPackage])(header =>
      Some(header.decryptIntoType[HeaderPackage](HeaderPackage.format))
    )
  }
}
