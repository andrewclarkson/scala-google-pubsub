package mn.clarkson.google

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.pubsub.model.PubsubMessage

package object pubsub extends  {
  implicit class FilePathCredentials(path: String) extends Credentials {
    def build(): GoogleCredential = {
      GoogleCredential.fromStream(new FileInputStream(path))
    }
  }

  implicit class DefaultCredentials(unit: Unit) extends Credentials {
    def build(): GoogleCredential = GoogleCredential.getApplicationDefault
  }

  implicit class IdentityCredentials(credentials: GoogleCredential) extends Credentials {
    def build(): GoogleCredential = credentials
  }

  implicit class ByteMessage(data: Array[Byte]) extends Messages {
    def toMessage: Seq[PubsubMessage] = Seq(new PubsubMessage().encodeData(data))
  }

  implicit class ByteMessages(data: Seq[Array[Byte]]) extends Messages {
    def toMessage: Seq[PubsubMessage] = data.map(new PubsubMessage().encodeData)
  }

  implicit class StringMessage(data: String) extends Messages {
    def toMessage: Seq[PubsubMessage] = Seq(new PubsubMessage().encodeData(data.getBytes))
  }

  implicit class StringMessages(data: Seq[String]) extends Messages {
    def toMessage: Seq[PubsubMessage] = data.map(_.getBytes).map(new PubsubMessage().encodeData)
  }

  implicit class PubSubMessage(data: PubsubMessage) extends Messages {
    def toMessage: Seq[PubsubMessage] = Seq(data)
  }

  implicit class PubSubMessages(data: Seq[PubsubMessage]) extends Messages {
    def toMessage: Seq[PubsubMessage] = data
  }
}
