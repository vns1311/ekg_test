package org.iiitb.ekg.triple.extractor

import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.mutable.ListBuffer

object NERExtractor {

  def extract(annotation: Annotation): Set[String] = {

    def isNounPhrase(pos: String): Boolean = {
      if (pos == "NN" || pos == "NNS" || pos == "NNP" || pos == "NNPS") true
      else false
      //if (pos == "NNP" || pos == "NNPS") true else false
    }

    val sentences = annotation.get(classOf[SentencesAnnotation])
    var isLastWordNP = false
    var npList = new ListBuffer[String]()
    val namedPhrases = new ListBuffer[String]()

    sentences.forEach(s => {
      val tokens = s.get(classOf[TokensAnnotation])
      tokens.forEach(t => {
        val word = t.get(classOf[TextAnnotation])
        val pos = t.get(classOf[PartOfSpeechAnnotation])
        val nerLabel = t.get(classOf[NamedEntityTagAnnotation])
        val isNP = isNounPhrase(pos)
        println(
          "[" + word + "] POS [" + pos + "]" + "] NER [" + nerLabel + "] isNP [" + isNP + "]")
        if (isNP) {
          if (npList.size == 0) {
            npList += (nerLabel + ":" + word)
          } else {
            npList += word
          }
        } else if (!isNP && isLastWordNP) {
          namedPhrases += npList.toList.mkString(" ")
          npList = new ListBuffer[String]()
        }
        isLastWordNP = isNP
      })
      if (isLastWordNP) {
        namedPhrases += npList.toList.mkString(" ")
      }
      isLastWordNP = false
      npList = new ListBuffer[String]()
    })
    namedPhrases.toList.toSet
  }

  def extract(doc: String, pipeline:StanfordCoreNLP): Set[String] = {
    val annotation = pipeline.process(doc)
    extract(annotation)
  }

  def fancyextract(annotation: Annotation): Set[String] = {

    def isNounPhrase(pos: String): Boolean = {
      if (pos == "NN" || pos == "NNS" || pos == "NNP" || pos == "NNPS") true
      else false
      //if (pos == "NN" || pos == "NNS" || pos == "NNP" || pos == "NNPS") true else false
    }

    def isDT(pos: String): Boolean = {
      if (pos == "DT") true else false
    }

    def isLRB(pos: String): Boolean = {
      if (pos == "-LRB-") true else false
    }

    val sentences = annotation.get(classOf[SentencesAnnotation])
    var isLastWordNP = false
    var npList = new ListBuffer[String]()
    var posList = new ListBuffer[String]()
    val namedPhrases = new ListBuffer[String]()

    sentences.forEach(s => {
      val tokens = s.get(classOf[TokensAnnotation])
      tokens.forEach(t => {
        val word = t.get(classOf[TextAnnotation])
        val pos = t.get(classOf[PartOfSpeechAnnotation])
        val nerLabel = t.get(classOf[NamedEntityTagAnnotation])

        // println("[" + word + "] POS [" + pos + "] NER [" + nerLabel + "]")
        if (isDT(pos) || isNounPhrase(pos)) {
          if (npList.size == 0) {
            npList += (nerLabel + ":" + word)
          } else {
            npList += word
          }
          posList += pos
        } else {
          val poses = posList.toList
          val mentions = npList.toList
          if (poses.size > 0) {
            if (poses.head == "-LRB-" || poses.head == "DT") {
              if (mentions.drop(1).size > 1) {
                namedPhrases += (mentions.head.split(":")(0) + ":" + mentions
                  .drop(1)
                  .mkString(" "))
              }
            } else if (poses.head == "NNP" || poses.head == "NNPS") {
              if (mentions.size > 1) {
                namedPhrases += mentions.mkString(" ")
              }
            }

            posList.clear()
            npList.clear()
            if (isLRB(pos)) {
              npList += (nerLabel + ":" + word)
              posList += pos
            }
          }
        }
      })
    })
    namedPhrases.toList.toSet
  }

  def fancyextract(doc: String, pipeline:StanfordCoreNLP): Set[String] = {
    val annotation = pipeline.process(doc)
    fancyextract(annotation)
  }
}
