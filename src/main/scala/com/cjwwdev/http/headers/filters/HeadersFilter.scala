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

package com.cjwwdev.http.headers.filters

import java.util.UUID

import akka.stream.Materializer
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.implicits.ImplicitDataSecurity._
import javax.inject.Inject
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

class DefaultHeadersFilter @Inject()(implicit val mat: Materializer,
                                     protected val config: ConfigurationLoader) extends HeadersFilter {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait HeadersFilter extends Filter {
  val appId: String

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    f(rh.withHeaders(rh.headers.add(
      "cjww-headers" -> HeaderPackage.build(appId)(rh).encrypt,
      "requestId"    -> s"requestId-${UUID.randomUUID()}"
    )))
  }
}
