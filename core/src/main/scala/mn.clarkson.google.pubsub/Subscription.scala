package mn.clarkson.google.pubsub

import java.io.{InputStream, Reader}

import com.google.api.services.pubsub.Pubsub
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.pubsub.model._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential

import scala.io.Source

case class Subscription(project: String, subscription: String, client: Pubsub) {

  private def subscriptionName: String = s"projects/$project/subscriptions/$subscription"

  def pull(wait: Boolean = true, max: Int = 100)
          (implicit ec: ExecutionContext): Future[List[Message]] = {
    val request = new PullRequest()
      .setReturnImmediately(!wait)
      .setMaxMessages(max)
    Future {
      client.projects().subscriptions().pull(subscriptionName, request).execute()
    } map { response =>
      response.getReceivedMessages.toList.map(m => Message(m.getAckId, m.getMessage.decodeData()))
    }
  }

  def acknowledge(ids: Seq[String])
                 (implicit ec: ExecutionContext): Future[Unit] = {
    val request = new AcknowledgeRequest()
    request.setAckIds(ids)
    Future {
      client.projects().subscriptions().acknowledge(subscriptionName, request).execute()
    }
  }

}
