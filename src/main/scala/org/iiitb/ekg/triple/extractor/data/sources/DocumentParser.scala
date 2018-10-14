package org.iiitb.ekg.triple.extractor.data.sources

object DocumentParser {
  def getText(path: String): Array[(Metadata, String)] = {
    val lines = scala.io.Source.fromFile(path).getLines.toArray
      .filter(_ != "")
      .map((new Metadata(path), _))
    lines
  }
}
