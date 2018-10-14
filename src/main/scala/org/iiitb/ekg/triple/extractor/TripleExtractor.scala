package org.iiitb.ekg.triple.extractor

import java.util.Properties

import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

object TripleExtractor {
  private val props = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,natlog,openie")
  props.setProperty("threads", "8")
  props.setProperty("openie.resolve_coref", "true")
  props.setProperty("openie.triple.all_nominals", "false")
  private val pipeline = new StanfordCoreNLP(props)
  private val propsWithoutCoref = new Properties()
  propsWithoutCoref.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,natlog,openie")
  propsWithoutCoref.setProperty("threads", "8")
  private val pipelineWithoutCoref = new StanfordCoreNLP(propsWithoutCoref)

  def getAnnotation(doc: String): Annotation = {
    try {
      pipeline.process(doc)
    } catch {
      case ex: java.lang.RuntimeException => {
        println("\n\n\n\nCAUGHT java.lang.RuntimeException AT LINE 228")
        pipelineWithoutCoref.process(doc)
      }
    }
  }

  def reduceGroup(triples: List[Triple]): Triple = {
    // triples.sortWith(_.obj < _.obj).last
    triples.sortWith(_.obj.length < _.obj.length).last
  }

  def purge(triples: List[Triple]): List[Triple] = {
    val groupedTriples = triples.groupBy(t => (t.sub + t.pred)).mapValues(reduceGroup)
    groupedTriples.values.toList
  }

  def getTypeTriples(namedEntities: Set[String]): List[Triple] = {
    val triples = namedEntities.map(e => {
      val tokens = e.split(":")
      Triple(tokens(1), "rdf:type", tokens(0))
    })
    triples.toList
  }

  def getTriples(doc: String): List[Triple] = {
    // println("##########################")
    println(doc)
    // println("##########################")
    // val t1 = System.currentTimeMillis
    val annotation = getAnnotation(doc)
    // val t2 = System.currentTimeMillis
    val namedPhrases = NERExtractor.extract(annotation)
    if (namedPhrases.isEmpty) {
      List[Triple]()
    }
    else {
      println("********** NER output **********")
      namedPhrases.foreach(println)
      //val t3 = System.currentTimeMillis
      val openieTriples = OpenIEExtractor.extract(annotation, namedPhrases)

      println("********** OpenIE output **********")
      openieTriples.foreach(println)
      // val t4 = System.currentTimeMillis
      // println("getAnnotation = " + (t2-t1) + " NER = " + (t3-t2) + " OpenIE = " + (t4-t3))
      val relations = purge(openieTriples.filter(_.conf > 0.98))
      println("********** Purged output **********")
      relations.foreach(println)
      val finalTriples = getTypeTriples(namedPhrases) ::: relations
      println("********** Final output **********")
      finalTriples.foreach(println)
      finalTriples
    }
  }

}
