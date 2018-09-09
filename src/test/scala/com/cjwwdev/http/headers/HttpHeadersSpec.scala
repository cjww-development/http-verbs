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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.implicits.ImplicitDataSecurity._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest

class HttpHeadersSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar {

  val mockConfig = mock[ConfigurationLoader]

  val testHttpHeaders = new HttpHeaders {
    override protected val config = mockConfig
  }

  "contentTypeHeader" should {
    "return a tuple2 containing the content type" in {
      val result = testHttpHeaders.contentTypeHeader
      result mustBe ("Content-Type", "text/plain")
    }
  }

  "initialiseHeaderPackage" should {
    "build a header package case class and encrypt" in {
      val request = FakeRequest().withSession("cookieId" -> "testSessionId")

      val (key, value) = testHttpHeaders.initialiseHeaderPackage(request)
      key mustBe "cjww-headers"
      value.getClass mustBe classOf[String]
    }
  }

  "constructHeaderPackageFromRequestHeaders" should {
    "return a HeaderPackage by decrypting the cjww-header from a request" in {
      val encHeaderPackage = HeaderPackage("testAppId", Some("testSessionId")).encrypt

      val request = FakeRequest().withHeaders("cjww-headers" -> encHeaderPackage)

      val result = testHttpHeaders.constructHeaderPackageFromRequestHeaders(request)
      result mustBe Some(HeaderPackage("testAppId", Some("testSessionId")))
    }
  }
}
