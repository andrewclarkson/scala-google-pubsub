package mn.clarkson.google.pubsub


case class Message(id: String, message: Array[Byte]) {
  def getMessageString(encoding: String = "UTF-8"): String = new String(message, encoding)
}
