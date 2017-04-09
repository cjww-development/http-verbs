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
import play.api.libs.ws.WSResponse
import play.api.http.Status._

trait ResponseUtils extends HttpExceptions {

  def processHttpResponse(wsResponse: WSResponse): WSResponse = {
    wsResponse.status match {
      case OK                     => wsResponse
      case BAD_REQUEST            => throw new BadRequestException(s"There was a problem ")
      case FORBIDDEN              => throw new ForbiddenException(s"Action was declared forbidden! ${wsResponse.status}")
      case NOT_FOUND              => throw new NotFoundException(s"Http request to destination has returned a ${wsResponse.status}")
      case CONFLICT               => throw new ConflictException(s"Http request to destination has return a ${wsResponse.status}")
      case INTERNAL_SERVER_ERROR  => throw new InternalServerErrorException(s"The destination server has encountered an error ${wsResponse.status}")
    }
  }
}
