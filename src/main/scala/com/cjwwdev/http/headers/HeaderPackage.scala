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
import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.RequestHeader

case class HeaderPackage(appId: String,
                         cookieId: Option[String])

object HeaderPackage {
  def build(config: ConfigurationLoader)(implicit rh: RequestHeader): HeaderPackage = HeaderPackage(
    appId    = config.getServiceId(config.get[String]("appName")),
    cookieId = rh.session.data.get("cookieId")
  )

  implicit val format: OFormat[HeaderPackage] = Json.format[HeaderPackage]

  implicit val obfuscator: Obfuscator[HeaderPackage] = new Obfuscator[HeaderPackage] {
    override def encrypt(value: HeaderPackage): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val deObfuscator: DeObfuscator[HeaderPackage] = new DeObfuscator[HeaderPackage] {
    override def decrypt(value: String): Either[HeaderPackage, DecryptionError] = DeObfuscation.deObfuscate(value)
  }
}
