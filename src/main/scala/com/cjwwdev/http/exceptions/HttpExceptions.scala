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

package com.cjwwdev.http.exceptions

import play.api.http.Status._

trait HttpExceptions {
  class HttpHeaderException(message: String) extends Exception(message)
  class HttpException(message: String, responseCode: Int) extends Exception(message)

  class CookieIdNotFound(message: String) extends HttpHeaderException(message)
  class ContextIdNotFound(message: String) extends HttpHeaderException(message)
  class FirstNameNotFound(message: String) extends HttpHeaderException(message)
  class LastNameNotFound(message: String) extends HttpHeaderException(message)

  class BadRequestException(message: String) extends HttpException(message, BAD_REQUEST)
  class ForbiddenException(message: String) extends HttpException(message, FORBIDDEN)
  class NotFoundException(message: String) extends HttpException(message, NOT_FOUND)
  class ConflictException(message: String) extends HttpException(message, CONFLICT)

  class InternalServerErrorException(message: String) extends HttpException(message, INTERNAL_SERVER_ERROR)
}