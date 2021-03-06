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

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import org.joda.time.LocalDateTime
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import play.api.test.FakeRequest

class WsResponseHelpersSpec extends PlaySpec {

  case class Test(testString: String, testInteger: Int)
  implicit val reads  = Json.reads[Test]
  implicit val writes = Json.writes[Test]
  implicit val obfuscator: Obfuscator[Test] = new Obfuscator[Test] {
    override def encrypt(value: WsResponseHelpersSpec.this.Test): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
  implicit val deObfuscator: DeObfuscator[Test] = new DeObfuscator[Test] {
    override def decrypt(value: String): Either[Test, DecryptionError] = DeObfuscation.deObfuscate(value)
  }

  val testApiResponse = Json.prettyPrint(Json.obj(
    "uri" -> "/test/uri",
    "method" -> "GET",
    "status" -> 200,
    "body" -> Json.obj(
      "testString" -> "testString",
      "testInteger" -> 616
    ),
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  ))

  val testEncApiResponse = Json.prettyPrint(Json.obj(
    "uri" -> "/test/uri",
    "method" -> "GET",
    "status" -> 200,
    "body" -> s"${Test("testString", 616).encrypt}",
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  ))

  val testEncStringApiResponse = Json.prettyPrint(Json.obj(
    "uri" -> "/test/uri",
    "method" -> "GET",
    "status" -> 200,
    "body" -> s"${"testString".encrypt}",
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  ))

  val testStringApiResponse = Json.prettyPrint(Json.obj(
    "uri" -> "/test/uri",
    "method" -> "GET",
    "status" -> 200,
    "body" -> "testString",
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  ))

  val testApiResponseInvalid = Json.prettyPrint(Json.obj(
    "uri" -> "/test/uri",
    "method" -> "GET",
    "status" -> 200,
    "body" -> Json.obj(
      "invalid" -> true
    ),
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  ))

  def testWsResponse(bodyIn: String): WSResponse = new WSResponse {
    override def headers: Map[String, Seq[String]]   = ???
    override def bodyAsSource: Source[ByteString, _] = ???
    override def allHeaders                          = ???
    override def underlying[T]                       = ???
    override def status                              = 200
    override def statusText                          = "OK"
    override def header(key: String)                 = ???
    override def cookies                             = ???
    override def cookie(name: String)                = ???
    override def body                                = bodyIn
    override def xml                                 = ???
    override def json: JsValue                       = Json.parse(bodyIn)
    override def bodyAsBytes                         = ???
  }

  implicit val request = FakeRequest()

  "logResponse" should {
    "return a ws response and log a success (HEAD)" in new WsResponseHelpers {
      testWsResponse("").logResponse("/test/url", "HEAD", inError = false)
    }

    "return a ws response and log an error (HEAD)" in new WsResponseHelpers {
      testWsResponse("").logResponse("/test/url", "HEAD", inError = true)
    }

    "return a ws response and log a success (GET)" in new WsResponseHelpers {
      testWsResponse("").logResponse("/test/url", "GET", inError = false)
    }

    "return a ws response and log an error (GET)" in new WsResponseHelpers {
      testWsResponse("""{ "errorMessage" : "there was an error" }""").logResponse("/test/url", "GET", inError = true)
    }
  }

  "toResponseString" should {
    "return a string without the need to decrypt" in new WsResponseHelpers {
      val result = testWsResponse(testStringApiResponse).toResponseString(needsDecrypt = false)
      result mustBe Left("testString")
    }

    "return a string after decrypting" in new WsResponseHelpers {
      val result = testWsResponse(testEncStringApiResponse).toResponseString(needsDecrypt = true)
      result mustBe Left("testString")
    }
  }

  "toDataType" should {
    "return a Test" in new WsResponseHelpers {
      val result = testWsResponse(testApiResponse).toDataType[Test](needsDecrypt = false)
      result mustBe Left(Test("testString", 616))
    }

    "return a Test (after decrypting)" in new WsResponseHelpers {
      val result = testWsResponse(testEncApiResponse).toDataType[Test](needsDecrypt = true)
      result mustBe Left(Test("testString", 616))
    }
  }
}
