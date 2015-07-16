package org.nirmalya.projects.gameLobby.contents

/**
 * Created by nirmalya on 21/5/15.
 */


case class ScoreSheet (tableID: Int, location: String) extends LobbyNode {

  override def identifiedAs = "Scoresheet (" + tableID + "@" + location + ")"

  private var scores = Map[String,Int]("Unknown" -> 0)

  override def updateScore(name: String, herLatestScore: Int): Int = {

    // TODO [NS]: Use Scalaz |+| operator here (idiomatic way to update an immutable Map)
    val oldScore = scores.getOrElse(name,0)
    this.scores = this.scores + (name -> (oldScore + herLatestScore))
    (oldScore + herLatestScore)

  }

  def getLatestScoreOf(playerName: String) = scores.getOrElse(playerName,0)

  def getLatestScore = scores.toList
}
