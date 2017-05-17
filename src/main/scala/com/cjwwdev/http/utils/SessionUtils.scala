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

import com.cjwwdev.http.exceptions.HttpExceptions
import play.api.Logger
import play.api.mvc.Request

trait SessionUtils extends HttpExceptions {
  def getCookieId(implicit request: Request[_]): String = {
    request.session.get("cookieId") match {
      case Some(id) => id
      case None =>
        Logger.error("[SessionUtils] - [getCookieId] : cookieId is not present in request session!")
        "invalid-cookie"
    }
  }

  def getContextId(implicit request: Request[_]): String = {
    request.session.get("contextId") match {
      case Some(id) => id
      case None =>
        Logger.error("[SessionUtils] - [getContextId] : contextId is not present in request session!")
        "invalid-context"
    }
  }

  def getDeversityId(implicit request: Request[_]): String = {
    request.session.get("devId") match {
      case Some(id) => id
      case None =>
        Logger.error("[SessionUtils] - [getDeversityId] : contextId is not present in request session!")
        "invalid-context"
    }
  }

  def getFirstName(implicit request: Request[_]): String = {
    request.session.get("firstName") match {
      case Some(name) => name
      case None =>
        Logger.error("[SessionUtils] - [getFirstName] : First name is not present in the request session!")
        throw new FirstNameNotFound("ERROR User not authenticated; no firstName present in request session")
    }
  }

  def getLastName(implicit request: Request[_]): String = {
    request.session.get("lastName") match {
      case Some(name) => name
      case None =>
        Logger.error("[SessionUtils] - [getLastName] : Last name is not present in the request session!")
        throw new LastNameNotFound("ERROR User not authenticated; no lastName present in request session")
    }
  }
}
