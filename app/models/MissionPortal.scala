package models

import scalikejdbc.{WrappedResultSet, autoConstruct}
import skinny.orm.{Alias, SkinnyJoinTable}

case class MissionPortal(missionId: Int, portalId: Int, no: Int)

object MissionPortal extends SkinnyJoinTable[MissionPortal] {
  override def defaultAlias: Alias[MissionPortal] = createAlias("mp")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[MissionPortal]): MissionPortal = autoConstruct(rs, n)
}
