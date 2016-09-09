package mn.clarkson.google.pubsub

import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.pubsub.Pubsub

import scala.concurrent.Future
import scala.collection.JavaConversions._

case class PubSub(project: String,
                  httpTransport: HttpTransport = Utils.getDefaultTransport,
                  jsonFactory: JsonFactory = Utils.getDefaultJsonFactory,
                  credentials: Credentials = ()) {
  private val client = new Pubsub.Builder(httpTransport, jsonFactory, new HttpInitializer(credentials.build())).build()

  def parseName(fullName: String): String = fullName.split("/")(3)

  def topics(max: Int, token: Option[String] = None): Future[ResultSet[Topic]] = {
    val r = client.projects().topics().list(project).setPageSize(max)
    val request = token match {
      case Some(t) => r.setPageToken(t)
      case None => r
    }
    Future {
      request.execute()
    } map { r =>
      ResultSet(
        r.getNextPageToken,
        r.getTopics.map(t => Topic(project, parseName(t.getName), client))
      )
    }
  }

  def topic(topic: String): Topic = Topic(project, topic, client)

  def subscriptions(pageSize: Int, token: Option[String] = None): Future[ResultSet[Subscription]] = {
    val r = client.projects().subscriptions().list(project).setPageSize(pageSize)
    val request = token match {
      case Some(t) => r.setPageToken(t)
      case None => r
    }
    Future {
      request.execute()
    } map { r =>
      ResultSet(
        r.getNextPageToken,
        r.getSubscriptions
          .map(s => Subscription(project, parseName(s.getName), client))
      )
    }
  }

  def subscription(subscription: String) = Subscription(project, subscription, client)

}
