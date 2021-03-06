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

package com.cjwwdev.http.verbs

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.headers.HttpHeaders
import javax.inject.Inject
import play.api.libs.ws.WSClient

class HttpImpl @Inject()(val wsClient: WSClient,
                         val config: ConfigurationLoader) extends Http {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait Http extends
  HttpHead with
  HttpGet with
  HttpPost with
  HttpPatch with
  HttpPut with
  HttpDelete with
  HttpHeaders {
  val wsClient: WSClient
}
