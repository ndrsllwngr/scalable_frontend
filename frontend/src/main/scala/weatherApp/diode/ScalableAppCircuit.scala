package weatherApp.diode

import diode.ActionResult.ModelUpdate
import diode.{ActionHandler, Circuit}

object ScalableAppCircuit extends Circuit[ScalableAppModel] {
  def initialModel = ScalableAppModel()
  override val actionHandler: HandlerFunction =
    (model, action) => action match {
      case SetPartyId(partyId) => Some(ModelUpdate(model.copy(partyId = Some(partyId))))
      case SetSongsForParty(songs) => Some(ModelUpdate(model.copy(songList = songs)))
      case _ => None
    }

  val playlistPageHandler = new ActionHandler(zoomTo(_.state)) {
    override def handle = {
      case SetPartyId(partyId) => updated(value.copy(partyId = Some(partyId)))
      case SetSongsForParty(songs) => updated(value.copy(songList = songs))
    }
  }

  override val actionHandler = composeHandlers(playlistPageHandler)
}
