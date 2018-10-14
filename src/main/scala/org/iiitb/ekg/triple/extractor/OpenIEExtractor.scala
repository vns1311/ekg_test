package org.iiitb.ekg.triple.extractor

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations.RelationTriplesAnnotation
import edu.stanford.nlp.pipeline.Annotation

import scala.collection.mutable.ListBuffer

object OpenIEExtractor {
  def extract(annotation: Annotation,
              namedPhrasesWithTags: Set[String]): List[Triple] = {

    val sentences = annotation.get(classOf[SentencesAnnotation])
    val tripleBuffer = new ListBuffer[Triple]()
    sentences.forEach(s => {
      val sTriples = s.get(classOf[RelationTriplesAnnotation])
      sTriples.forEach(t => {
        val sub = t.subjectGloss()
        val obj = t.objectGloss()
        val relation = t.relationGloss()
        tripleBuffer += Triple(sub, relation, obj, "", "", t.confidence)
      })
    })
    tripleBuffer.toList
  }
}
