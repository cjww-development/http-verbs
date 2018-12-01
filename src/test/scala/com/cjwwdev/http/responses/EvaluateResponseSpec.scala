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

package com.cjwwdev.http.responses

import com.cjwwdev.http.exceptions._
import com.cjwwdev.http.mocks.MockHttpUtils
import com.cjwwdev.http.responses.EvaluateResponse.ConnectorResponse
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import play.api.test.FakeRequest
import play.api.test.Helpers._

class EvaluateResponseSpec extends PlaySpec with MockHttpUtils {

  implicit val request = FakeRequest()

  def testEvaluate(wsResponse: WSResponse): ConnectorResponse = EvaluateResponse("/test/uri", "Head", wsResponse)

  def buildFakeBody(status: Int): JsValue = Json.parse(
    s"""
      |{
      |   "method" : "PATCH",
      |   "uri" : "/test/uri/with-body",
      |   "status" : $status,
      |   "errorMessage" : "testErrorMessage",
      |   "errorBody" : {
      |       "testKey" : [
      |           {
      |               "message" : "test"
      |           }
      |       ],
      |       "testKey2" : [
      |           {
      |               "message" : "test"
      |           }
      |       ]
      |   }
      |}
    """.stripMargin
  )

  "EvaluateResponse" should {
    "return a WsResponse" when {
      "the status code is in the 2xx range (200)" in {
        val resp = mockResponse("", OK)
        val result = testEvaluate(resp)
        result mustBe Left(resp)
      }

      "the status code is in the 2xx range (204)" in {
        val resp = mockResponse("", NO_CONTENT)
        val result = testEvaluate(resp)
        result mustBe Left(resp)
      }
    }
  }
}
