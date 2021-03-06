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

package com.cjwwdev.http.exceptions

class HttpHeaderException(message: String) extends Exception(message)
class HttpException(message: String) extends Exception(message)

class CookieIdNotFound(message: String) extends HttpHeaderException(message)
class ContextIdNotFound(message: String) extends HttpHeaderException(message)
class FirstNameNotFound(message: String) extends HttpHeaderException(message)
class LastNameNotFound(message: String) extends HttpHeaderException(message)


sealed trait HttpError
case object HttpJsonParseError extends HttpError
case object HttpDecryptionError extends HttpError