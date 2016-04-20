package models

import scalikejdbc.{WrappedResultSet, autoConstruct}
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Account(id: Long, name: String, hash: Array[Byte], salt: Array[Byte], created: Long)

object Account extends SkinnyCRUDMapperWithId[Long, Account] {
  override def idToRawValue(id: Long): Any = id
  override def rawValueToId(value: Any): Long = value.toString.toLong

  override def defaultAlias: Alias[Account] = createAlias("a")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Account]): Account = autoConstruct(rs, n)
}
