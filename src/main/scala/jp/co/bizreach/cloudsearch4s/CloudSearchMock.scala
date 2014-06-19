package jp.co.bizreach.cloudsearch4s

import CloudSearch._

/**
 * Mock for [[jp.co.bizreach.cloudsearch4s.CloudSearch]].
 *
 * @param params Excepted query and its result
 */
class CloudSearchMock(params: Map[String, CloudSearchResult[_]] = Map.empty) extends CloudSearchImpl("", "") {

  private val actions = scala.collection.mutable.ListBuffer[(String, String)]()

  override protected def executePostRequest(url: String, json: String): String  = {
    actions += ("POST" -> json)
    ""
  }

  override protected def searchInternal[T](url: String, queryString: String, clazz: Class[T]): CloudSearchResult[T] = {
    actions += ("GET" -> queryString)
    params(queryString).asInstanceOf[CloudSearchResult[T]]
  }

  /**
   * History of executed request.
   *
   * @return List of tuple which contains HTTP method (GET or POST) and parameter (query string or JSON string)
   */
  def executedActions: List[(String, String)] = actions.toList

}
