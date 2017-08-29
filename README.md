##  Play-Full

Here you will find a library that takes a lot of the boiler plate out of writing controllers in 
the Play Framework. This project
aims and reducing the boiler plate in our controllers leaving our code cleaner and more testable.

Here are some things you get for free with this library:
1. Query/JSON Validation
1. Logging for every request
1. Consistent JSON formats (Request and Response)
1. Guidelines for proper REST API's

### Philosophy of request parsing and response rendering
 
Your business logic is in the form of A -> B, you have some type A, you do your business 
logic then you return a B. So really your whole app is just map function. The problem is that 
our API's don't talk in scala object's, like A and B, they talk in HTTP. Oo first we 
need to convert the HTTP request to an A and then convert to the HTTP response from the B. 
Therefore our whole new translation is HTTP-Req -> (A -> B) -> HTTP-Resp. Since this is done 
in every single endpoint of a service, we have removed the boiler plate of having to 
parse/validate forms/json and render output/json. This library will help with everything 
you need to do in a controller, and let you focus on writing the business logic of your application.

### How to use 

To accomplish all this cool boilerplate killing, we need to define some new types. The first
class is `Schema`, this is what keeps the recipe for HTTP -> A and B -> HTTP.
`Schema` has 2 properties, a `reads` and a `writes`. The `reads` is of type `ScheamReads[A]` and is the 
reader of HTTP (HTTP -> A), and then the `writes` is of `SchemaWrites[B]` and is the writer
of HTTP (B -> HTTP). For more info on `ScheamReads[A]` and `ScheamWrites[B]` see their definitions below. 

Ill give you a quick example here and go into detail below.

To create a schema to use with a create artist API, you would do something like:
```scala
import io.lbert.play.controllers._
import io.lbert.app.models.ArtistJson._

val createArtistSchema =
    Schema(
      reads = SchemaReads()
        .JSON(artistReads).withKey("artist"),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
        .Status.Created
    )
```
This says to create a schema with the `artistReads` JSON reader nested under the key of `artist` and
write using the `artistWrites` JSON writer under the key of artist, but also in writing the HTTP response
we want to use the `201 Created` status.
 
The example request would look like
```json
{
  "artist": {
    "id": "506fca3e-5220-41a8-8506-151b2eee8adb",
    "name": "Foo"
  }
}
```
and the example response would look like
```json
{
  "artist": {
    "id": "506fca3e-5220-41a8-8506-151b2eee8adb",
    "name": "Foo"
  }
}
```
with a `201` return code.

#### Request => A
Here we will talk about parsing the request into type A.

Things you might want to parse:

1. Query params
1. Body
1. Headers

Things you might want to do with parsed data:

1. Validate (query/body)
1. Authenticate
1. Authorize

#### `ScheamReads[A]`

`SchemaReads[A]` is the definition of how to parse and validate the HTTP request. The `A` here is
what type you parse the request into. So if you want to authenticate someone, or if you want to 
parse a Json body for required data, you would define a `SchemaReads[A]`.

First off lets go over the base case to define a reads schema that does nothing:
```scala
import io.lbert.play.controllers.SchemaReads
val readsSchema = SchemaReads()
```

#### B => Response

#### Things to work on

1. Using failed futures to carry sad path
 
Since were not sure if we want to go all crazy with scalaz, its hard to deal with types like
`Future[Either[String,Artist]]` since you would have to do a `_.map(_.map(artist => ...))`
to get to the happy path... which is real ugly and makes using for comprehensions pretty much impossible. 
If we used scalaz here we could use monad transformers, but that conversation is left for another day. 

To get around this I made a controversial decision in using the sad path of futures 
(yea i know... it's an Exception) to carry the sad path of the code. So to fail some function 
you would do something like `Future.failed(Error("WTF you doin"))` instead of 
`Future.successful(Left("WTF you doin""))`.

#### REST

Since we would like to be RESTful with our API's there are some helpers to reduce even more
boiler plate in your code. Here we will follow the [api-styleguide].
  

Action | Method | Request Type | Response Type | Happy Code | Path 
--- | --- | --- | --- | --- | --- 
Get    | GET | Path | JSON | 200 | `/resources/:id` 
Search | GET | Query | JSON | 200 | `/resources?name=foo` 
Create | POST | JSON | JSON | 201 | `/resources`
Update | PUT | Path & JSON | JSON | 200 | `/resources/:id`   
Delete | DELETE | Path | No Content | 204 | `/resources/:id` 
