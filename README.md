[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/http-verbs/images/download.svg) ](https://bintray.com/cjww-development/releases/http-verbs/_latestVersion)

http-verbs
=================

Mechanisms for making HEAD, GET, POST, PUT, PATCH and DELETE requests.

To utilise this library add this to your sbt build file

```
"com.cjww-dev.libs" % "http-verbs_2.11" % "2.4.0" 
```

## About
### HttpExceptions.scala
Contains exceptions to catch errors for the session not containing the correct values as well specific http status code such as, Forbidden, Conflict, NotFound and error ranges.

<br>

### HttpHeaders.scala
Utility functions to get session and context id's from a http request header.

<br>

### ResponseUtils.scala
Contains utilities that can decrypt the response body into type **T** if the response code is in the success range. 

<br>

### SessionUtils.scala
Contains utilities to fetch Id's and names from the http session.

### Http.scala
Can be injected into a class that needs to make calls out to external services. 

For all these methods you need an implicit Request[_] in scape. 

GET needs an implicit Json Reads[T] in scope.

POST, PUT and PATCH need an implicit Json OWrites[T] in scope.

```scala
    //HEAD and GET example
    case class TestModel(str: String, int: Int)
    implicit val reads = Json.reads[TestModel]
    
    class ExampleConnector @Inject()(http: Http) {
      def exampleHeadRequest: Future[WSResponse] = {
        http.HEAD("/example/url")
      }
    
      def exampleGetTestModel: Future[TestModel] = {
        http.GET[TestModel]("/example/url")
      }
    }
```

```scala
    //POST, PUT and PATCH example
    case class TestModel(str: String, int: Int)
    implicit val writes = Json.writes[TestModel]
        
    class ExampleConnector @Inject()(http: Http) {
      def examplePost: Future[WSResponse] = {
        http.POST[TestModel]("/example/uri", TestModel("abc", 616))
      }
      
      def examplePut: Future[WSResponse] = {
        http.PUT[TestModel]("/example/uri", TestModel("abc", 616))
      }
      
      def examplePatch: Future[WSResponse] = {
        http.PATCH[TestModel]("/example/uri", TestModel("abc", 616))
      }
    }
```

```scala
    class ExampleConnector @Inject()(http: Http) {
      def exampleDelete: Future[WSResponse] = {
        http.DELETE("/example/uri")
      }
    }
```