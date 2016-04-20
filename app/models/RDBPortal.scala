package models

import responses.Portal
import scalikejdbc.{WrappedResultSet, autoConstruct}
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class RDBPortal(id: Int, name: String, latitude: Double, longitude: Double, typ: String) extends Portal

object RDBPortal extends SkinnyCRUDMapperWithId[Int, RDBPortal] {
  override val tableName = "portal"

  override val useExternalIdGenerator = true

  override def idToRawValue(id: Int): Any = id
  override def rawValueToId(value: Any): Int = value.toString.toInt

  override def defaultAlias: Alias[RDBPortal] = createAlias("p")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[RDBPortal]): RDBPortal = autoConstruct(rs, n)
}
