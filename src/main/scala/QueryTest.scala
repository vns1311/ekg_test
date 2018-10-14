package org.iiitb.ekg

import org.apache.jena.query.{QueryExecutionFactory, QueryFactory, ResultSetFormatter}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFWriter}
import org.apache.jena.util.FileManager

object QueryTest {
  def main(args: Array[String]): Unit = {
    // create an empty model// create an empty model
    val model = ModelFactory.createOntologyModel
    // use the FileManager to find the input file
    val in = FileManager.get.open(args(0))
    if (in == null) throw new IllegalArgumentException("File: " + args(0) + " not found")
    // read the RDF/XML file
    model.read(in,null)
    RDFDataMgr.write(System.out,model,Lang.RDFXML)

    val queryString = "SELECT ?x WHERE { ?x ?x ?x }"
    val query = QueryFactory.create(queryString) ;
    try {
      val qexec = QueryExecutionFactory.create(query, model)
      val results = qexec.execSelect()
      while (results.hasNext()) {
        val soln = results.nextSolution()
        val x = soln.get("varName") // Get a result variable by name.
        val r = soln.getResource("VarR")// Get a result variable - must be a resource
        val l = soln.getLiteral("VarL") // Get a result variable - must be a literal
      }
      ResultSetFormatter.outputAsCSV(results)
    }
  }

}
