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
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.Reads
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

trait ResponseUtils extends HttpExceptions {
  class Contains(r : Range) { def unapply(i : Int) : Boolean = r contains i }

  private val success       = new Contains(200 to 299)
  private val client        = new Contains(400 to 499)
  private val server        = new Contains(500 to 699)

  def processHttpResponse(wsResponse: WSResponse)(implicit request: Request[_]): WSResponse = {
    wsResponse.status match {
      case success()        => wsResponse
      case client()         => throw new ClientErrorException(s"Response was ${wsResponse.statusText} (${wsResponse.status}) from ${request.path}")
      case server()         => throw new ServerErrorException(s"Response was ${wsResponse.statusText} (${wsResponse.status}) from ${request.path}")
    }
  }

  def processHttpResponseIntoType[T](wsResponse: WSResponse)(implicit request: Request[_], reads: Reads[T]): T = {
    wsResponse.status match {
      case success()        => DataSecurity.decryptIntoType[T](wsResponse.body) match {
        case Some(data)     => data
        case None           => throw new HttpDecryptionException(s"Response body failed decryption from ${request.path} (response code ${wsResponse.status})")
      }
      case client()         => throw new ClientErrorException(s"Response was ${wsResponse.statusText} (${wsResponse.status}) from ${request.path}")
      case server()         => throw new ServerErrorException(s"Response was ${wsResponse.statusText} (${wsResponse.status}) from ${request.path}")
    }
  }
}
