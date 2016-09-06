package mn.clarkson.google.pubsub

import akka.actor.{Actor, ActorRef}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.pubsub.{Pubsub => GooglePubSub}
import akka.pattern.pipe

case class PubSubActor(receiver: ActorRef, client: Client) extends Actor {

  implicit val ec = context.dispatcher

  override def preStart() = {
    self.tell(Poll(), self)
  }

  def receive() = {
    case Poll() =>
      client.fetch().pipeTo(self)
    case messages: List[Message] =>
      messages.foreach(receiver.tell(_, self))
      self.tell(Poll(), self)
    case Acknowledge(ids: Seq[String]) =>
      client.acknowledge(ids).pipeTo(self)
    case Unit => _
  }
}
