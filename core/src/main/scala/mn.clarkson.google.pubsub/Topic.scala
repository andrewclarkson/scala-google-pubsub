package mn.clarkson.google.pubsub

import com.google.api.services.pubsub.Pubsub
import com.google.api.services.pubsub.model.PublishRequest

import scala.concurrent.Future
import scala.collection.JavaConversions._

case class Topic(project: String, topic: String, client: Pubsub) {
  private def topicName: String = s"projects/$project/topics/$topic"

  def parseName(fullName: String): String = fullName.split("/")(3)

  def publish(messages: Messages): Future[Unit] = {
    Future {
      client.projects().topics().publish(
        topicName,
        new PublishRequest().setMessages(messages.toMessage)
      ).execute()
    }
  }

  def delete(): Future[Unit] = Future {
    client.projects().topics().delete(topicName).execute()
  }

  def subscriptions(pageSize: Int, token: Option[String] = None): Future[ResultSet[Subscription]] = {
    val r = client.projects().topics().subscriptions().list(topic).setPageSize(pageSize)
    val request = token match {
      case Some(t) => r.setPageToken(t)
      case None => r
    }
    Future {
      request.execute()
    } map { r =>
      ResultSet(
        r.getNextPageToken,
        r.getSubscriptions.map(s => Subscription(project, parseName(s), client))
      )
    }
  }
}
