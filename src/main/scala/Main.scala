package org.iiitb.ekg

import org.iiitb.ekg.rdf.builder.JenaRDFBuilder
import org.iiitb.ekg.triple.extractor.TripleExtractor
import org.apache.jena.rdf.model.{RDFNode, Resource, StmtIterator}
import org.apache.jena.riot.Lang
import org.iiitb.ekg.triple.extractor.data.sources.DocumentParser
import org.apache.jena.query._
import org.apache.jena.query.ResultSetFormatter

object Main {
  def main(args: Array[String]) {
    val JenaRDF = new JenaRDFBuilder("http://ekg/")
    val inputFile = args(0)
    val docText = DocumentParser.getText(inputFile)
    val triples = docText.flatMap(sentence => TripleExtractor.getTriples(sentence._2))
    triples.foreach(triple => {
      JenaRDF.generateResource(triple)
    })
    JenaRDFBuilder.saveModel(JenaRDF.model,args(1),Lang.RDFXML)
  }
}
