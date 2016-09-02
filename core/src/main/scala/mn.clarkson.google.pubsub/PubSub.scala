package mn.clarkson.google.pubsub

import com.google.api.services.pubsub.{PubsubScopes, Pubsub => GooglePubSub}
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.{BasicAuthentication, HttpTransport}
import com.google.api.client.json.JsonFactory
import com.google.api.services.pubsub.model._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential

case class Client(httpTransport: HttpTransport = Utils.getDefaultTransport, jsonFactory: JsonFactory = Utils.getDefaultJsonFactory) {
  private val credentials = GoogleCredential.getApplicationDefault(httpTransport, jsonFactory)
  private val client = new GooglePubSub.Builder(httpTransport, jsonFactory, new HttpInitializer(credentials)).build()

  private def fullName(project: String, resource: String, name: String): String = s"projects/$project/$resource/$name"

  def fetch(project: String, subscription: String)
           (implicit ec: ExecutionContext): Future[List[Message]] = {
    val request = new PullRequest()
      .setReturnImmediately(false)
      .setMaxMessages(1000)
    Future {
      client.projects().subscriptions().pull(fullName(project, "subscriptions", subscription), request).execute()
    } map { response =>
      response.getReceivedMessages.toList.map(m => Message(m.getAckId, m.getMessage.decodeData()))
    }
  }

  def acknowledge(project: String, subscription: String, messages: Seq[Message])
                 (implicit ec: ExecutionContext): Future[Unit] = {
    val request = new AcknowledgeRequest()
    request.setAckIds(messages.map(_.id))
    Future {
      client.projects().subscriptions().acknowledge(fullName(project, "subscriptions", subscription), request).execute()
    }
  }

  def send(project: String, topic: String, data: List[Array[Byte]]): Future[Unit] = {
    val request = new PublishRequest()
    request.setMessages(data.map((new PubsubMessage).encodeData))
    Future {
      client.projects().topics().publish(fullName(project, "topics", topic), request).execute()
    }
  }

}
