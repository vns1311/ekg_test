package org.iiitb.ekg.triple.extractor

case class Triple(val sub: String, val pred: String, val obj: String, val timestamp: String = "", val src: String = "", val conf: Double = 1.0) {
  override def toString(): String = {
    val sbuf = new StringBuilder()
    sbuf.append(sub).append("\t")
      .append(pred).append("\t")
      .append(obj).append("\t")
      .append(timestamp).append("\t")
      .append(src)
    sbuf.toString
  }
}
