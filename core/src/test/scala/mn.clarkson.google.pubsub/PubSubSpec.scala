package mn.clarkson.google.pubsub
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import mn.clarkson.google.pubsub._

class PubSubSpec {
  PubSub("")
  PubSub("", credentials = "path.json")
  PubSub("", credentials = GoogleCredential.getApplicationDefault())
}