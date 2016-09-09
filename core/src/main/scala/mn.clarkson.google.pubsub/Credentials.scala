package mn.clarkson.google.pubsub

import java.io.{FileInputStream, InputStream}

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential

trait Credentials {
  def build(): GoogleCredential
}



