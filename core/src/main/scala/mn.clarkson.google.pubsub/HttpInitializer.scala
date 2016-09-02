package mn.clarkson.google.pubsub

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http._
import java.io.IOException

import com.google.api.client.util.{ExponentialBackOff, Sleeper};


class HttpInitializer(credentials: GoogleCredential) extends HttpRequestInitializer {
  def initialize(request: HttpRequest) = {
    request.setReadTimeout(2 * 60 * 1000)
    request.setInterceptor(credentials)
    val backoff = new HttpBackOffUnsuccessfulResponseHandler(
      new ExponentialBackOff())
      .setSleeper(Sleeper.DEFAULT);
    request.setUnsuccessfulResponseHandler(
      new HttpUnsuccessfulResponseHandler() {
        def handleResponse(request: HttpRequest, response: HttpResponse,
          supportsRetry: Boolean): Boolean = {
          if (credentials.handleResponse(
            request, response, supportsRetry)) {
            true
          } else if (backoff.handleResponse(request, response, supportsRetry)) {
            true
          } else {
            false
          }
        }
      }
    )
    request.setIOExceptionHandler(
      new HttpBackOffIOExceptionHandler(new ExponentialBackOff())
        .setSleeper(Sleeper.DEFAULT))
  }

}
