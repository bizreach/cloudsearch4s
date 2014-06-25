cloudsearch4s
========
This is a Scala client of AWS CloudSearch.

## How to use

TODO: sbt dependency configuration

```scala
import jp.co.bizreach.cloudsearch4s.CloudSearch

val registerUrl = "http://xxxx"
val searchUrl   = "http://xxxx"

val cloudsearch = CloudSearch(registerUrl, searchUrl)
```

cloudsearch4s can handle documents as `Map[String, Any]` or case class. If you want to handle documents as case class, you have to define a case class which is mapped to the index in the CloudSearch at first.
If you use case class, camel case property names are mapped to lowercase with underscore.

```scala
case class Job(
  jobTitle: String,   // mapped to job_title
  jobContent: String, // mapped to job_content
  salary: Int         // mapped to salary
)
```

### Register

```scala
// Register single document by Map
val id: String = cloudsearch.registerIndexByMap(
  Map("job_title" -> "Title", "job_content" -> "Content")
)

// Register multiple documents by Map
val ids: Seq[String] = cloudsearch.registerIndicesByMap(Seq(
  Map("job_title" -> "Title", "job_content" -> "Content"),
  Map("job_title" -> "Title", "job_content" -> "Content")
))

// Register single document by case class
val id: String = cloudsearch.registerIndexByMap(
  Job("Title", "Content")
)

// Register multiple documents by case class
val ids: Seq[String] = cloudsearch.registerIndicesByMap(Seq(
  Job(Title, Content),
  Job(Title, Content)
))
```

### Update

```scala
// Update single document by Map
cloudsearch.updateIndexByMap(
  "091f2b7e-5b3b-4936-ae42-c560655a165f",
  Map("job_title" -> "Title", "job_content" -> "Content")
)

// Update multiple documents by case class
cloudsearch.updateIndices(
  Seq(
    ("091f2b7e-5b3b-4936-ae42-c560655a165f", Job("Title", "Content")),
    ("00dd1a55-0e6d-437a-9e16-8f8b50f45a20", Job("Title", "Content"))
  )
)
```

### Delete

```scala
// Delete single document
cloudsearch.removeIndex("091f2b7e-5b3b-4936-ae42-c560655a165f")

// Delete multiple documents
cloudsearch.removeIndices(Seq(
  "091f2b7e-5b3b-4936-ae42-c560655a165f",
  "00dd1a55-0e6d-437a-9e16-8f8b50f45a20"
))
```

### Search

```scala
val result: CloudSearchResult[Job] = cloudsearch.search(classOf[Job],
  // Query which is assembled using Lucene's query builder API
  new TermQuery(new Term("index_type", "crawl"))
  // Highlight settings (Optional)
  highlights = Seq(HighlightParam("job_title")),
  // Facet search settings (Optional)
  facets = Seq(FacetParam("employment_status"))
)
```

## UnitTest

To mock `CloudSearch`, use `CloudSearchMock` instead of an instance which is generated by the companion object.

```scala
val testService = new HogeService {
  override protected val cloudsearch = new CloudSearchMock(Map(
    "q=*:*" -> CloudSearchResult[Job](
      total = 1,
      hits = Seq(
        CloudSearchDocument(
          id        = "091f2b7e-5b3b-4936-ae42-c560655a165f",
          fields    = Job("Title", "Content"),
          highlight = Map.empty
        )
      ),
      facets = Map.empty
    )
  ))
  ...
}
```

Give expected queries and its result to the constructor of `CloudSearchMock`. If unspecified query is given, test is failed.
And it's possible to get history of request by `CloudSearchMock#executedRequests`. You can check whether expected requests are sent.
