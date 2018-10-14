package org.iiitb.ekg.rdf.builder

import java.io.{FileWriter, IOException}

import org.iiitb.ekg.triple.extractor.Triple
import org.apache.jena.rdf.model._
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.util.URIref
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS
import org.apache.jena.vocabulary.XSD

class JenaRDFBuilder(namespace: String) {

  val nameSpace = namespace
  val model = ModelFactory.createDefaultModel()
  model.setNsPrefix("", nameSpace)
  model.setNsPrefix("rdf", RDF.getURI)
  model.setNsPrefix("xsd", XSD.getURI)
  model.setNsPrefix("rdfs", RDFS.getURI)

  val confidence = model.createProperty(nameSpace + "confidence")
  val src = model.createProperty( nameSpace+"src" )
  val timestamp = model.createProperty( nameSpace+"timestamp" )

  def generateResource(triplet: Triple): Unit = {
    val statement = model.createResource
    val subject = model.createResource.addProperty(RDFS.label, triplet.sub)
    val relation = model.createProperty(nameSpace + URIref.encode(triplet.pred))
    val obj = model.createResource.addProperty(RDFS.label, triplet.obj)

    statement.addLiteral(confidence, triplet.conf)
    statement.addLiteral(src,triplet.src)
    statement.addLiteral(timestamp,triplet.timestamp)

    statement.addProperty(RDF.subject, subject)
    statement.addProperty(RDF.predicate, relation)
    statement.addProperty(RDF.`object`, obj)
  }
}
object JenaRDFBuilder {
  def showTTL(model: Model): Unit = { // Show the model in a few different formats.
    RDFDataMgr.write(System.out, model, Lang.TTL)
  }

  def showNTRIPLES(model: Model): Unit = {
    RDFDataMgr.write(System.out, model, Lang.NTRIPLES)
  }

  def showRDFXML(model: Model): Unit = {
    RDFDataMgr.write(System.out, model, Lang.RDFXML)
  }

  def showTURTLE(model: Model): Unit = {
    RDFDataMgr.write(System.out, model, Lang.TURTLE)
  }

  def showModelStatements(model: Model) = {
    // list the statements in the Model// list the statements in the Model
    val iter = model.listStatements
    // print out the predicate, subject and object of each statement
    while ({ iter.hasNext }) {
      val stmt = iter.nextStatement
      // get next statement
      val subject = stmt.getSubject
      // get the subject
      val predicate = stmt.getPredicate
      // get the predicate
      val `object` = stmt.getObject // get the object
      print(subject.toString)
      print(" " + predicate.toString + " ")
      if (`object`.isInstanceOf[Resource]) System.out.print(`object`.toString)
      else { // object is a literal
        print(" \"" + `object`.toString + "\"")
      }
      println(" .")
    }
  }
  def saveModel(model: Model, outFile: String, outLang: Lang): Unit = {
    val out = new FileWriter(outFile)
    try RDFDataMgr.write(out, model,outLang)
    finally try out.close()
    catch {
      case closeException: IOException =>
      // ignore
    }
  }
}
