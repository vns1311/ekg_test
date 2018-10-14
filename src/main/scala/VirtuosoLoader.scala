package org.iiitb.ekg

import org.apache.jena.graph.{NodeFactory,Triple}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.util.FileManager
import virtuoso.jena.driver.VirtGraph


object VirtuosoLoader {
  def main(args: Array[String]): Unit = {
    val set = new VirtGraph ("consciousness1","jdbc:virtuoso://localhost:1111", "dba", "shyam")
    val model = ModelFactory.createDefaultModel()
    // use the FileManager to find the input file
    val in = FileManager.get.open(args(0))
    if (in == null) throw new IllegalArgumentException("File: " + args(0) + " not found")
    // read the RDF/XML file
    model.read(in,null)
    //RDFDataMgr.write(System.out,model,Lang.RDFXML)

    val statements = model.listStatements()
    while(statements.hasNext) {
      val stmt = statements.nextStatement()
      println(stmt.toString)
      val subject = stmt.getSubject.toString
      val predicate = stmt.getPredicate.toString
      val `object` = stmt.getObject.toString

      val sub1 = NodeFactory.createURI(subject)
      val pre1 = NodeFactory.createURI(predicate)
      val obj1 = NodeFactory.createURI(`object`)
      try
        set.add(new Triple(sub1, pre1, obj1))
      catch {
        case e: Exception => println(e.printStackTrace())
      }
    }

  }


}
