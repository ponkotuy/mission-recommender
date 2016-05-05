package responses

import org.scalatest.FunSuite
import play.api.libs.json.Json

class PortalResponseSuite extends FunSuite {
  test("Parse dirty json") {
    import PortalResponse.responseReads
    val json =
      """{"mission":75505,"portal":[
        |[false,1,{"id":"277385","code":"af753274090644c194923c123a9e55e7.16","name":"\u4e09\u7fbd\u30d5\u30af\u30ed\u30a6","longitude":139.712746,"latitude":35.729676,"type":"p","icon ":null}],
        |[false,1,{"id":"50733","code":"a32e546576ae46d99f08b180f93c5a12.16","name":"\u6c60\u888b\u99c5\u6771\u53e3\u4ea4\u756a","longitude":139.71288,"latitude":35.729635,"type":"p","icon":null}],
        |[false,1,{"id":"277384","code":"fb1734a2214d4ecdbd54dee8f563fe3f.16","name":"\u3075\u304f\u308d\u3046\u306e\u6642\u8a08","longitude":139.713046,"latitude":35.729732,"type":"p","icon":null}],
        |[false,1,{"id":"315660","code":"60309ed33b964e7fa7d76d697d2fa9e7.16","name":"\u30a6\u30eb\u30c8\u30e9\u30de\u30f3\u30de\u30c3\u30af\u30b9","longitude":139.713957,"latitude":35.730672,"type":"p","icon":null}],
        |[false,1,{"id":"240293","code":"fbfeced2d62e4414bc26559aaf58cbf0.16","name":"\u540d\u4ee3 \u305f\u3044\u713c","longitude":139.714261,"latitude":35.73125,"type":"p","icon":null}],
        |[false,1,{"id":"17905","code":"7577f17fe170456d9a09484ff952cbb9.11","name":"\u3075\u304f\u308d\u3046\u50cf","longitude":139.715197,"latitude":35.73142,"type":"p","icon":null}],
        |[false,1,{"id":"17906","code":"4252613c0a2f4b47902e9f8d546b46c3.16","name":"\u6c60\u888b\u99c5\u524d\u90f5\u4fbf\u5c40(Ikebukuro Ekimae Post Office)","longitude":139.715086,"latitude":35.731909,"type":"p","icon":null}],
        |[false,1,{"id":"17902","code":"d1a5bb2488ec4523b2a31d0714b904ee.16","name":"\u30d3\u30c3\u30af\u30ab\u30e1\u30e9\u672c\u5e97\u30d1\u30bd\u30b3\u30f3\u9928","longitude":139.713476,"latitude":35.73159,"type":"p","icon":null}],
        |[false],
        |[false,1,{"id":"24404","code":"97072151bf654bbeb9ec169417f05ec6.11","name":"\u3044\u3051\u3075\u304f\u308d\u3046 2","longitude":139.710067,"latitude":35.731268,"type":"p","icon":null}],
        |[false,1,{"id":"50714","code":"b504a22e2f7141efb1a7dddddaeeb76b.11","name":"\u3048\u3093\u3061\u3083\u3093","longitude":139.710008,"latitude":35.73138,"type":"p","icon":null}],
        |[false,1,{"id":"50781","code":"2bf4baaca22d4af08008d518d88a95b7.16","name":"Salmon Gett er","longitude":139.70983,"latitude":35.731382,"type":"p","icon":null}],
        |[false,1,{"id":"50787","code":"90b33d187fd04634be0d55585dfa998e.11","name":"\u6c60\u888b\u30e1\u30c8\u30ed\u30dd\u30ea\u30bf\u30f3\u30b7\u30a2\u30bf\u4e00\u524d\u5674\u6c34","longitude":139.709118,"latitude":35.730262,"type":"p","icon":null}],
        |[false,1,{"id":"50785","code":"cd8b5acb45004344b994e9923a93bb68.16","name":"No.1 Star Owl","longitude":139.70935,"latitude":35.72964,"type":"p","icon":null}],
        |[false,1,{"id":"315649","code":"3fedd5a3f99d4a15a2fd993dd47a0941.16","name":"\u6c60\u888b\u5730\u540d\u3086\u304b\u308a\u306e\u6c60","longitude":139.708641,"latitude":35.728798,"type":"p","icon":null}],
        |[false,1,{"id":"50804","code":"27882ce5671e4e92bd85f8b8be242105.11","name":"\u689f\u306e\u6a39","longitude":139.708624,"latitude":35.728762,"type":"p","icon":null}]]}""".stripMargin
    val result = Json.parse(json).validate[PortalResponse].get
    assert(result.mission === 75505)
    val portal = result.portal.head
    assert(portal.id === 277385)
    assert(portal.typ === "p")
  }
}
