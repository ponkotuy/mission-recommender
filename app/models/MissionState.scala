package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class MissionState(id: Long, missionId: Int, userId: Long, isClear: Boolean, feedback: Int)

object MissionState extends SkinnyCRUDMapperWithId[Long, MissionState] {
  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  override def defaultAlias: Alias[MissionState] = createAlias("ms")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[MissionState]): MissionState = autoConstruct(rs, n)

  def updateClear(missionId: Int, userId: Long)(implicit session: DBSession) = applyUpdate {
    insert.into(MissionState).namedValues(
      column.missionId -> missionId,
      column.userId -> userId,
      column.isClear -> true
    ).append(sqls"ON DUPLICATE KEY UPDATE ${column.isClear} = true")
  }

  def updateFeedback(missionId: Int, userId: Long, feedback: Int)(implicit session: DBSession) = applyUpdate {
    insert.into(MissionState).namedValues(
      column.missionId -> missionId,
      column.userId -> userId,
      column.feedback -> feedback
    ).append(sqls"ON DUPLICATE KEY UPDATE ${column.feedback} = ${column.feedback} + ${feedback}")
  }
}
