package responses

class JSONParseError(json: String, className: String) extends Exception() {
  override def getMessage: String = s"JSONParseError(to ${className}): ${json}"
}
