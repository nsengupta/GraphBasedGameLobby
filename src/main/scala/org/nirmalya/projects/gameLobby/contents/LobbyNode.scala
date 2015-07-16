package org.nirmalya.projects.gameLobby.contents

trait ScoreBoardLike { def updateScore(pName: String, scoreValue: Int): Int }

abstract class LobbyNode extends ScoreBoardLike {

  def identifiedAs: String

  override def updateScore(pName: String, scoreValue: Int) = 0

}