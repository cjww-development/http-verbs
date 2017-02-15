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
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeRequest

class HttpSessionSpec extends PlaySpec with HttpExceptions with OneAppPerSuite {

  class Setup {
    val testHttpSession = new SessionUtils{}
  }

  "getCookieId" should {
    "return the cookieId present in the http session" in new Setup {
      implicit val request = FakeRequest().withSession("cookieId" -> "testCookieId")

      val result = testHttpSession.getCookieId
      result mustBe "testCookieId"
    }

    "throw a CookieIdNotFound exception if the cookieId is not present in the http session" in new Setup {
      implicit val request = FakeRequest()
      intercept[CookieIdNotFound](testHttpSession.getCookieId)
    }
  }

  "getContextId" should {
    "return the contextId present in the http session" in new Setup {
      implicit val request = FakeRequest().withSession("contextId" -> "testContextId")

      val result = testHttpSession.getContextId
      result mustBe "testContextId"
    }

    "throw a ContextIdNotFound exception if the contextId is not present in the http session" in new Setup {
      implicit val request = FakeRequest()
      intercept[ContextIdNotFound](testHttpSession.getContextId)
    }
  }

  "getFirstName" should {
    "return the firstName present in the http session" in new Setup {
      implicit val request = FakeRequest().withSession("firstName" -> "testFirstName")

      val result = testHttpSession.getFirstName
      result mustBe "testFirstName"
    }

    "throw a FirstNameNotFound exception if the firstName is not present in the http session" in new Setup {
      implicit val request = FakeRequest()
      intercept[FirstNameNotFound](testHttpSession.getFirstName)
    }
  }

  "getLastName" should {
    "return the lastName present in the http session" in new Setup {
      implicit val request = FakeRequest().withSession("lastName" -> "testLastName")

      val result = testHttpSession.getLastName
      result mustBe "testLastName"
    }

    "throw a LastNameNotFound exception if the lastName is not present in the http session" in new Setup {
      implicit val request = FakeRequest()
      intercept[LastNameNotFound](testHttpSession.getLastName)
    }
  }
}
