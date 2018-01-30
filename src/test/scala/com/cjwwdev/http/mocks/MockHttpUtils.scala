/*
 *  Copyright 2018 CJWW Development
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.cjwwdev.http.mocks

import org.scalatest.mockito.MockitoSugar
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

trait MockHttpUtils extends MockitoSugar {
  implicit val duration: Duration = 5.seconds
  def await[T](future : Awaitable[T]) : T = Await.result(future, duration)

  val mockWsClient = mock[WSClient]

  def mockResponse(bodyIn: String, code: Int) = new WSResponse {
    override def statusText           = ""
    override def underlying[T]        = ???
    override def xml                  = ???
    override def body                 = bodyIn
    override def header(key: String)  = ???
    override def cookie(name: String) = ???
    override def bodyAsBytes          = ???
    override def cookies              = ???
    override def status               = code
    override def json                 = ???
    override def allHeaders           = ???
  }
}
