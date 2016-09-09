package mn.clarkson.google.pubsub

import com.google.api.services.pubsub.model.PubsubMessage

trait Messages {
  def toMessage: Seq[PubsubMessage]
}
