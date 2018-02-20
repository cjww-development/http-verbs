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

import com.cjwwdev.security.encryption.DataSecurity
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest

class HttpHeadersSpec extends PlaySpec with GuiceOneAppPerSuite {
  val testHttpHeaders = new HttpHeaders {
    override lazy val appName        = "testAppName"
    override lazy val APPLICATION_ID = "testAppId"
  }

  "appIdHeader" should {
    "return a tuple2 containing the appId" in {
      val result = testHttpHeaders.appIdHeader
      result mustBe ("appId", "testAppId")
    }
  }

  "contentTypeHeader" should {
    "return a tuple2 containing the content type" in {
      val result = testHttpHeaders.contentTypeHeader
      result mustBe ("Content-Type", "text/plain")
    }
  }

  "sessionIdHeader" should {
    "return a tuple2 containing the current requests session id" in {
      val request = FakeRequest().withSession("cookieId" -> "testSessionId")

      val result = testHttpHeaders.sessionIdHeader(request)
      result mustBe ("cookieId", "testSessionId")
    }
  }

  "contextIdHeader" should {
    "return a tuple2 containing the current requests context id" in {
      val request = FakeRequest().withSession("contextId" -> "testContextId")

      val result = testHttpHeaders.contextIdHeader(request)
      result mustBe ("contextId", "testContextId")
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
      val encHeaderPackage = DataSecurity.encryptType(HeaderPackage("testAppId", "testSessionId"))(HeaderPackage.format)

      val request = FakeRequest().withHeaders("cjww-headers" -> encHeaderPackage)

      val result = testHttpHeaders.constructHeaderPackageFromRequestHeaders(request)
      result mustBe Some(HeaderPackage("testAppId", "testSessionId"))
    }
  }
}
