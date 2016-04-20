package utils


object Get {
  def withParams(url: String)(params: Map[String, String]): String = {
    if(params.isEmpty) url
    else {
      val param = params.map { case (k, v) => s"${k}=${v}"}.mkString("&")
      s"${url}?${param}"
    }
  }
}
