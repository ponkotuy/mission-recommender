package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class MissionState(id: Long, missionId: Int, userId: Long, isClear: Boolean, feedback: Int, notFound: Boolean)

object MissionState extends SkinnyCRUDMapperWithId[Long, MissionState] {
  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  override def defaultAlias: Alias[MissionState] = createAlias("ms")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[MissionState]): MissionState = autoConstruct(rs, n)

  def updateClear(missionId: Int, userId: Long, isClear: Boolean = true)(implicit session: DBSession) = applyUpdate {
    insert.into(MissionState).namedValues(
      column.missionId -> missionId,
      column.userId -> userId,
      column.isClear -> isClear
    ).append(sqls"ON DUPLICATE KEY UPDATE ${column.isClear} = ${isClear}")
  }

  def updateFeedback(missionId: Int, userId: Long, feedback: Int)(implicit session: DBSession) = applyUpdate {
    insert.into(MissionState).namedValues(
      column.missionId -> missionId,
      column.userId -> userId,
      column.feedback -> feedback
    ).append(sqls"ON DUPLICATE KEY UPDATE ${column.feedback} = ${column.feedback} + ${feedback}")
  }

  def updateNotFound(missionId: Int, userId: Long, notFound: Boolean = true)(implicit session: DBSession) = applyUpdate {
    insert.into(MissionState).namedValues(
      column.missionId -> missionId,
      column.userId -> userId,
      column.notFound -> notFound
    ).append(sqls"ON DUPLICATE KEY UPDATE ${column.notFound} = ${notFound}")
  }
}
