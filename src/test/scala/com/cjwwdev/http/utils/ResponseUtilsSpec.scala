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
import com.cjwwdev.http.mocks.MockResponse
import com.cjwwdev.security.encryption.DataSecurity
import play.api.http.Status._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class ResponseUtilsSpec extends PlaySpec with MockitoSugar with HttpExceptions with MockResponse {
  class Setup {
    val testUtil = new ResponseUtils {}

    val testEnc = DataSecurity.encryptData[String]("testString").get
  }

  "processHttpResponse" should {
    "return testString" in new Setup {
      val resp = mockResponse(testEnc, OK)
      val result = testUtil.processHttpResponse(resp)
      result mustBe resp
    }

    "throw a BadRequestException" in new Setup {
      val resp = mockResponse("",BAD_REQUEST)
      intercept[BadRequestException](testUtil.processHttpResponse(resp))
    }

    "throw a ForbiddenException" in new Setup {
      val resp = mockResponse("",FORBIDDEN)
      intercept[ForbiddenException](testUtil.processHttpResponse(resp))
    }

    "throw a NotFoundException" in new Setup {
      val resp = mockResponse("",NOT_FOUND)
      intercept[NotFoundException](testUtil.processHttpResponse(resp))
    }
  }
}
