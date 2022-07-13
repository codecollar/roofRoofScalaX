import java.io.File
import java.io.PrintWriter
import scalaj.http._
import scala.xml.{XML, NodeSeq, Elem}
import scala.io.StdIn
import com.github.nscala_time.time.Imports._

object hwReadRssFeed extends App {
  val searchTermInput: String = StdIn.readLine()
  val lowerCaseSearchTerm: String = searchTermInput.toLowerCase()
  val listOfProvider = List(
    "https://news.un.org/feed/subscribe/en/news/all/rss.xml",
    "https://rss.nytimes.com/services/xml/rss/nyt/World.xml",
    "http://feeds.bbci.co.uk/news/rss.xml"
  )
  def retrieveFeed(url: String): NodeSeq = {
    val res: HttpResponse[String] = Http(
      url
    ).asString
    val resBody: String = res.body
    val elements: Elem = XML.loadString(resBody)
    val titles: NodeSeq = elements.\\("item").\("title")
    return titles
  }
  val searchDatetimeNow: DateTime = DateTime.now()
  val format: String = "yyyy-MM-dd-hh-mm-ss"
  val searchDatetime: String = searchDatetimeNow.toString(format)
  val fileName: String = lowerCaseSearchTerm + "-" + searchDatetime + ".txt"

  var feeds: String = ""
  val writer = new PrintWriter(new File(fileName))
  for {
    provider <- listOfProvider
    feeds = retrieveFeed(provider)
    title <- feeds
    lowerCaseTitle = title.text.toLowerCase()
    if lowerCaseTitle.contains(lowerCaseSearchTerm)
  } yield writer.write("\n" + lowerCaseTitle)
  writer.close()
}
