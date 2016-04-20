package models

import responses.MissionWithPortals
import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class RDBMission(
    id: Int,
    name: String,
    intro: String,
    latitude: Double,
    longitude: Double,
    portals: Seq[RDBPortal] = Nil,
    state: Option[MissionState] = None) extends MissionWithPortals

object RDBMission extends SkinnyCRUDMapperWithId[Int, RDBMission] {
  override val tableName = "mission"
  override val columnNames = Seq("id", "name", "intro", "latitude", "longitude")

  override val useExternalIdGenerator = true

  override def idToRawValue(id: Int): Any = id
  override def rawValueToId(value: Any): Int = value.toString.toInt

  override def defaultAlias: Alias[RDBMission] = createAlias("m")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[RDBMission]): RDBMission =
    autoConstruct(rs, n, "portals", "state")

  lazy val portalsRef = hasManyThrough[MissionPortal, RDBPortal](
    through = MissionPortal -> MissionPortal.defaultAlias,
    throughOn = (m: Alias[RDBMission], mp: Alias[MissionPortal]) => sqls.eq(m.id, mp.missionId),
    many = RDBPortal -> RDBPortal.defaultAlias,
    on = (mp: Alias[MissionPortal], p: Alias[RDBPortal]) => sqls.eq(mp.portalId, p.id),
    merge = (mission, portals) => mission.copy(portals = portals)
  )

  def stateRef(userId: Long) = {
    val m = defaultAlias
    val ms = MissionState.defaultAlias
    hasOneWithFkAndJoinCondition[MissionState](
      right = MissionState,
      fk = "missionId",
      on = sqls.eq(ms.missionId, m.id).and.eq(ms.userId, userId),
      merge = (mission, state) => mission.copy(state = state)
    )
  }
}
