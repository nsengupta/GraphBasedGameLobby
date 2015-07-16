package org.nirmalya.projects.gameLobby.contents

/**
 * Created by nirmalya on 29/5/15.
 */

case class Player (val name: String, val loggedInAt: String = "11-07-2015" ) extends LobbyNode {
  override def identifiedAs = s"$name, logged in at $loggedInAt"

  override def canEqual(a: Any) = a.isInstanceOf[Player]

  override def equals(that: Any): Boolean =

    that match {
      case p: Player => p.canEqual(this) && (this.hashCode == that.hashCode)
      case _ => false
    }

  // '==' and '##' should go together
  override def hashCode:Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (name == null) 0 else name.hashCode)
    result
  }
}



