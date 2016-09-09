package mn.clarkson.google.pubsub

case class ResultSet[A](token: String, results: Seq[A])
