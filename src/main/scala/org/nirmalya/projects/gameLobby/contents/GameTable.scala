package org.nirmalya.projects.gameLobby.contents

case class GameTable (tableID: Int, tableLocation: String = "NA", stakeValue: Float = 0.0f) extends LobbyNode {

  def identifiedAs = s"$tableID, at $tableLocation"

  override def equals(that: Any): Boolean =

    that match {
      case p: GameTable => p.canEqual(this) && (this.hashCode == that.hashCode)
      case _ => false
    }

  // '==' and '##' should go together
  override def hashCode:Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (tableID == 0) 0 else tableID.hashCode)
    result
  }
  
}