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

import com.cjwwdev.http.exceptions._
import com.cjwwdev.http.mocks.MockHttpUtils
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSResponse
import play.api.test.Helpers._

class EvaluateResponseSpec extends PlaySpec with MockHttpUtils {

  def testEvaluate(wsResponse: WSResponse) = EvaluateResponse("/test/uri", "Head", wsResponse)

  "EvaluateResponse" should {
    "return a WsResponse" when {
      "the status code is in the 2xx range (200)" in {
        val resp = mockResponse("", OK)
        val result = testEvaluate(resp)
        result mustBe resp
      }

      "the status code is in the 2xx range (204)" in {
        val resp = mockResponse("", NO_CONTENT)
        val result = testEvaluate(resp)
        result mustBe resp
      }
    }

    "throw a NotAcceptableException" when {
      "the status code is 409" in {
        val resp   = mockResponse("", NOT_ACCEPTABLE)
        intercept[NotAcceptableException](testEvaluate(resp))
      }
    }

    "throw a NotFoundException" when {
      "the status code is 404" in {
        val resp = mockResponse("", NOT_FOUND)
        intercept[NotFoundException](testEvaluate(resp))
      }
    }

    "throw a BadRequestException" when {
      "the status code is 400" in {
        val resp = mockResponse("", BAD_REQUEST)
        intercept[BadRequestException](testEvaluate(resp))
      }
    }

    "throw a ForbiddenException" when {
      "the status code is 403" in {
        val resp = mockResponse("", FORBIDDEN)
        intercept[ForbiddenException](testEvaluate(resp))
      }
    }

    "throw a ConflictException" when {
      "the status code is 409" in {
        val resp = mockResponse("", CONFLICT)
        intercept[ConflictException](testEvaluate(resp))
      }
    }

    "throw a ClientErrorException" when {
      "the status code is not 403, 404 or 409 but is in the 4xx range" in {
        val resp = mockResponse("", UNAUTHORIZED)
        intercept[ClientErrorException](testEvaluate(resp))
      }
    }

    "throw a ServerErrorException" when {
      "the status code in in the 5xx range" in {
        val resp = mockResponse("", INTERNAL_SERVER_ERROR)
        intercept[ServerErrorException](testEvaluate(resp))
      }
    }
  }
}
