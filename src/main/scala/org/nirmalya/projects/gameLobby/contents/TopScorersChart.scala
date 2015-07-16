package org.nirmalya.projects.gameLobby.contents

import scala.collection.immutable.SortedSet

/**
 * Created by nirmalya on 15/7/15.
 */

case class TopScore (playerName: String, latestTotal: Int)

class TopScorersChart (gameType: GameType) extends LobbyNode {

  override def identifiedAs = "TopPlayersChart (" + gameType.name  + ")"
  
  val defaultTopScoreMin = TopScore("Unknown",-1)
  val defaultTopScoreMax = TopScore("Unknown",0)

  var card =  List[TopScore](defaultTopScoreMin, defaultTopScoreMax)

  // At the moment, we keep only 5 highest scores
  def updateWithNewScore(name: String, lastScore: Int) = {

     card = (TopScore(name,lastScore) :: card).sortBy(e => -e.latestTotal).take(5)

  }

  def topScorers(mx: Int = 5) =  (List[TopScore]() ++ card).take(mx)
}
