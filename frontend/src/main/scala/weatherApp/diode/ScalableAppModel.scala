package weatherApp.diode

import diode.Action
import weatherApp.models.Song


case class ScalableAppModel(
                     state: ScalableAppState = ScalableAppState()
                   )

case class ScalableAppState (
                            partyId: Option[String] = None,
                            songList: List[Song] = List()
                            )

case class SetPartyId(partyId: String) extends Action

case class SetSongsForParty(songList: List[Song]) extends Action


