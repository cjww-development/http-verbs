// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cjwwdev.http.utils

import com.cjwwdev.config.ConfigurationLoader
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.MimeTypes.TEXT
import play.api.mvc.Request

import scala.util.{Failure, Success, Try}

trait HttpHeaders {
  val configLoader: ConfigurationLoader

  private val appName = configLoader.loadedConfig.underlying.getString("appName")
  private val APPLICATION_ID = configLoader.loadedConfig.underlying.getString(s"microservice.external-services.$appName.application-id")

  def appIdHeader: (String, String)       = "appId"      -> APPLICATION_ID
  def contentTypeHeader: (String, String) = CONTENT_TYPE -> TEXT

  def sessionIdHeader(implicit request: Request[_]): (String, String) = Try(request.session("cookieId")) match {
    case Success(sId) => "cookieId" -> sId
    case Failure(_)   => "cookieId" -> "INVALID_SESSION_ID"
  }

  def contextIdHeader(implicit request: Request[_]): (String, String) = Try(request.session("contextId")) match {
    case Success(cId) => "contextId" -> cId
    case Failure(_)   => "contextId" -> "INVALID_CONTEXT_ID"
  }
}
