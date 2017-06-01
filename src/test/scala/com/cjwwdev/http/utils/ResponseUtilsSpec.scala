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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{Format, Json}
import play.api.test.FakeRequest

class ResponseUtilsSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite with HttpExceptions with MockResponse {
  class Setup {
    val testUtil = new ResponseUtils {}

    val testEnc = DataSecurity.encryptString("testString").get
  }

  case class TestModel(string: String, int: Int)
  implicit val format: Format[TestModel] = Json.format[TestModel]

  "processHttpResponse" should {
    "return the response" when {
      "the response code is in the success range (2xx)" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val informationResponse = mockResponse("", OK)

        val result = testUtil.processHttpResponse(informationResponse)
        result mustBe informationResponse
      }
    }

    "throw a ClientErrorException" when {
      "the response code is the client error range (4xx)" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val informationResponse = mockResponse("", BAD_REQUEST)

        intercept[ClientErrorException](testUtil.processHttpResponse(informationResponse))
      }
    }

    "throw a ServerErrorException" when {
      "the response code is the server error range (5xx)" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val informationResponse = mockResponse("", INTERNAL_SERVER_ERROR)

        intercept[ServerErrorException](testUtil.processHttpResponse(informationResponse))
      }
    }
  }

  "processHttpResponseIntoType" should {
    "return a test model" when {
      "the response code is in the success range and the body has been decrypted" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val input = TestModel("testString", 616)
        val enc = DataSecurity.encryptType[TestModel](input).get
        val successResponse = mockResponse(enc, OK)

        val result = testUtil.processHttpResponseIntoType[TestModel](successResponse)
        result mustBe input
      }
    }

    "throw a HttpDecryptionException" when {
      "the response code is in the success range but the body couldn't be decrypted" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val successResponse = mockResponse("INVALID_BODY", OK)

        intercept[HttpDecryptionException](testUtil.processHttpResponseIntoType[TestModel](successResponse))
      }
    }

    "throw a ClientErrorException" when {
      "the response code is in the client error range" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val successResponse = mockResponse("", BAD_REQUEST)

        intercept[ClientErrorException](testUtil.processHttpResponseIntoType[TestModel](successResponse))
      }
    }

    "throw a ServerErrorException" when {
      "the response code is in the server error range" in new Setup {
        implicit val request = FakeRequest("GET", "/fake/path")
        val successResponse = mockResponse("INVALID_BODY", INTERNAL_SERVER_ERROR)

        intercept[ServerErrorException](testUtil.processHttpResponseIntoType[TestModel](successResponse))
      }
    }
  }
}
